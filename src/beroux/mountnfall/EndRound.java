package beroux.mountnfall;
import beroux.game.*;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.*;
import java.util.*;

public class EndRound extends SceneView
{
// Construction
	public EndRound()
	{
	}

	public void initialize(String whitePlayerName, String blackPlayerName, int rounds) {
		assert rounds > 0;
		
		// Load font (if not loaded)
		if (_roundsLeftString == null)
		{
			_whitePlayerName = new FontString("CosmicTwo24");
			_blackPlayerName = (FontString) _whitePlayerName.clone();
			_roundsLeftString = new FontString("CosmicTwo48");
			_whiteScoreString = (FontString) _roundsLeftString.clone();
			_blackScoreString = (FontString) _roundsLeftString.clone();
		}

		// White player name
		_whitePlayerName.setString(whitePlayerName);
		_whitePlayerName.setPosition(
				WHITE_PLAYER_X - _whitePlayerName.getWidth()/2,
				WHITE_PLAYER_Y);

		// Black player name
		_blackPlayerName.setString(blackPlayerName);
		_blackPlayerName.setPosition(
				BLACK_PLAYER_X - _blackPlayerName.getWidth()/2,
				BLACK_PLAYER_Y);

		// Rounds left
		_roundsLeft = rounds;

		// Reset score
		_whiteScore = 0;
		_blackScore = 0;
	}

// SceneView
	/** Check if scene is ready to be entered.
	 * @return False if the scene hasn't been initialized and cannot be entered.
	 */
	public boolean isInitilialized()
	{
		return _roundsLeftString != null;
	}
	
	public void onEntry()
	{
		// Background
		_background	= new Sprite("EndRoundBackground");

		// Rounds left
		_roundsLeftString.setString( Integer.toString(_roundsLeft) );
		_roundsLeftString.setPosition(
				ROUNDS_LEFT_X - _roundsLeftString.getWidth()/2,
				ROUNDS_LEFT_Y);

		// White's Score
		if (_whiteScore < 1000)
			_whiteScoreString.setString( Integer.toString(_whiteScore) );
		else
			_whiteScoreString.setString( Integer.toString(_whiteScore/1000)+","+Integer.toString(_whiteScore%1000) );
		_whiteScoreString.setPosition(
				WHITE_SCORE_X - _whiteScoreString.getWidth(),
				WHITE_SCORE_Y);

		// Black's Score
		if (_blackScore < 1000)
			_blackScoreString.setString( Integer.toString(_blackScore) );
		else
			_blackScoreString.setString( Integer.toString(_blackScore/1000)+","+Integer.toString(_blackScore%1000) );
		_blackScoreString.setPosition(
				BLACK_SCORE_X - _blackScoreString.getWidth(),
				BLACK_SCORE_Y);

		// Ok/Start button
		Sprite caption;
		if (_roundsLeft > 0)
			caption = new Sprite("Start");
		else
			caption = new Sprite("Ok");

		PushButtonActionListener listener = new PushButtonActionListener();
		int buttonX = Screen.width()/2,
			buttonY = Screen.height()*90/100;
		caption.defineReferencePixel(
				caption.getWidth()/2, 
				caption.getHeight()/2);
		caption.setRefPixelPosition(buttonX, buttonY);
		Sprite circle = new Sprite("StartCircle");
		circle.defineReferencePixel(
				circle.getWidth()/2, 
				circle.getHeight()/2);
		circle.setRefPixelPosition(buttonX, buttonY);
		_start = new BumpingButton(caption, circle);
		_start.setActionCommand("start");
		_start.addActionListener(listener);
		_start.setHitBox(caption.getBoundingBox());
	}

	public void onExit()
	{
		_background = null;
		_winnerAnim = null;
		_loserAnim = null;
		_start = null;
	}
	
	public void update(float dt)
	{
		_start.update(dt);
	}

	public void paint(Graphics2D g)
	{
		// Draw background.
		_background.paint(g);

		// Draw animation
		_winnerAnim.paint(g);
		_loserAnim.paint(g);

		// Draw text
		_whitePlayerName.paint(g);
		_blackPlayerName.paint(g);
		_whiteScoreString.paint(g);
		_blackScoreString.paint(g);
		_roundsLeftString.paint(g);

		// Draw start button.
		_start.paint(g);
	}

	/** Called when a mouse button press is detected.
	 * @param x			The X coordinates of the mouse at press time.
	 * @param y			The Y coordinates of the mouse at press time.
	 * @param button	The mouse button pressed.
	 */
	public void onMouseDown(int x, int y, int button)
	{
		if (_start == null)
			return;
			
		_start.onMouseDown(x, y, button);
	}

	/** Mouse motion.
	 */
	public void onMouseMove(int x, int y)
	{
		if (_start == null)
			return;
			
		_start.onMouseMove(x, y);

		// Change cursor.
		boolean overClickableItem = 
			_start.isMouseOver();
		if (overClickableItem != _overClickableItem)
		{
			if (overClickableItem)
				Screen.getInstance().setCursor(_handCursor);
			else
				Screen.getInstance().setCursor(_defaultCursor);
		}
		_overClickableItem = overClickableItem;
	}

// Attributes
	public String getWhitePlayerName() {
		return _whitePlayerName.getString();
	}

	public String getBlackPlayerName() {
		return _blackPlayerName.getString();
	}

	public int getRoundsLeft() {
		return _roundsLeft;
	}

// Operations
	public void onEndRound(int winnerColour, Board endGameBoard) {
		--_roundsLeft;

		// Animations
		if (_winnerAnim == null)
		{
			_winnerAnim = new Sprite("Bally/Winner");
			_winnerAnim.defineReferencePixel(
					_winnerAnim.getWidth()/2,
					_winnerAnim.getHeight());
		}
		if (_loserAnim == null)
		{
			_loserAnim = new Sprite("Bally/Loser");
			_loserAnim.defineReferencePixel(
					_loserAnim.getWidth()/2,
					_loserAnim.getHeight());
		}
		if (winnerColour == Board.Counter.Colour.WHITE)
		{
			_winnerAnim.setRefPixelPosition(WHITE_PLAYER_X, CHARACTERS_Y);
			_loserAnim.setRefPixelPosition(BLACK_PLAYER_X, CHARACTERS_Y);
		}
		else
		{
			_winnerAnim.setRefPixelPosition(BLACK_PLAYER_X, CHARACTERS_Y);
			_loserAnim.setRefPixelPosition(WHITE_PLAYER_X, CHARACTERS_Y);
		}
		
		// Count number of counters of each player.
		int whitePlayerCounters = 0,
			blackPlayerCounters = 0;
		Board.SquaresIterator	iter = new Board.SquaresIterator();
		while (iter.hasNext())
		{
			Board.Square sq = iter.next();
			Board.Counter c = endGameBoard.getCounter(sq);
			if (c.getColour() == Board.Counter.Colour.WHITE)
				whitePlayerCounters += c.getCount();
			else
				blackPlayerCounters += c.getCount();
		}

		// Update score
		if (winnerColour == Board.Counter.Colour.WHITE)
			_whiteScore += 1000 + whitePlayerCounters*50 + blackPlayerCounters*10;
		else
			_blackScore += 1000 + blackPlayerCounters*50 + whitePlayerCounters*10;
	}

// Implementation
	private class PushButtonActionListener implements ActionListener 
	{
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("start"))
				if (_roundsLeft > 0)
					sendMessageToController("more rounds");
				else
					sendMessageToController("end");
		}
	}

	private static final int WHITE_PLAYER_X = 163, WHITE_PLAYER_Y = 300;
	private static final int BLACK_PLAYER_X = Screen.width()-WHITE_PLAYER_X, BLACK_PLAYER_Y = 300;
	private static final int CHARACTERS_Y = 260;
	private static final int WHITE_SCORE_X = 270, WHITE_SCORE_Y = 387;
	private static final int BLACK_SCORE_X = 580, BLACK_SCORE_Y = 387;
	private static final int ROUNDS_LEFT_X = 430, ROUNDS_LEFT_Y = 490;
	
	private boolean		_overClickableItem;
	private BumpingButton	_start;
	private int			_roundsLeft,
						_whiteScore,
						_blackScore;
	private FontString	_whitePlayerName,
						_blackPlayerName,
						_roundsLeftString,
						_whiteScoreString,
						_blackScoreString;
	private Sprite		_background,
						_winnerAnim,
						_loserAnim;
	private Cursor		_defaultCursor = new Cursor(Cursor.DEFAULT),
						_handCursor = new Cursor("HandCursor", 6, 5);
}
