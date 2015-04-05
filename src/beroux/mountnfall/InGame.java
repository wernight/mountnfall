package beroux.mountnfall;
import beroux.game.*;
import java.awt.Graphics2D;
import java.util.Stack;
import java.io.*;

public class InGame extends SceneView
{
// Construction
	public InGame()
	{
		_state = State.LOADING;
		_startingPlayer = Board.Counter.Colour.WHITE;
	}

	public boolean create(Player whitePlayer, Player blackPlayer, Sprite title)
	{
		_whitePlayer = whitePlayer;
		_blackPlayer = blackPlayer;
		_title = title;
		return true;
	}
	
	public boolean loadGame(File file)
	{
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
			
			_startingPlayer = ((Integer) in.readObject()).intValue();
			_logic = (GameLogic) in.readObject();
			_gui.setGameLogic(_logic);
			// TODO _undo = (Stack) out.readObject();
			
			in.close();
			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}

// SceneView
	/** Check if scene is ready to be entered.
	 * @return False if the scene hasn't been initialized and cannot be entered.
	 */
	public boolean isInitilialized()
	{
		return _whitePlayer != null &&
		   _blackPlayer != null;
	}
	
	public void onEntry()
	{
		assert _state == State.LOADING;

		_undo = new Stack();
		_gui = new GUI(_logic);
		_gui.setTitle(_title);
		
		// Start new game
		_logic.execute(new NewGame(_whitePlayer.getName(), _blackPlayer.getName(), _startingPlayer));

		// Start 1st game turn
		startPlaying();
		_state = State.PLAY;
	}

	public void onExit()
	{
		_gui = null;
		_undo = null;
		_state = State.LOADING;
	}
	
	public void update(float dt)
	{
		if (_state == State.PLAY)
			play();

		// Update GUI
		_gui.update(dt);
	}

	public void paint(Graphics2D g)
	{
		// Displayer GUI
		_gui.paint(g);
	}

	/** Called when a mouse button press is detected.
	 * @param x			The X coordinates of the mouse at press time.
	 * @param y			The Y coordinates of the mouse at press time.
	 * @param button	The mouse button pressed.
	 */
	public void onMouseDown(int x, int y, int button)
	{
		switch (_state)
		{
		case State.PLAY:
			// Input player move?
			if (_inputPlayerMove)
			{
				// Get clicked square (if some)
				Board.Square clickedSquare = _gui.getBoardView().getClickedSquare(x, y);

				// Cancel first click?
				if (clickedSquare == null || button != GameCanvas.MouseButton.LEFT)
				{
					if (_firstSelectedSquare != null)
					{
						// Cancel first click.
						_gui.getBoardView().setHighlight(_firstSelectedSquare, false);
						_firstSelectedSquare = null;
						_gui.getBoardView().clearHighlight();
						_gui.getBoardView().assign(_logic); // Display the current board.
						onMouseMove(0, 0);
						onMouseMove(x, y);
					}
				}
				else // Left click on a square
				{
					// If the player has NOT chosen a starting square
					if (_firstSelectedSquare == null)
					{
						// Is it one of the current player's counter/tower?
						Board.Counter c =_logic.getBoard().getCounter(clickedSquare);
						if (c.getCount() > 0 && c.getColour() == _logic.getCurrentPlayerColour())
						{
							_firstSelectedSquare = clickedSquare;

							// Highlight source square.
							_gui.getBoardView().setHighlight(_firstSelectedSquare, true);
							// Show a cursor explaining move.
							if (c.getCount() == 1)
								Screen.getInstance().setCursor( _pileUpCursor );
							else
								Screen.getInstance().setCursor( _depileCursor );
						}
					}
					else
					{
						// Find the move wished by the user.
						Board.Counter c = _logic.getBoard().getCounter(_firstSelectedSquare);
						if (c.getCount() == 1)
							// Pile up
							_playerMove = new PileUpMove(_firstSelectedSquare, clickedSquare);
						else
							// Depile
							_playerMove = new DepileMove(_firstSelectedSquare, clickedSquare);
						
						// If the move is valid, input is done.
						if (_logic.isMoveValid(_playerMove))
						{
							_inputPlayerMove = false;
							Screen.getInstance().setCursor( _defaultCursor );
						}
						
						// Cancel first click.
						_gui.getBoardView().setHighlight(_firstSelectedSquare, false);
						_firstSelectedSquare = null;
						_gui.getBoardView().clearHighlight();
						_gui.getBoardView().assign(_logic); // Display the current board.
						onMouseMove(0, 0);
						onMouseMove(x, y);
					}
				}
			}
			break;

		case State.GAME_OVER:
			Screen.getInstance().setCursor( _defaultCursor );
			sendMessageToController("game over");
			break;

		case State.UNDO:
			Screen.getInstance().setCursor(_defaultCursor);
			_state = State.PLAY;
			startPlaying();
			break;
		}
	}

	/** Mouse motion.
	 */
	public void onMouseMove(int x, int y)
	{
		// Input player move?
		if (_state == State.PLAY && _inputPlayerMove)
		{
			// Get clicked square (if some)
			Board.Square overSquare = _gui.getBoardView().getClickedSquare(x, y);
			if (overSquare==null?_lastSquareOver!=null:!overSquare.equals(_lastSquareOver))
			{
				// Didn't select a source square?
				if (_firstSelectedSquare == null)
				{
					// Display a hand cursor when cursor is in board.
					if (overSquare != null && _logic.getBoard().getCounter(overSquare).getColour() == _logic.getCurrentPlayerColour())
						Screen.getInstance().setCursor( _handCursor );
					else
						Screen.getInstance().setCursor( _defaultCursor );
				}
				// Selected a source square (_firstSelectedSquare != null).
				else
				{
					// Highlight source square.
					_gui.getBoardView().clearHighlight();
					_gui.getBoardView().setHighlight(_firstSelectedSquare, true);

					// Display a preview of the next move.
					if (overSquare == null)
					{
						// Display the real current board (not a move preview).
						_gui.getBoardView().assign(_logic);
						
						// Display the default cursor.
						Screen.getInstance().setCursor( _defaultCursor );
					}
					else
					{
						// Find the move wished by the user.
						Board.Counter c = _logic.getBoard().getCounter(_firstSelectedSquare);
						if (c.getCount() == 1)
							// Pile up
							_playerMove = new PileUpMove(_firstSelectedSquare, overSquare);
						else
							// Depile
							_playerMove = new DepileMove(_firstSelectedSquare, overSquare);
						
						// If the move is valid.
						if (_logic.isMoveValid(_playerMove))
						{
							// Show a preview of the move.
							GameLogic logicPreview = (GameLogic) _logic.clone();
							logicPreview.execute(_playerMove);
							_gui.getBoardView().assign(logicPreview);

							// Highlight destination square.
							_gui.getBoardView().setHighlight(overSquare, true);
						}
						else
						{
							// Display the current board.
							_gui.getBoardView().assign(_logic);
						}
					}
				}
				
				_lastSquareOver = overSquare;
			}
		}
	}

// Attributes
	/** Return the player move.
	 * @return A Move or null if the player has not chosen yet.
	 */
	public Move getPlayerMove()
	{
		// Waiting for player input?
		if (_inputPlayerMove)
			return null;
		else
			return _playerMove;
	}

	/** Return the winning player's colour.
	 * @return Board.Counter.Colour.BLACK or Board.Counter.Colour.WHITE.
	 */
	public int getWinner()
	{
		return _logic.getWinner();
	}

	public Board getBoard()
	{
		return _logic.getBoard();
	}
	
// Operations
	public void setStartingPlayer(int player)
	{
		_startingPlayer = player;
	}

	/** Wait for player to input a move.
	 * The player move can be retrieved by calling getPlayerMove.
	 */
	public void inputPlayerMove()
	{
		_inputPlayerMove = true;
		_firstSelectedSquare = null;
		_lastSquareOver = null;
		_playerMove = null;
	}

	public void undoLastMove()
	{
		if (!_undo.empty())
		{
			// Load previous state
			_logic = (GameLogic) _undo.pop();
			_gui.setGameLogic(_logic);

			Screen.getInstance().setCursor(_goCursor);
			_state = State.UNDO;
		}
	}

	public boolean saveGame(File file)
	{
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
			
			out.writeObject(new Integer(_startingPlayer));
			out.writeObject(_logic);
			// TODO _undo = (Stack) out.readObject();
		
			out.close();
			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}

// Implementation
	private interface State
	{
		public static final int 
				LOADING = 0,
				PLAY = 1,
				UNDO = 2,
				GAME_OVER = 3;
	}

	private void play()
	{
		// Check if player has moved.
		Move move;
		if (getCurrentPlayer() instanceof HumanPlayer)
			move = getPlayerMove();
		else
			move = getCurrentPlayer().getPlayed();
		if (move != null)
		{
			if (validateMove(move))
			{
				System.out.println("Player moved: "+move);
				executeMove(move);
		
				if (checkGameOver())
					return;
			}

			// Ask current player to play
			startPlaying();
		}
	}

	private void startPlaying()
	{
		// Tell current player to move
		// (or move again if player didn't changed).
		if (getCurrentPlayer() instanceof HumanPlayer)
			inputPlayerMove();
		getCurrentPlayer().onPlay((GameLogic) _logic.clone());

		_state = State.PLAY;
	}

	private boolean validateMove(Move move)
	{
		// Check if it's valid.
		return _logic.isMoveValid(move);
	}

	private void executeMove(Move move)
	{
		// Save state for undo
		_undo.push(_logic.clone());
		
		// Highlight the source and destination square.
		_gui.getBoardView().clearHighlight();
		Board.Square src, dest;
		if (move.getClass() == PileUpMove.class)
		{
			src = ((PileUpMove) move).counter;
			dest = ((PileUpMove) move).destination;
		}
		else // Depile move
		{
			DepileMove depileMove = (DepileMove) move;
			src = depileMove.tower;
			int count = _logic.getBoard().getCounter(src).getCount() - 1;
			dest = src.shift(count*depileMove.dy, count*depileMove.dx);
		}
		_gui.getBoardView().clearHighlight();
		_gui.getBoardView().setHighlight(src, true);
		_gui.getBoardView().setHighlight(dest, true);

		// Execute the move.
		_logic.doMove(move);
	}
	
	private boolean checkGameOver()
	{
		// Game over?
		if (_logic.isGameOver())
		{
			System.out.println("Yes");
			_state = State.GAME_OVER;
			Screen.getInstance().setCursor( _goCursor );
			return true;
		}
		return false;
	}

	private Player getCurrentPlayer()
	{
		switch (_logic.getCurrentPlayerColour())
		{
		case Board.Counter.Colour.WHITE:		return _whitePlayer;
		case Board.Counter.Colour.BLACK:		return _blackPlayer;
		default:				return null;
		}
	}
	
	private int			_startingPlayer;
	private Sprite		_title;
	private GUI			_gui;
	private Player		_whitePlayer;
	private Player		_blackPlayer;
	private GameLogic	_logic = new GameLogic();
	private Cursor		_defaultCursor = new Cursor(Cursor.DEFAULT),
						_handCursor = new Cursor("HandCursor", 6, 5),
						_pileUpCursor = new Cursor("PileUpCursor", 15, 30),
						_depileCursor = new Cursor("DepileCursor", 30, 16),
						_goCursor = new Cursor("GO", 16, 16);
	private Stack		_undo;
	private int	_state;
	
	// Input player move
	private boolean			_inputPlayerMove;
	private Board.Square	_firstSelectedSquare,
							_lastSquareOver;
	private Move			_playerMove;
}
