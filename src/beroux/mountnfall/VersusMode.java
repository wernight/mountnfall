package beroux.mountnfall;
import beroux.game.*;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class VersusMode extends SceneView
{
// Construction
	public VersusMode()
	{
		PushButtonActionListener listener = new PushButtonActionListener();
		Iterator iter;
		Rectangle hitBox;

		// Start button
		int buttonX = Screen.width()/2,
			buttonY = Screen.height()*80/100;
		Sprite caption = new Sprite("Start");
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

		// Player names
		_whitePlayerName.setString("Player 1");
		_whitePlayerName.setPosition(265, 240);
		_blackPlayerName.setString("Player 2");
		_blackPlayerName.setPosition(265, 310);

		// Rounds
		TreeMap digits = new TreeMap();
		for (int i=1; i<=4; ++i)
			digits.put(new Integer(i), new Sprite(""+i));
		iter = digits.keySet().iterator();
		while (iter.hasNext())
		{
			Sprite spr = (Sprite) digits.get(iter.next());
			spr.defineReferencePixel(0, spr.getHeight()/2);
		}
		
		hitBox = new Rectangle(265, 334, 100, 60);
		_rounds = new EnumButton(hitBox.x, hitBox.y+hitBox.height/2, digits);
		_rounds.setHitBox(hitBox);
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
		_background	= new Sprite("VersusModeBackground");
	}

	public void onExit()
	{
		_background	= null;
	}
	
	public void update(float dt)
	{
		_start.update(dt);
		_rounds.update(dt);
	}

	public void paint(Graphics2D g2)
	{
		// Draw background.
		_background.paint(g2);

		// Draw info.
		_whitePlayerName.paint(g2);
		_blackPlayerName.paint(g2);
		_rounds.paint(g2);

		// Draw start button.
		_start.paint(g2);
	}

	/** Called when a mouse button press is detected.
	 * @param x			The X coordinates of the mouse at press time.
	 * @param y			The Y coordinates of the mouse at press time.
	 * @param button	The mouse button pressed.
	 */
	public void onMouseDown(int x, int y, int button)
	{
		_start.onMouseDown(x, y, button);
		_rounds.onMouseDown(x, y, button);
	}

	/** Mouse motion.
	 * @param x			The X coordinates of the cursor in the window.
	 * @param y			The Y coordinates of the cursor in the window.
	 */
	public void onMouseMove(int x, int y)
	{
		_start.onMouseMove(x, y);
		_rounds.onMouseMove(x, y);

		// Change cursor.
		boolean overClickableItem = 
			_start.isMouseOver() ||
			_rounds.isMouseOver();
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

	public int getNumberOfRounds() {
		return ((Integer) _rounds.getActiveKey()).intValue();
	}

// Operations

// Implementation
	private class PushButtonActionListener implements ActionListener 
	{
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("start"))
				sendMessageToController("start");
		}
	}
	
	private FontString	_whitePlayerName = new FontString("CosmicTwo36"),
						_blackPlayerName = new FontString("CosmicTwo36");
	private boolean		_overClickableItem;
	private BumpingButton	_start;
	private EnumButton	_rounds;
	private Sprite		_background;
	private Cursor		_defaultCursor = new Cursor(Cursor.DEFAULT),
						_handCursor = new Cursor("HandCursor", 6, 5);
}
