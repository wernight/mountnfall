package beroux.mountnfall;
import java.util.NoSuchElementException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.io.*;

/** Manage the game rules.
 * Know the current player's color, whitch move is authorized, ...
 */
final class GameLogic extends Observable implements Cloneable, Serializable
{
// Construction
	public GameLogic()
	{
	}

// Command Pattern
	/** Execute a command.
	 * @param command	A command to execute.
	 * @return True if executed.
	 */
	public boolean execute(GameCommand command)
	{
		// A move?
		if (command instanceof Move)
		{
			// Save previous state.
			return doMove((Move) command);
		}

		// Other commands?
		if (command.getClass() == NewGame.class)
			newGame( ((NewGame)command).startingPlayer );
		else if (command.getClass() == GameOver.class)
		{
			_gameOver = true;
			_winner = ((GameOver) command).winner;
		}
		else
			// Unknown command.
			return false;
		
		// Notify all observers.
		setChanged();
		notifyObservers(command);
		
		return true;
	}

// Attributes
	/** Return the.getColour() of the active player.
	 * @return The.getColour() of the current player.
	 */
	public int getCurrentPlayerColour()
	{
		return _currentPlayerColour;
	}

	public int getWinner()
	{
		return _winner;
	}

	public boolean isGameOver()
	{
		return _gameOver;
	}

	public Board getBoard()
	{
		return _board;
	}

// Operations
	/** Execute a move.
	 * Same as execute(move) but a little bit faster.
	 * @param move	Move to execute.
	 * @return True if done, false if invalid move.
	 */
	public boolean doMove(Move move)
	{
		// Check if move is valid.
		if (!isMoveValid(move))
			return false;
		
		// Execute move.
		if (move.getClass() == PileUpMove.class)
		{
			PileUpMove realMove = (PileUpMove) move;
			pileUpMove(realMove.counter, realMove.destination);
		}
		else
		{
			DepileMove realMove = (DepileMove) move;
			depileMove(realMove.tower, realMove.dx, realMove.dy);
		}

		// Change current player.
		nextPlayer();

		// Notify observers.
		setChanged();
		notifyObservers(move);
		
		// Game over?
		if (!canPlayerMove(_currentPlayerColour))
		{
			if (_currentPlayerColour == Board.Counter.Colour.WHITE)
				execute(new GameOver(Board.Counter.Colour.BLACK));
			else
				execute(new GameOver(Board.Counter.Colour.WHITE));
		}

		return true;
	}

	/** Check if the move is valid.
	 * @return True if the move is authorized.
	 */
	public boolean isMoveValid(Move move)
	{
		// Game already over?
		if (_gameOver)
			return false;
		
		if (move.getClass() == PileUpMove.class)
		{
			PileUpMove realMove = (PileUpMove) move;
			return isPileUpMoveValid(realMove.counter, realMove.destination);
		}
		else if (move.getClass() == DepileMove.class)
		{
			DepileMove realMove = (DepileMove) move;
			return isDepileMoveValid(realMove.tower, realMove.dx, realMove.dy);
		}

		// BAD! Should not happen.
		return false;
	}
	
	/** Clone the game state.
	 * The clone will have no observers.
	 */
	public Object clone()
	{
		// The Observable is a non-cloneable superclass, so...
		GameLogic clone = new GameLogic();

		clone._board = (Board) _board.clone();
		clone._currentPlayerColour = _currentPlayerColour;
		clone._gameOver = _gameOver;
		clone._winner = _winner;
		
		return clone;
	}

	public boolean equals(Object o)
	{
		if (o == null || o.getClass() != GameLogic.class)
			return false;

		GameLogic logic = (GameLogic) o;
		
		return _currentPlayerColour == logic.getCurrentPlayerColour() &&
			_gameOver == logic.isGameOver() &&
			(!_gameOver || _winner == logic.getWinner()) &&
			_board.equals(logic.getBoard());
	}
	
	/** Find possible moves of the current player.
	 * All the moves returned should be valid but they
	 * are not necessarily valid. But if a move is valid, then
	 * it is in the list returned (no valid move isn't in the list).
	 * @return A list of all possible moves of the current player.
	 */
	public List getAllPossibleMoves()
	{
		ArrayList list = new ArrayList(50);

		for (int row=Board.rows()-1; row>=0; --row)
		for (int col=Board.columns()-1; col>=0; --col)
		{
			Board.Square src = new Board.Square(row, col);
			Board.Counter srcCounter = _board.getCounter(src);
			
			// Get the next squares where we have some counters.
			if (srcCounter.getColour() != _currentPlayerColour)
				continue;

			// A counter? -> Pile up
			if (srcCounter.getCount() == 1)
			{
				// For every possible pile up direction...
				for (int dx=-1; dx<=1; ++dx)
					for (int dy=-1; dy<=1; ++dy)
					{
						if (dx == 0 && dy == 0)
							continue;
						
						// Pile up until it's no more possible in this direction.
						Board.Square dest;
						for (dest=src.shift(dy, dx); dest.isInsideBoard(); dest=dest.shift(dy, dx))
						{
							Board.Counter c = _board.getCounter(dest);
							if (c.getCount() == 0 ||
								c.getColour() != _currentPlayerColour)
								break;	// Impossible to pile up there.

							// Add move.
							list.add(new PileUpMove(src, dest));

							// Last possible move?
							if (c.getCount() > 1)
								break;
						}
					}
			}
			// A tower? -> Depile
			else if (srcCounter.getCount() > 1)
			{
				// For every possible pile up direction...
				for (int dx=-1; dx<=1; ++dx)
					for (int dy=-1; dy<=1; ++dy)
					{
						if (dx == 0 && dy == 0)
							continue;
						
						// Add move.
						if (src.shift(dy, dx).isInsideBoard())
							list.add(new DepileMove(src, dx, dy));
					}
			}
		}

		return list;
	}
	
// Implementation
	/** Start a new game.
	 * The white player starts the game.
	 */
	private void newGame(int startingPlayer)
	{
		Board.SquaresIterator	iter = new Board.SquaresIterator();
		
		_board.clear();
		while (iter.hasNext())
		{
			Board.Square sq = iter.next();

			if ((Board.rows()-1-sq.row()) + sq.column() < 5)
				_board.setCounter(sq, Board.whiteCounter);
			else if (sq.row() + (Board.columns()-1-sq.column()) < 5)
				_board.setCounter(sq, Board.blackCounter);
		}
		
		_currentPlayerColour = startingPlayer;
		_gameOver = false;
	}

	private boolean canPlayerMove(int playerColour)
	{
		// Check all possible movement to see if one movement is possible.
		for (int row=Board.rows()-1; row>=0; --row)
		for (int col=Board.columns()-1; col>=0; --col)
		{
			Board.Square src = new Board.Square(row, col);
			Board.Counter srcCounter = _board.getCounter(src);
				
			// Must be a counter of the current player
			if (srcCounter.getColour() != playerColour)
				continue;

			// A single counter?
			if (srcCounter.getCount() == 1)
			{
				// Check if pile up possible.
				for (int dx=-1; dx<=1; ++dx)
					for (int dy=-1; dy<=1; ++dy)
					{
						if (dx == 0 && dy == 0)
							continue;
						
						Board.Square arroundSq = src.shift(dy, dx);
						if (arroundSq.isInsideBoard())
						{
							Board.Counter arroundC = _board.getCounter(arroundSq);
							if (arroundC.getCount() > 0 && arroundC.getColour() == playerColour)
								return true;// Depiling possible
						}
					}
			}
			// A tower?
			else if (srcCounter.getCount() > 1)

			{
				// Check if depiling is possible.
				for (int dx=-1; dx<=1; ++dx)
					for (int dy=-1; dy<=1; ++dy)
					{
						if (dx == 0 && dy == 0)
							continue;

						// Check if depiling the tower is valid.
						int count = srcCounter.getCount();
						for (Board.Square dest=src.shift(dy,dx); --count>0; dest=dest.shift(dy,dx))
						{
							if (!dest.isInsideBoard())
								break;	// Square is out of board.
							
							// We are not allowed to pile up on our towers.
							Board.Counter c = _board.getCounter(dest);
							if (c.getCount() > 1 && c.getColour() == playerColour)
								break;	// Can not depile on one of our towers.
						}
						if (count == 0)	// If all the counters could be depiled...
							return true;	// Move if valid
					}
			}							
		}
		return false;
	}
	
	private boolean isPileUpMoveValid(Board.Square src, Board.Square dest)
	{
		Board.Counter destCounter = _board.getCounter(dest);
		int dy = dest.row() - src.row(),
			dx = dest.column() - src.column();

		// Are we trying to move a counter not of our colour?
		if (destCounter.getColour() != _currentPlayerColour)
			return false;
		
		// Invalid direction?
		if ((dx == 0 && dy == 0) ||
			(dx != 0 && dy != 0 && Math.abs(dx) != Math.abs(dy)))
			return false;

		// Normalize direction
		if (dx > 0)	dx = 1; else if (dx < 0) dx = -1;
		if (dy > 0)	dy = 1; else if (dy < 0) dy = -1;

		// Are we piling up on a tower or a counter?
		if (destCounter.getCount() < 1)
			return false;
		
		// Are we piling up single counters of our colour?
		Board.Square sq;
		for (sq = src; !sq.equals(dest); sq = sq.shift(dy, dx))
		{
			if (!sq.isInsideBoard())
				return false;	// Square is out of board.
			Board.Counter c = _board.getCounter(sq);
			if (c.getCount() != 1 || c.getColour() != destCounter.getColour())
				return false;	// Not a single counter or our colour.
		}

		return true;
	}
	
	private void pileUpMove(Board.Square src, Board.Square dest)
	{
		int dx, dy;

		// Pile up direction.
		dy = dest.row() - src.row();
		if (dy > 0)	dy = 1; else if (dy < 0) dy = -1;
		
		dx = dest.column() - src.column();
		if (dx > 0)	dx = 1; else if (dx < 0) dx = -1;

		// Do the pile up.
		int count = 0;
		for (; !src.equals(dest); src = src.shift(dy, dx))
		{
			// Increment destination tower height.
			Board.Counter c = _board.getCounter(src);
			count += c.getCount();

			// No more counter in this square
			_board.setCounter(src, Board.emptyCounter);
		}

		// Increase destination tower's height.
		Board.Counter c = _board.getCounter(dest);
		_board.setCounter(dest, new Board.Counter(c.getCount() + count, c.getColour()));
	}
	
	private boolean isDepileMoveValid(Board.Square src, int dx, int dy)
	{
		Board.Counter tower = _board.getCounter(src);

		// Are we trying to move a tower not of our colour,
		// or a tower that is not a tower?
		if (tower.getColour() != _currentPlayerColour ||
			tower.getCount() <= 1)
			return false;
		
		// Invalid direction?
		if (dx == 0 && dy == 0)
			return false;
		
		// Check if depiling the tower is valid.
		for (int i=1; i<tower.getCount(); ++i)
		{
			src = src.shift(dy, dx);
			if (!src.isInsideBoard())
				return false;	// Square is out of board.
			
			// We are not allowed to pile up on our towers.
			Board.Counter c = _board.getCounter(src);
			if (c.getCount() > 1 && c.getColour() == tower.getColour())
				return false;	// Can not depile on one of our towers.
		}
	
		return true;
	}

	private void depileMove(Board.Square src, int dx, int dy)
	{
		// Do the depiling.
		Board.Counter tower = _board.getCounter(src);
		
		// Get a single counter of same colour as the tower.
		Board.Counter singleCounter;
		if (tower.getColour() == Board.Counter.Colour.WHITE)
			singleCounter = Board.whiteCounter;
		else
			singleCounter = Board.blackCounter;

		// Tower -> Counter.
		_board.setCounter(src, singleCounter);
		
		// Depile the tower.
		for (int i=1; i<tower.getCount(); ++i)
		{
			src = src.shift(dy, dx);
			
			Board.Counter c = _board.getCounter(src);
			if (c.getColour() == tower.getColour())
				c = new Board.Counter(c.getCount() + 1, c.getColour());
			else
				c = singleCounter;
			_board.setCounter(src, c);
		}
	}

	private void nextPlayer()
	{
		if (_currentPlayerColour == Board.Counter.Colour.WHITE)
			_currentPlayerColour = Board.Counter.Colour.BLACK;
		else //if (_currentPlayerColour == Board.Counter.Colour.BLACK)
			_currentPlayerColour = Board.Counter.Colour.WHITE;
	}

	// Game state info
	private Board	_board = new Board();
	private int		_currentPlayerColour;
	private boolean	_gameOver = true;
	private int		_winner;
}
