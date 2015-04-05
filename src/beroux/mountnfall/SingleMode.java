package beroux.mountnfall;
import beroux.game.*;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.*;
import java.util.*;

public class SingleMode extends SceneView
{
// Construction
	public SingleMode()
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
		_start.setHitBox(caption.getBoundingBox());
		_start.setActionCommand("start");
		_start.addActionListener(listener);

		// Levels
		TreeMap levels = new TreeMap();
		levels.put(new Integer(AiPlayer.Level.EASY), new Sprite("Easy"));
		levels.put(new Integer(AiPlayer.Level.MEDIUM), new Sprite("Medium"));
		levels.put(new Integer(AiPlayer.Level.HARD), new Sprite("Hard"));
		iter = levels.keySet().iterator();
		while (iter.hasNext())
		{
			Sprite spr = (Sprite) levels.get(iter.next());
			spr.defineReferencePixel(0, spr.getHeight()/2);
		}
		
		hitBox = new Rectangle(265, 190, 200, 60);
		_level = new EnumButton(hitBox.x, hitBox.y+hitBox.height/2, levels);
		_level.setHitBox(hitBox);

		// Colours
		TreeMap colours = new TreeMap();
		colours.put(new Integer(Board.Counter.Colour.WHITE), new Sprite("Whites"));
		colours.put(new Integer(Board.Counter.Colour.BLACK), new Sprite("Blacks"));
		iter = colours.keySet().iterator();
		while (iter.hasNext())
		{
			Sprite spr = (Sprite) colours.get(iter.next());
			spr.defineReferencePixel(0, spr.getHeight()/2);
		}
		
		hitBox = new Rectangle(265, 260, 200, 60);
		_colour = new EnumButton(hitBox.x, hitBox.y+hitBox.height/2, colours, new Integer(Board.Counter.Colour.WHITE));
		_colour.setHitBox(hitBox);

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
	   _background = new Sprite("SingleModeBackground");
	}

	public void onExit()
	{
		_background = null;
	}
	
	public void update(float dt)
	{
		_start.update(dt);
		_level.update(dt);
		_colour.update(dt);
		_rounds.update(dt);
	}

	public void paint(Graphics2D g2)
	{
		// Draw background.
		_background.paint(g2);

		// Draw info.
		_level.paint(g2);
		_colour.paint(g2);
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
		_level.onMouseDown(x, y, button);
		_colour.onMouseDown(x, y, button);
		_rounds.onMouseDown(x, y, button);
	}

	/** Mouse motion.
	 * @param x			The X coordinates of the cursor in the window.
	 * @param y			The Y coordinates of the cursor in the window.
	 */
	public void onMouseMove(int x, int y)
	{
		// Send event to buttons.
		_start.onMouseMove(x, y);
		_level.onMouseMove(x, y);
		_colour.onMouseMove(x, y);
		_rounds.onMouseMove(x, y);

		// Change cursor.
		boolean overClickableItem = 
			_start.isMouseOver() ||
			_level.isMouseOver() ||
			_colour.isMouseOver() ||
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
	public int getAiLevel() {
		return ((Integer) _level.getActiveKey()).intValue();
	}

	public int getPlayerColour() {
		return ((Integer) _colour.getActiveKey()).intValue();
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
	
	// GUI
	private boolean		_overClickableItem;
	private Sprite		_background;
	private EnumButton	_level,
						_colour,
						_rounds;
	private BumpingButton	_start;
	private Cursor		_defaultCursor = new Cursor(Cursor.DEFAULT),
						_handCursor = new Cursor("HandCursor", 6, 5);
}
