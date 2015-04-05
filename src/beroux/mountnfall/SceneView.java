package beroux.mountnfall;
import java.awt.Graphics2D;

abstract class SceneView
{
// Attributes
	public final void setController(ScenesController ctlr)
	{
		_controller = ctlr;
	}
	
// Operations
	protected final void sendMessageToController(String msg)
	{
		if (_controller != null)
			_controller.pushMessage(msg);
	}
	
// Overridables
	/** Check if scene is ready to be entered.
	 * @return False if the scene hasn't been initialized and cannot be entered.
	 */
	public abstract boolean isInitilialized();
	
	/** Entering the scene.
	 * Initialize it and make it ready to render.
	 */
	public abstract void onEntry();

	/** Exitting the scene.
	 * Clean up things previously initialized in the onEntry().
	 */
	public abstract void onExit();
	
	/** Update the scene between two rendering.
	 * @param dt	Time difference since the last update.
	 */
	abstract public void update(float dt);

	/** Render the scene.
	 * The scene should not update any member variable here.
	 * It should only update them in update().
	 */
	abstract public void paint(Graphics2D g);

	/** Called when a mouse button press is detected.
	 * @param x			The X coordinates of the mouse at press time.
	 * @param y			The Y coordinates of the mouse at press time.
	 * @param button	The mouse button pressed.
	 */
	public void onMouseDown(int x, int y, int button)
	{
	}

	/** Called when a mouse button release is detected.
	 * @param x			The X coordinates of the mouse at release time.
	 * @param y			The Y coordinates of the mouse at release time.
	 * @param button	The mouse button released.
	 */
	public void onMouseUp(int x, int y, int button)
	{
	}
	
	/** Called when the cursor has changed position.
	 * @param x			The X coordinates of the cursor in the window.
	 * @param y			The Y coordinates of the cursor in the window.
	 */
	public void onMouseMove(int x, int y)
	{
	}
	
	/** Called when a key is pressed.
	 * @param key		ASCII character of the key pressed.
	 * @param mod		Current key modifiers made of KeyModifierFlags.
	 */
	public void onKeyDown(char key, int mod)
	{
	}
	
	/** Called when a key is pressed.
	 * @param key		ASCII character of the key released.
	 * @param mod		Current key modifiers made of KeyModifierFlags.
	 */
	public void onKeyUp(char key, int mod)
	{
	}

// Implementation
	private ScenesController	_controller;
}
