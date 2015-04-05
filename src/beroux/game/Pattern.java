package beroux.mountnfall;
import beroux.game.*;
import java.awt.Graphics;

/** Render a moving pattern.
 * The background is made of a pattern repeated horizontally and verically.
 * By calling "translate" you can translate the background.
 */

class Pattern
{
// Construction
	public Pattern(Sprite pattern)
	{
		create(pattern);
	}
	
	/** Change the pattern of the "white" squares.
	 * @param pattern	The image that will be repeated to form background.
	 * @note Both images must be ize of Size*Size.
	 */
	public boolean create(Sprite pattern)
	{
		_pattern = pattern;

		_originX = 0.0f;
		_originY = 0.0f;

		return true;
	}
	
// Attributes
	
// Operations
	/** Translate the background.
	 * If you call twice this function, the translation's length
	 * will be doubled.
	 * @param dx	Translation over X.
	 * @param dy	Translation over Y.
	 */
	public void translate(float dx, float dy)
	{
		float width = _pattern.getWidth(),
			  height = _pattern.getHeight();
		
		// Find the starting render position
		// in order to render the entire background
		// starting from those positions and only incrementing
		// over x and y.

		_originX += dx;
		while (_originX > 0.0f)
			_originX -= width;
		while (_originX < -width)
			_originX += width;

		_originY += dy;
		while (_originY > 0.0f)
			_originY -= height;
		while (_originY < -height)
			_originY += height;
	}

	/** Render the patten.
	 */
	public void paint(Graphics g)
	{
		float width = _pattern.getWidth(),
			  height = _pattern.getHeight();

		if (_pattern == null)
			return;

		// Render the background.
		for (int x=Math.round(_originX); x<Screen.width(); x+=width)
			for (int y=Math.round(_originY); y<Screen.height(); y+=height)
			{
				_pattern.setRefPixelPosition(x, y);
				_pattern.paint(g);
			}
	}

// Implementation
	protected Sprite	_pattern;
	protected float	_originX, _originY;
}
