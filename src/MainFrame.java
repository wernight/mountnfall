import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import beroux.game.*;

import javax.swing.*;

public class MainFrame extends Canvas implements MouseListener, MouseMotionListener
{
	private GameCanvas _game;
	private Screen _screen;
	private JFrame _container;

	public static void main(String[] args)
	{
		new MainFrame();
	}
	
	public MainFrame()
	{
		// create a frame to contain our game
		_container = new JFrame("Mount'n Fall");

		// get hold the content of the frame and set up the resolution of the game
		JPanel panel = (JPanel) _container.getContentPane();
		panel.setPreferredSize(new Dimension(Screen.width(), Screen.height()));
		panel.setLayout(null);
		
		// setup our canvas size and put it into the content of the frame
		setBounds(0, 0, Screen.width(), Screen.height());
		panel.add(this);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(new JMenu(" "));
		_container.setJMenuBar(menuBar);
		
		// Tell AWT not to bother repainting our canvas since we're
		// going to do that our self in accelerated mode
		setIgnoreRepaint(true);
		
		// finally make the window visible 
		_container.pack();
		_container.setResizable(false);
		_container.setVisible(true);

		// add a listener to respond to the user closing the window.
		// If they do we'd like to exit the game.
		_container.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		// Initialize our game's screen.
		_screen = Screen.getInstance();
		_screen.setCanvas(this);
		
		// Initialise the entities in our game so there's something
		// to see at startup.
		InitGame();
		
		// Add a key input system (defined below) to our canvas
		// so we can respond to key pressed.
//		addKeyListener(new KeyInputHandler());
		addMouseListener(this);
		addMouseMotionListener(this);
		
		// Request the focus so key events come to us.
		requestFocus();

		// Start the main game loop, note: this method will not
		// return until the game has finished running. Hence we are
		// using the actual main thread to run the game.
		GameLoop();
	}

	public boolean InitGame()
	{
		_game = new beroux.mountnfall.ScenesController();

		if (!_game.initialize(_container))
			return false;

		return true;
	}

	/** The main game loop.
	 *  This loop is running during all game.
	 *  It is responsible for the following activities:
	 * - Updating the game state (entities, logic...)
	 * - Drawing on screen the content
	 * - Retransmit game events
	 */
	public void GameLoop()
	{
		// Calculate FPS
		long fpsStartTime = System.currentTimeMillis(),
			 frames = 0;
		float	_previousRatio = _screen.getRatio();

		// Create the buffering strategy which will allow AWT
		// to manage our accelerated graphics.
		createBufferStrategy(2);
		// The stragey that allows us to use accelerate page flipping.
		BufferStrategy strategy = getBufferStrategy();
		
		// Go away and grab the sprite from the resource loader
		// keep looping round til the game ends
		long lastLoopTime = System.currentTimeMillis();
		while (true)
		{
			// work out how long its been since the last update, this
			// will be used to calculate how far the entities should
			// move this loop
			long currentTime = System.currentTimeMillis();
			_game.update((currentTime - lastLoopTime)*0.001f);
			lastLoopTime = currentTime;
			
			// Display FPS
			++frames;
			long fpsEndTime = System.currentTimeMillis();
			if ((fpsEndTime - fpsStartTime) > 5000)
			{
				long fps = (frames*1000)/(fpsEndTime - fpsStartTime);
				System.out.println("FPS: "+fps);
				fpsStartTime = fpsEndTime;
				frames = 0;
			}
		
			// Get hold of a graphics context for the accelerated surface
			Graphics2D g2d = (Graphics2D) strategy.getDrawGraphics();

			// Render the game.
			_screen.setGraphics( g2d );
			if (Math.abs(_screen.getRatio() - 1.0f) > 0.001f)
				g2d.scale(_screen.getRatio(), _screen.getRatio());
			_game.paint(g2d);

			// finally, we've completed drawing so clean up the graphics
			// and flip the buffer over
			g2d.dispose();
			strategy.show();
			
			// finally pause for a bit. Note: this should run us at about
			// 100 fps but on windows this might vary each loop due to
			// a bad implementation of timer
			try { Thread.sleep(10); } catch (Exception e) {}
			
			// Resize screen if necessary
			if (Math.abs(_screen.getRatio() - _previousRatio) > 0.0001)
			{
				JPanel panel = (JPanel) _container.getContentPane();
				panel.setPreferredSize( new Dimension(
						Math.round(Screen.width()*_screen.getRatio()), 
						Math.round(Screen.height()*_screen.getRatio())) );
				
				setBounds(
						0, 
						0, 
						Math.round(Screen.width()*_screen.getRatio()), 
						Math.round(Screen.height()*_screen.getRatio()));

				_container.pack();
				
				_previousRatio = _screen.getRatio();
			}
		}
	}

// MouseListener
	// Invoked when the mouse button has been clicked (pressed and released) on a component.
	public void mouseClicked(MouseEvent e)
	{
	}

	// Invoked when the mouse enters a component.
	public void mouseEntered(MouseEvent e)
	{
	}

	// Invoked when the mouse exits a component.
	public void mouseExited(MouseEvent e)
	{
	}

	// Invoked when a mouse button has been pressed on a component.
	public void mousePressed(MouseEvent e)
	{
		_game.onMouseDown(
				Math.round(e.getX()/_screen.getRatio()), 
				Math.round(e.getY()/_screen.getRatio()), 
				e.getButton());
	}

	// Invoked when a mouse button has been released on a component.
	public void mouseReleased(MouseEvent e)
	{
		_game.onMouseUp(
				Math.round(e.getX()/_screen.getRatio()),
				Math.round(e.getY()/_screen.getRatio()), 
				e.getButton());
	}

// MouseMotionListener
	public void mouseDragged(MouseEvent e)
	{
		_game.onMouseMove(
				Math.round(e.getX()/_screen.getRatio()),
				Math.round(e.getY()/_screen.getRatio()));
    }

    public void mouseMoved(MouseEvent e)
	{
		_game.onMouseMove(
				Math.round(e.getX()/_screen.getRatio()),
				Math.round(e.getY()/_screen.getRatio()));
    }

}
