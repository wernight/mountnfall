package beroux.mountnfall;
import java.util.List;
import java.util.Random;
import java.lang.Thread;

/** Recall AlphaBeta Acceleration.
 * This algo is an upgrade of the AlphaBeta (MinMax with Beta cut) algorithm.
 * It was invented by Werner BEROUX on April, 26th 2005.
 *
 * AlphaBeta can be seen as a tree. Let's say that AlphaBeta found a way
 * leading to a Beta cut. Changing only one move in the tree will propably
 * lead again to a Beta cut. So by recalling the last way that lead to a
 * Beta cut, it tries directly the way that has the most probabilities of
 * returning instant (even for a very high depth).
 *
 * Let's take an example for the Connect-4 game:
 * The human has 3 connected white counters in column 4. The A.I. is the black
 * player. Now the A.I. try by placing 1st counter in column 1. Then in goes on
 * as usual until the 2nd counter (human's counter) is played in column 4. It
 * leads to a win and so Beta value is modified.
 *
 * Without acceleration, the A.I. would then try 1st counter in column 2 with
 * almost the same thinking as for the first counter. Of course the Beta value
 * is lower so more will be cut. It will still try by putting 2nd counter in
 * column 1, then 3rd in column 1, then ... and so on. That can be really a lot.
 *
 * With the acceleration, the AI will first try the same way as last way that
 * has lead to a Beta-cut on the give depth as before: 2nd counter in column 4.
 * Instant return.
 *
 * Result: Time requiere divided by a value depending on the number of moves
 * available each turn. That means an exponential speed increase. So a deeper
 * thinking possible. And, even better, Alpha-Beta takes a really random time
 * to think (less random if you can find the best move quickly), but
 * Recall AlphaBeta almost always takes a constant time to think (like MinMax).
 */

class AiPlayer extends Player implements java.io.Serializable
{
	public static interface Level
	{
		public static final int
			EASY = 1,
			MEDIUM = 2,
			HARD = 3;
	}
	
// Construction
	public AiPlayer(int level)
	{
		super("Computer"+level);
		_level = level;
	}
	
// Player
	// Tell the player that it's his turn to play.
	public void onPlay(GameLogic logic)
	{
		_ai = new AiThread(_level, logic);
		_ai.start();
	}
	
	// Request the player's move choice. Can be null if player hasn't choosen yet.
	public Move getPlayed()
	{
		return _ai.getResult();
	}

// Implementation
	private static final class AiThread extends Thread
	{
		public AiThread(int level, GameLogic logic)
		{
			_level = level;
			_logic = logic;
			_myColour = logic.getCurrentPlayerColour();
			_move = null;	// Didn't chosed a move.
		}

		private void setResult(Move move)
		{
			_move = move;
		}

		public synchronized Move getResult()
		{
			return _move;
		}
		
		public void run()
		{
			Move chosenMove = null;
			
			// Get a list of possible moves.
			List moves = _logic.getAllPossibleMoves();
			
			// Evaluate the number of possible actions each turn.
			float averagePossibleMoves = averagePossibleMoves(_logic, 2);
			
			// AI level.
			int maxDifference;
			int maxDepth;
			switch (_level)
			{
			case Level.EASY:
				if (_rand.nextInt()%5 != 0)
					maxDifference = Math.abs(_rand.nextInt()%200);
				else
					maxDifference = 0;
				maxDepth = (int) Math.round(Math.log(100) / Math.log(averagePossibleMoves));
				break;
				
			case Level.MEDIUM:
				if (_rand.nextInt()%5 != 0)
					maxDifference = Math.abs(_rand.nextInt()%10)*Math.abs(_rand.nextInt()%10);
				else
					maxDifference = 0;
				maxDepth = (int) Math.round(Math.log(10000) / Math.log(averagePossibleMoves));
				break;
				
			default:	// HARD
				// No maxDifference
				maxDifference = 0;
				maxDepth = (int) Math.round(Math.log(100000) / Math.log(averagePossibleMoves));
			}
			if (maxDepth > 6)
				maxDepth = 6;
			System.out.println("Evaluating "+(maxDepth+1)+" turns.");

			// Initialize
			int moveId = 0;
			_recallMoves = new Move[maxDepth];
			for (int i=0; i<_recallMoves.length; ++i)
				_recallMoves[i] = (Move) moves.get(0);

			// Try to guess the best move by making a superficial evaluation (not deept)
			int max = -INFINITE;
			int alpha = -INFINITE;
			for (int i=0; i< moves.size(); ++i)
			{
				// If the move is valid...
				GameLogic clone = (GameLogic) _logic.clone();
				if (clone.doMove((Move) moves.get(i)))
				{
					// Evaluate the move.
					int eval = alphaBeta(clone, maxDepth/2, alpha, +INFINITE);
					if (eval > max)
					{
						moveId = i;
						max = eval;

						if (max > alpha)
							alpha = max;
					}
				}
			}

			// For each possible move...
			int evals[] = new int[moves.size()];
			max = -INFINITE;
			alpha = -INFINITE;
			boolean moveEvaluated[] = new boolean[moves.size()];
			int movesToEvaluate = moves.size();
			while (movesToEvaluate > 0)
			{
				// Choose a random move that hasn't been evaluated
				// (NOTE: On first loop, moveId will no be movified)
				while (moveEvaluated[moveId])
					moveId = Math.abs(_rand.nextInt()) % moves.size();
				--movesToEvaluate;
				moveEvaluated[moveId] = true;

				// If the move is valid...
				GameLogic clone = (GameLogic) _logic.clone();
				if (clone.doMove((Move) moves.get(moveId)))
				{
					// Evaluate the move.
					evals[moveId] = alphaBeta(clone, maxDepth, alpha, +INFINITE);
					if (evals[moveId] > max)
					{
						max = evals[moveId];
						chosenMove = (Move) moves.get(moveId);

						// NOTE: Alpha-Beta gives a true evaluation of each action
						// only if Alpha = -INFINITE and Beta = +INFINITE.
						// Since we need true evaluation when choosing a random
						// column, we can't use alpha-cut here.
						if (maxDifference == 0 && max > alpha)
							alpha = max;
					}
				}
				else
					evals[moveId] = -INFINITE;
			}

			// Find a more or less random best move.
			if (maxDifference > 0)
			{
				chosenMove = null;
				max = -INFINITE;
				for (int count=10*moves.size(); count>0; --count)
				{
					int i = Math.abs(_rand.nextInt()%moves.size());
					if (evals[i] > max)
					{
						max = evals[i] - maxDifference;
						chosenMove = (Move) moves.get(i);
					}
				}
			}
			
			// Return move.
			setResult(chosenMove);
		}

		/** Calculate average number of valid moves each turn.
		 * @param logic		Game logic state to evaluate.
		 * @param depth		Evaluation depth. Result is more precise when it's a pair number.
		 */
		private float averagePossibleMoves(GameLogic logic, int depth)
		{
			int sum = 0;
			int count = 0;
			
			// Game over?
			if (logic.isGameOver())
				return 0;

			// Get possible moves list.
			List moves = logic.getAllPossibleMoves();

			// Maximum depth reached?
			if (--depth <= 0)
				return moves.size();

			// For each possible move...
			GameLogic clone = (GameLogic) logic.clone();
			for (int i=moves.size()-1; i>=0; --i)
				// If the move is valid...
				if (clone.doMove((Move) moves.get(i)))
				{
					// Evaluate number of possible moves next turn.
					sum += averagePossibleMoves(logic, depth);
					++count;
				}
			return ((float)moves.size() + (float)sum/count)*0.5f;
		}
		
		private final int alphaBeta(GameLogic logic, int depth, int alpha, int beta)
		{
			// Game over?
			if (logic.isGameOver())
				if (logic.getWinner() == _myColour)
					return 10000;	// A.I. won.
				else
					return -10000;	// A.I. lost.

			// Maximum depth reached?
			if (--depth < 0)
				return evaluate(logic);

			if (logic.getCurrentPlayerColour() == _myColour)
			{
				int max = -INFINITE;
				GameLogic clone = (GameLogic) logic.clone();

				// Recall Alpha-Beta Acceleration
				// Copyright (c) Werner BEROUX
				if (clone.doMove(_recallMoves[depth]))
				{
					max = alphaBeta(clone, depth, alpha, beta);
					
					if (max >= beta)
						return max;
					if (max > alpha)
						alpha = max;

					// Undo.
					clone = (GameLogic) logic.clone();
				}

				// For each possible move...
				List moves = logic.getAllPossibleMoves();
				for (int i=moves.size()-1; i>=0; --i)
				{
					// Don't need to check twice the same move
					if (_recallMoves[depth].equals(moves.get(i)))
						continue;
					
					// If the move is valid...
					if (clone.doMove((Move) moves.get(i)))
					{
						// Find the best move.
						int value = alphaBeta(clone, depth, alpha, beta);
						if (value > max)
						{
							max = value;
							if (max >= beta)
							{
								_recallMoves[depth] = (Move) moves.get(i);
								break;
							}
							if (max > alpha)
								alpha = max;
						}

						// Undo.
						clone = (GameLogic) logic.clone();
					}
				}

				return max;
			}
			else
			{
				int min = +INFINITE;
				GameLogic clone = (GameLogic) logic.clone();

				// Recall Alpha-Beta Acceleration
				// Copyright (c) Werner BEROUX
				if (clone.doMove(_recallMoves[depth]))
				{
					// Find the best move.
					min = alphaBeta(clone, depth, alpha, beta);

					if (min <= alpha)
						return min;
					if (min < beta)
						beta = min;

					// Undo.
					clone = (GameLogic) logic.clone();
				}

				// For each possible move...
				List moves = logic.getAllPossibleMoves();
				for (int i=moves.size()-1; i>=0; --i)
				{
					// Don't need to check twice the same move
					if (_recallMoves[depth].equals(moves.get(i)))
						continue;
					
					// If the move is valid...
					if (clone.doMove((Move) moves.get(i)))
					{
						// Find the best move.
						int value = alphaBeta(clone, depth, alpha, beta);
						if (value < min)
						{
							min = value;
							if (min <= alpha)
							{
								_recallMoves[depth] = (Move) moves.get(i);
								break;
							}
							if (min < beta)
								beta = min;
						}

						// Undo.
						clone = (GameLogic) logic.clone();
					}
				}

				return min;
			}
		}

		private final int evaluate(GameLogic logic)
		{
			Board board = logic.getBoard();
			
			// Count number of counters the AI has minus the those of opponent.
			int diff = 0;
			for (int row=Board.rows()-1; row>=0; --row)
				for (int col=Board.columns()-1; col>=0; --col)
				{
					Board.Counter c = board.getCounter(new Board.Square(row, col));
					if (c.getColour() == _myColour)
						diff += c.getCount();
					else
						diff -= c.getCount();
				}

			// Count number of available moves.
			int moves = logic.getAllPossibleMoves().size();

			if (logic.getCurrentPlayerColour() == _myColour)
				return 100*diff + moves;
			else
				return 100*diff - moves;
		}

		private static final int INFINITE = 0xFFFF;

		private int 		_level;
		private GameLogic 	_logic;
		private int 		_myColour;
		private Move		_move;
		private static Random	_rand = new Random();
		private Move		_recallMoves[];
	}
	
	private int			_level;
	private transient AiThread	_ai;
}
