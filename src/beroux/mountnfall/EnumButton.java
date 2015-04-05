package beroux.mountnfall;
import beroux.game.*;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.EventListenerList;

class EnumButton extends AbstractButton
{
// Construction
	/** Create the button.
	 * captionsList and idList must be same size.
	 * @param x		Center x position of the button.
	 * @param y		Center y position of the button.
	 * @param captions	A list of sprites.
	 */
	public EnumButton(int x, int y, TreeMap captions)
	{
		this(x, y, captions, captions.firstKey());
	}
	
	/** Create the button.
	 * captionsList and idList must be same size.
	 * @param x		Center x position of the button.
	 * @param y		Center y position of the button.
	 * @param captions	A list of sprites.
	 * @param activeKey	The active key of the map.
	 */
	public EnumButton(int x, int y, Map captions, Object activeKey)
	{
		super(x, y);

		_captions = captions;
		_activeKey = activeKey;
	}
	
// Attributes
	public void setActiveKey(Object activeKey)
	{
		_activeKey = activeKey;
	}

	public Object getActiveKey()
	{
		return _activeKey;
	}
	
// Operations
	/** Update the object.
	 * If no update happens, the rend function should always render the same image.
	 * @param dt	Time difference since last update.
	 */
	public void update(float dt)
	{
		if (_mouseOver)
		{
			_time += dt;
			_scale = (float)( 0.5f*Math.cos(_time*12.0f - Math.PI/2.0f)*Math.exp(-_time*4.00f) + 1.0f );
		}
		else
		{
			_time = 0.0f;
			_scale = 1.0f;
		}
	}

	/** Render the object on screen.
	 */
	public void paint(Graphics2D g2)
	{
		Sprite caption = (Sprite) _captions.get(_activeKey);

		if (Math.abs(_scale - 1.0f) > 0.001f)
		{
			AffineTransform	Tx = g2.getTransform();
			g2.scale(_scale, _scale);
			g2.translate(_x*1.0f/_scale, _y*1.0f/_scale);
			
			caption.setRefPixelPosition(0, 0);
			caption.paint(g2);

			g2.setTransform(Tx);
		}
		else
		{
			caption.setRefPixelPosition(_x, _y);
			caption.paint(g2);
		}
	}

// Implementation
    protected void fireActionPerformed(ActionEvent event)
	{
		// Next item in enumeration
		Set keysSet = _captions.keySet();
		Iterator iter = keysSet.iterator();
		while (iter.hasNext())
			if (iter.next().equals(_activeKey))
			{
				if (!iter.hasNext())
					iter = keysSet.iterator();
				_activeKey = iter.next();
				break;
			}
		
		// Make it bump
		_time = 0.0f;
		
		super.fireActionPerformed(event);
	}

	private final Map		_captions;
	private Object			_activeKey;
	private float			_time,
							_scale;
}
