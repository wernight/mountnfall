package beroux.mountnfall;
import beroux.game.*;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.event.*;

public class MainMenu extends SceneView
{
// Construction
	public MainMenu()
	{
		PushButtonActionListener listener = new PushButtonActionListener();

		Sprite counterButton[] = new Sprite[3];
		_buttons = new BumpingButton[Buttons.count];
		for (int i=0; i<counterButton.length; ++i)
		{
			counterButton[i] = new Sprite("CounterButton");
			counterButton[i].defineReferencePixel(
					counterButton[i].getWidth()/2,
					counterButton[i].getHeight()/2);
			
			switch (i)
			{
			case Buttons.SingleMode:
				counterButton[i].setRefPixelPosition(120, 375+35);
				_buttons[i] = new BumpingButton(null, counterButton[i]);
				_buttons[i].setHitBox(new Rectangle(160, 375, 310, 65));
				_buttons[i].setActionCommand("single");
				break;
			case Buttons.VersusMode:
				counterButton[i].setRefPixelPosition(120, 450+35);
				_buttons[i] = new BumpingButton(null, counterButton[i]);
				_buttons[i].setHitBox(new Rectangle(160, 450, 310, 65));
				_buttons[i].setActionCommand("versus");
				break;
			case Buttons.Credits:
				counterButton[i].setRefPixelPosition(120, 525+35);
				_buttons[i] = new BumpingButton(null, counterButton[i]);
				_buttons[i].setHitBox(new Rectangle(160, 525, 310, 65));
				_buttons[i].setActionCommand("credits");
				break;
			}
			_buttons[i].addActionListener(listener);
		}
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
		_background = new Sprite("MainMenu");
	}

	public void onExit()
	{
		_background = null;
	}
	
	public void update(float dt)
	{
		for (int i=0; i<Buttons.count; ++i)
			_buttons[i].update(dt);
	}

	public void paint(Graphics2D g2)
	{
		// Draw background.
		_background.paint(g2);
		
		for (int i=0; i<Buttons.count; ++i)
			_buttons[i].paint(g2);
	}

	/** Called when a mouse button press is detected.
	 * @param x			The X coordinates of the mouse at press time.
	 * @param y			The Y coordinates of the mouse at press time.
	 * @param button	The mouse button pressed.
	 */
	public void onMouseDown(int x, int y, int button)
	{
		for (int i=0; i<Buttons.count; ++i)
			_buttons[i].onMouseDown(x, y, button);
	}

	/** Mouse motion.
	 * @param x			The X coordinates of the cursor in the window.
	 * @param y			The Y coordinates of the cursor in the window.
	 */
	public void onMouseMove(int x, int y)
	{
		for (int i=0; i<Buttons.count; ++i)
			_buttons[i].onMouseMove(x, y);
		
		// Change cursor.
		boolean overClickableItem = false;
		for (int i=0; i<Buttons.count; ++i)
			overClickableItem |= _buttons[i].isMouseOver();
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

// Operations

// Implementation
	private class PushButtonActionListener implements ActionListener 
	{
		public void actionPerformed(ActionEvent e) {
			sendMessageToController( e.getActionCommand() );
		}
	}

	private interface Buttons
	{
		public static final int
				SingleMode = 0,
				VersusMode = 1,
				Credits = 2,
				count = 3;
	}
	
	// GUI
	private boolean		_overClickableItem;
	private Sprite		_background;
	private BumpingButton	_buttons[];
	private Cursor		_defaultCursor = new Cursor(Cursor.DEFAULT),
						_handCursor = new Cursor("HandCursor", 6, 5);
}
