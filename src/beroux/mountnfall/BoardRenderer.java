package beroux.mountnfall;
import beroux.game.*;
import java.util.Observable;
import java.util.Observer;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics2D;

class BoardRenderer implements IGameObject
{
	public BoardRenderer(GameLogic logic)
	{
		_board = logic.getBoard();
		
		_boardImage = new Sprite("Board");
		_boardImage.defineReferencePixel(_boardImage.getWidth()/2, _boardImage.getHeight()/2);
		_boardImage.setRefPixelPosition(Screen.width()/2, Screen.height()/2);
		
		_whiteCounter = new Sprite("WhiteCounter");
		_whiteCounter.defineReferencePixel(
				_whiteCounter.getWidth()/2,
				_whiteCounter.getHeight()/2);
		
		_blackCounter = new Sprite("BlackCounter");
		_blackCounter.defineReferencePixel(
				_blackCounter.getWidth()/2, 
				_blackCounter.getHeight()/2);

		// Square highlighting
		_whiteSquareHL = new Sprite("BoardWhiteHL");
		_whiteSquareHL.defineReferencePixel(
				_whiteSquareHL.getWidth()/2, 
				_whiteSquareHL.getHeight()/2);
		
		_blackSquareHL = new Sprite("BoardBlackHL");
		_blackSquareHL.defineReferencePixel(
				_blackSquareHL.getWidth()/2, 
				_blackSquareHL.getHeight()/2);

		_highlightSquare = new boolean[Board.rows()][Board.columns()];

		// Font
		_font = new GameFont(
			new Font("Dialog", Font.PLAIN, 26),
			Color.LIGHT_GRAY);

		// Observe board changes
		logic.addObserver(_observer);
}

// IGameObject
	public void update(float dt)
	{
	}

	public void paint(Graphics2D g)
	{
		// Draw board
		_boardImage.paint(g);

		// Hightlight square & draw counters;
		// we won't use the SquaresIterator because we must render the counters in a specific order.
		for (int row=0; row<Board.rows(); ++row)
			for (int col=Board.columns()-1; col>=0; --col)
			{
				Sprite sprite;
				
				// Highlight squares?
				if (_highlightSquare[row][col])
				{
					// Colour
					if ((row+col) % 2 == 0)
						sprite = _whiteSquareHL;
					else
						sprite = _blackSquareHL;
					
					// Display
					int x = Math.round(GRID_LEFT + (float)col/(Board.columns()-1)*GRID_WIDTH),
						y = Math.round(GRID_TOP + (float)row/(Board.rows()-1)*GRID_HEIGHT);
					sprite.setRefPixelPosition(x, y);
					sprite.paint(g);
				}

				// Draw counter?
				Board.Counter c = _board.getCounter(new Board.Square(row, col));
				if (c.getCount() > 0)
				{
					// Colour
					if (c.getColour() == Board.Counter.Colour.WHITE)
						sprite = _whiteCounter;
					else
						sprite = _blackCounter;

					// Display counter
					int x = Math.round(GRID_LEFT + (float)col/(Board.columns()-1)*GRID_WIDTH),
						y = Math.round(GRID_TOP + (float)row/(Board.rows()-1)*GRID_HEIGHT);
					for (int i=0; i<c.getCount(); ++i)
					{
						sprite.setRefPixelPosition(x, y + i*ROCKS_TRANSLATION_Y);
						sprite.paint(g);
					}

					// Display number of counter
					if (c.getCount() > 1)
					{
						Screen.getInstance().drawString(
							_font,
							""+c.getCount(),
							x - _font.stringWidth(""+c.getCount())*0.5f,
							y + 10.0f + (c.getCount()-1)*ROCKS_TRANSLATION_Y);
					}
				}
			}
	}
	
// Attribtes
	/** Highlight a square or don't.
	 */
	public void setHighlight(Board.Square sq, boolean highlight)
	{
		_highlightSquare[sq.row()][sq.column()] = highlight;
	}

// Operations
	public void assign(GameLogic logic)
	{
		_board = logic.getBoard();
	}

	/** Return the square under cursor position.
	 * @param x		Cursor's X coord.
	 * @param y		Cursor's Y coord.
	 * @return A square or null if the cursor is not under a cursor.
	 */
	public Board.Square getClickedSquare(int x, int y)
	{
		// Get board row and column under screen coordinates.
		float row = (y-GRID_TOP) / GRID_HEIGHT * (Board.rows()-1) + 0.5f,
			column = (x-GRID_LEFT) / GRID_WIDTH * (Board.columns()-1) + 0.5f;
		if (row < 0.0f || column < 0.0f)	// Outside the board.
			return null;

		// Check that the square is inside the board.
		Board.Square clickedSquare = new Board.Square((int)row, (int)column);
		if (!clickedSquare.isInsideBoard())
			return null;

		return clickedSquare;
	}

	/** Don't highlight any square.
	 */
	public void clearHighlight()
	{
		for (int row=Board.rows()-1; row>=0; --row)
			for (int col=Board.columns()-1; col>=0; --col)
				_highlightSquare[row][col] = false;
	}
	
// Implementation
	private class GameLogicObserver implements Observer
	{
		/** This method is called whenever the observed object is changed.
		 * @param o		the observable object.
		 * @param arg	an argument passed to the notifyObservers method.
		 */
		public void update(Observable o, Object arg)
		{
			GameCommand message = (GameCommand) arg;
		}
	}

	private final static float	// Center position of the top left board square.
						   		GRID_LEFT = 143.0f,								
								GRID_TOP = 143.0f,
								// Width & Height in to the center position of the bottom right board square.
								GRID_WIDTH = 350.0f,
								GRID_HEIGHT = 348.0f;
	private final static int ROCKS_TRANSLATION_Y = -5;
	
	private final GameLogicObserver	_observer = new GameLogicObserver();
	private final GameFont 	_font;
	private Board	_board;
	private Sprite	_boardImage,
					_whiteCounter,
					_blackCounter;

	// Square highlighting
	private Sprite	_whiteSquareHL,
					_blackSquareHL;
	private boolean _highlightSquare[][];
}
