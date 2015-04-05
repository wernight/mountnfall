package beroux.mountnfall;
import beroux.game.*;
import java.util.Observable;
import java.util.Observer;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics2D;

/** Game Graphical User Interface
 * The white player is on the left, and the black player on the right.
 */
class GUI implements IGameObject 
{
// Construction
	public GUI(GameLogic subject)
	{
		_subject = subject;
		_boardRendered = new BoardRenderer( subject );
		_background = new InGameBackground(new Sprite("InGameBackground"));
		_grass = new Sprite("Grass");
		_grass.setRefPixelPosition(
				0,
				Screen.height()-_grass.getHeight());
		_title = null;

		// Towers
		Sprite tower = new Sprite("Tower"),
			   grassFg = new Sprite("TowerGrassFg"),
			   grassBg = new Sprite("TowerGrassBg");
		tower.defineReferencePixel(tower.getWidth()/2, 0);
		grassFg.defineReferencePixel(grassFg.getWidth()/2, 27);
		grassBg.defineReferencePixel(grassBg.getWidth()/2, 50);
		_leftTower = new Tower(60, 500, tower, grassFg, grassBg);
		_rightTower = new Tower(Screen.width()-60, 500, (Sprite) tower.clone(), (Sprite) grassFg.clone(), (Sprite) grassBg.clone());

		// Current player
		_currentPlayer = new Sprite("CurrentPlayer");
		_currentPlayerPositionX = Screen.width()/2;

		// Font
		System.out.println("loading fonts");
		_whitePlayerName = new FontString("CosmicTwo36");
		_blackPlayerName = (FontString) _whitePlayerName.clone();
		
		// Observer the model.
		subject.addObserver(_observer);
	}
	
// IGameObject
	public void update(float dt)
	{
		// Background
		_background.translate(BACKGROUND_SPEED_X*dt, BACKGROUND_SPEED_Y*dt);

		// Board
 		_boardRendered.update(dt);

		// Towers
		_leftTower.update(dt);
		_rightTower.update(dt);

		// Translate the current player pannel.
		float destX;
		if (_subject.getCurrentPlayerColour() == Board.Counter.Colour.WHITE)
			destX = 0.0f;
		else
			destX = Screen.width() - _currentPlayer.getWidth();
		float acceletationX = CURRENT_PLAYER_ACCELERATION*(destX - _currentPlayerPositionX);
		if (Math.abs((int)((destX - _currentPlayerPositionX)*100)/Screen.width()) > 90)
			_currentPlayerVelocityX = acceletationX;
		else
			_currentPlayerVelocityX += acceletationX*dt;
		_currentPlayerVelocityX -= CURRENT_PLAYER_FRICTION*_currentPlayerVelocityX*dt;
		_currentPlayerPositionX += _currentPlayerVelocityX*dt;
		// Clamp to visible areas
		if (_currentPlayerPositionX < -_currentPlayer.getWidth())
			_currentPlayerPositionX = -_currentPlayer.getWidth();
		else if (_currentPlayerPositionX > Screen.width()+_currentPlayer.getWidth())
			_currentPlayerPositionX = Screen.width()+_currentPlayer.getWidth();
	}
	
	public void paint(Graphics2D g)
	{
		// Background
		_background.paint(g);
		_grass.paint(g);
		if (_title != null)
			_title.paint(g);

		// Display the board
		_boardRendered.paint(g);
		
		// Display the towers
		_leftTower.paint(g);
		_rightTower.paint(g);

		// Display current player
		_currentPlayer.setRefPixelPosition(
				Math.round(_currentPlayerPositionX), 
				Math.round(CURRENT_PLAYER_Y - _currentPlayer.getHeight()*0.5f));
		_currentPlayer.paint(g);
		_whitePlayerName.paint(g);
		_blackPlayerName.paint(g);
	}

// Attributes
	/** Return the board renderer.
	 */
	public BoardRenderer getBoardView()
	{
		return _boardRendered;
	}

	/** Set the title to display.
	 */
	public void setTitle(Sprite title)
	{
		_title = title;
		_title.setRefPixelPosition(
				Screen.width()/2,
				Screen.height()/13);
	}
	
	public void setGameLogic(GameLogic logic)
	{
		// Observer the new game logic
		_subject.deleteObserver(_observer);
		_subject = logic;
		_subject.addObserver(_observer);

		// Tell board view to display new board
		_boardRendered.assign(logic);
		
		// Update tower height.
		updateTowersHeights();
	}

// Operations

// Implementation
	private class GameLogicObserver implements Observer
	{
	// Observer
		/** This method is called whenever the observed object is changed.
		 * @param o		the observable object.
		 * @param arg	an argument passed to the notifyObservers method.
		 */
		public void update(Observable o, Object arg)
		{
			GameCommand message = (GameCommand) arg;

			if (message.getClass() == NewGame.class)
			{
				NewGame realMsg = (NewGame) message;
				
				_whitePlayerName.setString(realMsg.whitePlayerName);
				_whitePlayerName.setPosition(
					Math.round(_currentPlayer.getWidth()/2.0f - _whitePlayerName.getWidth()/2.0f),
					Math.round(CURRENT_PLAYER_Y + _whitePlayerName.getHeight()/2.0f));
				
				_blackPlayerName.setString(realMsg.blackPlayerName);
				_blackPlayerName.setPosition(
					Math.round(Screen.width() - _currentPlayer.getWidth()/2.0f - _blackPlayerName.getWidth()/2.0f),
					Math.round(CURRENT_PLAYER_Y + _blackPlayerName.getHeight()/2.0f));

				updateTowersHeights();
			}
			else if (message.getClass() == DepileMove.class)
				// Some counters removed? Set tower height.
				updateTowersHeights();
			else if (message.getClass() == GameOver.class)
			{
				// Crunch down the tower of the loser.
				if (((GameOver) message).winner == Board.Counter.Colour.WHITE)
					_rightTower.setScale(0.0f);
				else
					_leftTower.setScale(0.0f);
			}
		}
	}
	
	private void updateTowersHeights()
	{
		int whitePlayerCounters = 0,
			blackPlayerCounters = 0;

		// Count number of counters of each player.
		Board.SquaresIterator	iter = new Board.SquaresIterator();
		while (iter.hasNext())
		{
			Board.Square sq = iter.next();
			Board.Counter c = _subject.getBoard().getCounter(sq);
			if (c.getColour() == Board.Counter.Colour.WHITE)
				whitePlayerCounters += c.getCount();
			else
				blackPlayerCounters += c.getCount();
		}

		// Change towers' heights.
		_leftTower.setScale((float) whitePlayerCounters / MAXIMUM_PLAYER_COUNTERS);
		_rightTower.setScale((float) blackPlayerCounters / MAXIMUM_PLAYER_COUNTERS);
	}
	
	private static final int	MAXIMUM_PLAYER_COUNTERS = 5 + 4 + 3 + 2 + 1;
	private static final float	BACKGROUND_SPEED_X = 20.0f,
								BACKGROUND_SPEED_Y = -4.0f;
	private static final float	CURRENT_PLAYER_ACCELERATION = 200.0f,
								CURRENT_PLAYER_FRICTION = 8.0f;
	private static final float  CURRENT_PLAYER_Y = 0.9f*Screen.height();

	private final GameLogicObserver	_observer = new GameLogicObserver();
	private final InGameBackground	_background;
	private final Sprite		_grass;
	private final Tower			_leftTower,
								_rightTower;
	private final BoardRenderer	_boardRendered;
	private Sprite 				_title;
	private FontString			_whitePlayerName,
								_blackPlayerName;
	private GameLogic 			_subject;

	// Current player.
	private Sprite		_currentPlayer;
	private float		_currentPlayerPositionX,
						_currentPlayerVelocityX;
}
