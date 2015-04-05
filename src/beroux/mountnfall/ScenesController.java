package beroux.mountnfall;
import beroux.game.*;
import java.util.*;
import javax.swing.*;
import java.awt.Graphics2D;
import java.awt.event.*;

public class ScenesController extends beroux.game.GameCanvas
{
// Construction
	public ScenesController()
	{
	}
	
// Game
	// Events
	
	/** Called when a mouse button press is detected.
	 * @param x			The X coordinates of the mouse at press time.
	 * @param y			The Y coordinates of the mouse at press time.
	 * @param button	The mouse button pressed.
	 */
	public void onMouseDown(int x, int y, int button)
	{
		_currentState.getView().onMouseDown(x, y, button);
	}

	/** Called when a mouse button release is detected.
	 * @param x			The X coordinates of the mouse at release time.
	 * @param y			The Y coordinates of the mouse at release time.
	 * @param button	The mouse button released.
	 */
	public void onMouseUp(int x, int y, int button)
	{
		_currentState.getView().onMouseUp(x, y, button);
	}
	
	/** Called when the cursor has changed position.
	 * @param x			The X coordinates of the cursor in the window.
	 * @param y			The Y coordinates of the cursor in the window.
	 */
	public void onMouseMove(int x, int y)
	{
		_currentState.getView().onMouseMove(x, y);
	}
	
	/** Called when a key is pressed.
	 * @param key		ASCII character of the key pressed.
	 * @param mod		Current key modifiers made of KeyModifierFlags.
	 */
	public void onKeyDown(char key, int mod)
	{
		_currentState.getView().onKeyDown(key, mod);
	}
	
	/** Called when a key is pressed.
	 * @param key		ASCII character of the key released.
	 * @param mod		Current key modifiers made of KeyModifierFlags.
	 */
	public void onKeyUp(char key, int mod)
	{
		_currentState.getView().onKeyUp(key, mod);
	}

	// Operations
	/** Initialize the game.
	 * @return False in case of error and the game should exit.
	 */
	public boolean initialize(JFrame frame)
	{
		// Create state machine
		_states[EState.MAIN_MENU] = new State(new MainMenu());
		_states[EState.SINGLE] = new State(new SingleMode());
		_states[EState.VERSUS] = new State(new VersusMode());
		_states[EState.IN_GAME] = new State(_inGame);
		_states[EState.CREDITS] = new State(new Credits());
		_states[EState.END_ROUND] = new State(_endRound);

		_states[EState.MAIN_MENU].addTransition( new Transition("single", _states[EState.SINGLE]) );
		_states[EState.MAIN_MENU].addTransition( new Transition("versus", _states[EState.VERSUS]) );
		_states[EState.MAIN_MENU].addTransition( new Transition("credits", _states[EState.CREDITS]) );
		_states[EState.SINGLE].addTransition( new Transition("start", _states[EState.IN_GAME]) );
		_states[EState.VERSUS].addTransition( new Transition("start", _states[EState.IN_GAME]) );
		_states[EState.IN_GAME].addTransition( new Transition("game over", _states[EState.END_ROUND]) );
		_states[EState.END_ROUND].addTransition( new Transition("more rounds", _states[EState.IN_GAME]) );
		_states[EState.END_ROUND].addTransition( new Transition("end", _states[EState.MAIN_MENU]) );
		for (int i=0; i<_states.length; ++i)
			_states[i].getView().setController(this);

		// Initial state machine
		_currentState = _states[0];
		if (!_currentState.getView().isInitilialized())
			return false;
		_currentState.getView().onEntry();

		// Create a menu
		JMenu menu;
		_menuBar = frame.getJMenuBar();
		
		menu = new JMenu("Main menu");
		new MenuListener("main", menu);
		_menuBar.add(menu);

		_menuBar.add(new JMenu(" "));

		menu = new JMenu("Save");
		new MenuListener("save", menu);
		_menuBar.add(menu);
		
		menu = new JMenu("Load");
		new MenuListener("load", menu);
		_menuBar.add(menu);
		
		_menuBar.add(new JMenu(" "));

		menu = new JMenu("Undo");
		new MenuListener("undo", menu);
		_menuBar.add(menu);
		
		_menuBar.add(new JMenu(" "));

		menu = new JMenu("+");
		new MenuListener("size+", menu);
		_menuBar.add(menu);
		
		menu = new JMenu("-");
		new MenuListener("size-", menu);
		_menuBar.add(menu);
		
		_menuBar.add(new JMenu(" "));

		menu = new JMenu("About...");
		new MenuListener("about", menu);
		_menuBar.add(menu);
		
		frame.setJMenuBar(_menuBar);
		
		return true;
	}
	
	/** Update the game.
	 * @param dt	Time difference between two updates.
	 */
	public void update(float dt)
	{
		// Read messages in queue.
		while (!_msgQueue.isEmpty())
		{
			// Get queue's head
			String msg = (String) _msgQueue.lastElement();
			_msgQueue.remove( _msgQueue.size() - 1 );

			// Read the message with the state machine.
			processMessage(msg);
		}

		// Read menu messages in queue.
		while (!_menuMsgQueue.isEmpty())
		{
			// Get queue's head
			String msg = (String) _menuMsgQueue.lastElement();
			_menuMsgQueue.remove( _menuMsgQueue.size() - 1 );

			// Read the message with the state machine.
			processMenuMessage(msg);
		}
		
		// Update current scene.
		_currentState.getView().update(dt);
	}

	/** Render the game.
	 */
	public void paint(Graphics2D g)
	{
		_currentState.getView().paint(g);
	}

// Operations
	void pushMessage(String msg)
	{
		_msgQueue.add(msg);
	}

// Implementation
	private boolean processMessage(String msg)
	{
		System.out.println("Processing: "+msg);

		for (int i=0; i<_currentState.getTransitionsCount(); ++i)
			if (_currentState.getTransition(i).getActivator().equals( msg ))
				return goToState( _currentState.getTransition(i).getDestination() );
		return false;
	}

	private void pushMenuMessage(String msg)
	{
		_menuMsgQueue.add(msg);
	}

	private boolean processMenuMessage(String msg)
	{
		if (msg.equals("main"))
			// Go to main menu
			return goToState(_states[EState.MAIN_MENU]);
		else if (msg.equals("save"))
		{
			// Only when we are in game
			if (_currentState == _states[EState.IN_GAME])
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogType(JFileChooser.SAVE_DIALOG);
				int returnVal = chooser.showOpenDialog( Screen.getInstance().getCanvas() );
				if (returnVal == JFileChooser.APPROVE_OPTION &&
					_inGame.saveGame( chooser.getSelectedFile() ))
				{
					System.out.println("Game saved.");
					return true;
				}
			}
			else
				System.out.println("Cannot save when not in game.");
			return false;
		}
		else if (msg.equals("load"))
		{
			// Only when we are in game
			if (_currentState == _states[EState.IN_GAME])
			{
				JFileChooser chooser = new JFileChooser();
				int returnVal = chooser.showOpenDialog( Screen.getInstance().getCanvas() );
				if (returnVal == JFileChooser.APPROVE_OPTION &&
					_inGame.loadGame( chooser.getSelectedFile() ))
				{
					System.out.println("Game loaded.");
					return true;
				}
			}
			else
				System.out.println("Cannot load when not in game.");
			return false;
		}
		else if (msg.equals("undo"))
		{
			// Only when we are in game
			if (_currentState == _states[EState.IN_GAME])
			{
				// Undo last move
				_inGame.undoLastMove();
				return true;
			}
			return false;
		}
		else if (msg.equals("size+"))
		{
			Screen scr = Screen.getInstance();
			if (scr.getRatio() < 2.0f)
				scr.setRatio(scr.getRatio()+0.25f);
			return true;
		}
		else if (msg.equals("size-"))
		{
			Screen scr = Screen.getInstance();
			if (scr.getRatio() > 0.5f)
				scr.setRatio(scr.getRatio()-0.25f);
			return true;
		}
		else if (msg.equals("about"))
		{
			if (_currentState != _states[EState.CREDITS])
				// Display credits and return to current scene
				return goToState( _states[EState.CREDITS] );
		}
		return false;
	}
	
	private boolean goToState(State newState)
	{
		// Make transition
		doStateTransition(_currentState, newState);
		
		// View must be initialized and ready.
		if (newState.getView().isInitilialized())
		{
			// Change state.
			_currentState.getView().onExit();
			_currentState = newState;
			_currentState.getView().onEntry();
			return true;
		}
		else
			return false;
	}

	private void doStateTransition(State currentState, State newState)
	{
		// Setup InGame
		if (newState.getView() instanceof InGame)
		{
			if (currentState.getView() instanceof SingleMode)
			{
				// Setup InGame
				SingleMode view = (SingleMode) currentState.getView();
				Sprite title = new Sprite("SingleMode");
				title.defineReferencePixel(title.getWidth()/2, title.getHeight()/2);
				Player p1 = new HumanPlayer("You"),
					   p2 = new AiPlayer(view.getAiLevel());
				Player whitePlayer, blackPlayer;
				if (view.getPlayerColour() == Board.Counter.Colour.WHITE)
				{
					whitePlayer = p1;
					blackPlayer = p2;
				}
				else
				{
					whitePlayer = p2;
					blackPlayer = p1;
				}
				_inGame.create(whitePlayer, blackPlayer, title);
				_startingPlayer = Board.Counter.Colour.WHITE;

				// Setup EndRound
				_endRound.initialize(
						whitePlayer.getName(), 
						blackPlayer.getName(), 
						view.getNumberOfRounds());
			}
			else if (currentState.getView() instanceof VersusMode)
			{
				// Setup InGame
				VersusMode view = (VersusMode) currentState.getView();
				Sprite title = new Sprite("VersusMode");
				title.defineReferencePixel(title.getWidth()/2, title.getHeight()/2);
				Player whitePlayer = new HumanPlayer(view.getWhitePlayerName()),
					   blackPlayer = new HumanPlayer(view.getBlackPlayerName());
				_inGame.create(whitePlayer, blackPlayer, title);
				_startingPlayer = Board.Counter.Colour.WHITE;

				// Setup EndRound
				_endRound.initialize(
						whitePlayer.getName(), 
						blackPlayer.getName(), 
						view.getNumberOfRounds());
			}
			else if (currentState.getView() instanceof EndRound)
			{
				// Change starting player
				if (_startingPlayer == Board.Counter.Colour.WHITE)
					_startingPlayer = Board.Counter.Colour.BLACK;
				else
					_startingPlayer = Board.Counter.Colour.WHITE;
			}
			else
				assert false;

			// Set starting player
			_inGame.setStartingPlayer(_startingPlayer);
		}
		// Update EndRound
		else if (currentState.getView() instanceof InGame && 
				newState.getView() instanceof EndRound)
		{
			// Setup EndRound
			_endRound.onEndRound(_inGame.getWinner(), _inGame.getBoard());
		}
		// Credits?
		else if (newState.getView() instanceof Credits)
		{
			// Make sure we return to current state
			_states[EState.CREDITS].removeAllTransitions();
			_states[EState.CREDITS].addTransition( new Transition("end", _currentState) );
		}
	}
	
	class MenuListener extends MouseAdapter
	{
		public MenuListener(String actionCommand, JMenu menu)
		{
			_actionCommand = actionCommand;
			_menu = menu;
			menu.addMouseListener(this);
		}
		
	    public void mouseClicked(MouseEvent e)
		{
			pushMenuMessage(_actionCommand);
	    }
		
		public void mouseReleased(MouseEvent e)
		{
			_menu.setSelected(false);
		}

		private String	_actionCommand;
		private JMenu	_menu;
	}
 
	private class Transition
	{
		public Transition(String activator, State destination) {
			_activator = activator;
			_destination = destination;
		}

		public String getActivator() {
			return _activator;
		}

		public State getDestination() {
			return _destination;
		}

		private String _activator;
		private State _destination;
	}
		
	private class State
	{
		public State(SceneView view) {
			_transitions = new ArrayList();
			_view = view;
		}

		public SceneView getView() {
			return _view;
		}

		public int getTransitionsCount() {
			return _transitions.size();
		}

		public Transition getTransition(int index) {
			return (Transition) _transitions.get(index);
		}

		public void addTransition(Transition transition) {
			_transitions.add(transition);
		}

		public void removeAllTransitions() {
			_transitions.clear();
		}

		private SceneView _view;
		private ArrayList _transitions;
	}

	interface EState
	{
		public static final int
			MAIN_MENU = 0,
			SINGLE = 1,
			VERSUS = 2,
			CREDITS = 3,
			IN_GAME = 4,
			END_ROUND = 5;
	}

	private State		_states[] = new State[6],
						_currentState;
	private Vector		_msgQueue = new Vector(),
						_menuMsgQueue = new Vector();
	private int			_startingPlayer;
	private InGame		_inGame = new InGame();
	private EndRound	_endRound = new EndRound();
	private JMenuBar 	_menuBar;
}
