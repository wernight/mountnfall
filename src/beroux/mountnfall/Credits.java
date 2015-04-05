package beroux.mountnfall;
import beroux.game.*;
import java.awt.Graphics2D;

public class Credits extends SceneView
{
// Construction
	public Credits()
	{
	}

// SceneView
	/** Check if scene is ready to be entered.
	 * @return False if the scene hasn't been initialized and cannot be entered.
	 */
	public boolean isInitilialized()
	{
		return true;
	}
	
	public void onEntry()
	{
		// Load sprites
		_background = new Sprite("Credits");
	}

	public void onExit()
	{
	}
	
	public void update(float dt)
	{
	}

	public void paint(Graphics2D g)
	{
		// Draw background.
		_background.paint(g);
	}
	
	/** Called when a mouse button press is detected.
	 * @param x			The X coordinates of the mouse at press time.
	 * @param y			The Y coordinates of the mouse at press time.
	 * @param button	The mouse button pressed.
	 */
	public void onMouseDown(int x, int y, int button)
	{
		sendMessageToController("end");
	}

// Attributes

// Operations

// Implementation
	// GUI
	private Sprite		_background;
}
