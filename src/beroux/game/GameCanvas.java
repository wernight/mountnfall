/** The GameCanvas class provides the basis for a game user interface.
 * The original version, named Game was more an idea of a middleware layer.
 * 
 * When I (Werner BEROUX) saw that it was almost not possible to change from
 * J2RE to J2ME for example, I renamed it to AbstractGame. It was more a
 * canvas for games, so that the hard stuff hard kept inside this package.
 * It was lately renamed to GameCanvas to get closer of the J2ME version.
 *
 * Because both are close, here is a documentation taken from J2ME:
 *
 * The GameCanvas class provides the basis for a game user interface.
 * In addition to the features inherited from Canvas (commands, input
 * events, etc.) it also provides game-specific capabilities such as an
 * off-screen graphics buffer and the ability to query key status.
 * 
 * A dedicated buffer is created for each GameCanvas instance. Since a
 * unique buffer is provided for each GameCanvas instance, it is preferable
 * to re-use a single GameCanvas instance in the interests of minimizing 
 * heap usage. The developer can assume that the contents of this buffer 
 * are modified only by calls to the Graphics object(s) obtained from the 
 * GameCanvas instance; the contents are not modified by external sources 
 * such as other MIDlets or system-level notifications. The buffer is 
 * initially filled with white pixels.
 *
 * The buffer's size is set to the maximum dimensions of the GameCanvas. 
 * However, the area that may be flushed is limited by the current dimensions 
 * of the GameCanvas (as influenced by the presence of a Ticker, Commands, 
 * etc.) when the flush is requested. The current dimensions of the 
 * GameCanvas may be obtained by calling getWidth and getHeight.
 *
 * A game may provide its own thread to run the game loop. A typical loop 
 * will check for input, implement the game logic, and then render the 
 * updated user interface. The following code illustrates the structure 
 * of a typcial game loop:
 *
 *   // Get the Graphics object for the off-screen buffer
 *   Graphics g = getGraphics();
 *   while (true) {
 *       // Check user input and update positions if necessary
 *       int keyState = getKeyStates();
 *       if ((keyState & LEFT_PRESSED) != 0) {
 *           sprite.move(-1, 0);
 *       }
 *       else if ((keyState & RIGHT_PRESSED) != 0) {
 *           sprite.move(1, 0);
 *       }
 *
 *       // Clear the background to white
 *       g.setColor(0xFFFFFF);
 *       g.fillRect(0,0,getWidth(), getHeight());
 *
 *       // Draw the Sprite
 *       sprite.paint(g);
 *
 *       // Flush the off-screen buffer
 *       flushGraphics();
 */
package beroux.game;
import javax.swing.JFrame;
import java.awt.Graphics2D;

public abstract class GameCanvas
{
	public interface MouseButton
	{
		static final int LEFT = 1;
		static final int RIGHT = 2;
		static final int MIDDLE = 3;
	}

	public interface KeyModifierFlags
	{
		static final int ALT =	0x01;
		static final int CTRL =	0x02;
		static final int SHIFT = 0x04;
	}

// Attributes
	
// Events
	/** Called when a mouse button press is detected.
	 * @param x			The X coordinates of the mouse at press time.
	 * @param y			The Y coordinates of the mouse at press time.
	 * @param button	The mouse button pressed.
	 */
	public void onMouseDown(int x, int y, int button)
	{}

	/** Called when a mouse button release is detected.
	 * @param x			The X coordinates of the mouse at release time.
	 * @param y			The Y coordinates of the mouse at release time.
	 * @param button	The mouse button released.
	 */
	public void onMouseUp(int x, int y, int button)
	{}
	
	/** Called when the cursor has changed position.
	 * @param x			The X coordinates of the cursor in the window.
	 * @param y			The Y coordinates of the cursor in the window.
	 */
	public void onMouseMove(int x, int y)
	{}
	
	/** Called when a key is pressed.
	 * @param key		ASCII character of the key pressed.
	 * @param mod		Current key modifiers made of KeyModifierFlags.
	 */
	public void onKeyDown(char key, int mod)
	{}
	
	/** Called when a key is pressed.
	 * @param key		ASCII character of the key released.
	 * @param mod		Current key modifiers made of KeyModifierFlags.
	 */
	public void onKeyUp(char key, int mod)
	{}

// Operations
	/** Initialize the game.
	 * @return False in case of error and the game should exit.
	 */
	public abstract boolean initialize(JFrame frame);
	
	/** Update the game.
	 * @param dt	Time difference in seconds between two updates.
	 */
	public abstract void update(float dt);

	/** Render the game.
	 */
	public abstract void paint(Graphics2D g);
}
