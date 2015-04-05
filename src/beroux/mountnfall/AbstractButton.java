package beroux.mountnfall;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.event.*;
import javax.swing.event.EventListenerList;

abstract class AbstractButton implements IGameObject
{
// Construction
	/** Create the button.
	 * @param x		Center x position of the button.
	 * @param y		Center y position of the button.
	 * @param caption	Button text always displayed. The reference pixel should be the center of the sprite.
	 * @param circle	Animated sprite in background. The reference pixel should be the center of the sprite.
	 */
	public AbstractButton(int x, int y)
	{
		_x = x;
		_y = y;
		_mouseOver = false;
		_actionCommand = "";
		_hitBox = new Rectangle(0,0,0,0);
	}
	
// Attributes
	/** Sets the action command for this button.
	 * @param actionCommand the action command for this button
	 */
	public void setActionCommand(String actionCommand)
	{
		_actionCommand = actionCommand;
	}

	/** Returns the action command for this button.
	 * @return the action command for this button
	 */
	public String getActionCommand()
	{
		return _actionCommand;
	}

	// Adds an ActionListener to the button.
	public void addActionListener(ActionListener l)
	{
		_listenerList.add(ActionListener.class, l);
	}

	public boolean isMouseOver()
	{
		return _mouseOver;
	}

	public void setHitBox(Rectangle hitBox)
	{
		_hitBox = hitBox;
	}
	
// Operations
	/** Called when a mouse button press is detected.
	 * @param coord		The X/Y coordinates of the mouse at press time.
	 * @param button	The mouse button pressed.
	 */
	public void onMouseDown(int x, int y, int button)
	{
		if (_hitBox.contains(x, y))
			fireActionPerformed(new ActionEvent(this, 0, null));
	}

	/** Mouse motion.
	 */
	public void onMouseMove(int x, int y)
	{
		// Define if mouse if over button or not.
		_mouseOver = _hitBox.contains(x, y);
	}

	/** Update the object.
	 * If no update happens, the rend function should always render the same image.
	 * @param dt	Time difference since last update.
	 */
	public abstract void update(float dt);

	/** Render the object on screen.
	 */
	public abstract void paint(Graphics2D g2);

// Implementation
    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the <code>event</code> 
     * parameter.
     *
     * @param event  the <code>ActionEvent</code> object
     * @see EventListenerList
     */
    protected void fireActionPerformed(ActionEvent event)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = _listenerList.getListenerList();
		ActionEvent e = null;

		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length-2; i>=0; i-=2)
		{
			if (listeners[i]==ActionListener.class)
			{
				// Lazily create the event:
				if (e == null)
				{
					String actionCommand = event.getActionCommand();
					if(actionCommand == null)
						actionCommand = getActionCommand();
					e = new ActionEvent(this,
							ActionEvent.ACTION_PERFORMED,
							actionCommand,
							event.getWhen(),
							event.getModifiers());
				}
				((ActionListener)listeners[i+1]).actionPerformed(e);
			}          
		}
    }

	protected final int		_x, 
							_y;
	protected boolean		_mouseOver;
	private EventListenerList _listenerList = new EventListenerList();
	private String			_actionCommand;
	private Rectangle		_hitBox;
}
