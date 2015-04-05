package beroux.mountnfall;
import beroux.game.*;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.event.*;
import javax.swing.event.EventListenerList;

class BumpingButton extends AbstractButton
{
// Construction
	/** Create the button.
	 * @param caption	Button text always displayed.
	 * @param bump		Animated sprite in background.
	 */
	public BumpingButton(Sprite caption, Sprite bump)
	{
		super(bump.getX() + bump.getRefPixelX(), bump.getY() + bump.getRefPixelY());
		_caption = caption;
		_bump = bump;
	}
	
// AbstractButton
	/** Update the object.
	 * If no update happens, the rend function should always render the same image.
	 * @param dt	Time difference since last update.
	 */
	public void update(float dt)
	{
		if (_mouseOver)
		{
			_time += dt;
			_scale = (float)( Math.cos(_time*16.0f - Math.PI)*Math.exp(-_time*3.00f) + 1.0f );
		}
		else
		{
			_scale = (float)( Math.max(0.0f, _scale - 8.0f*_scale*dt) );
			_time = 0.0f;
		}
	}

	/** Render the object on screen.
	 */
	public void paint(Graphics2D g2)
	{
		if (_scale*_bump.getWidth() > 1.0f &&
			_scale*_bump.getHeight() > 1.0f)
		{
			AffineTransform	Tx = g2.getTransform();
			g2.translate(-_x*_scale, -_y*_scale);
			g2.scale(_scale, _scale);
			g2.translate(_x/_scale, _y/_scale);
			
			_bump.paint(g2);
			
			g2.setTransform(Tx);
		}

		if (_caption != null)
			_caption.paint(g2);
	}

// Attributes
	
// Operations

// Implementation
	private final Sprite	_caption,
							_bump;
	private float			_time,
							_scale;
}
