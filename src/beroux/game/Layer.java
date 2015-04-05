/** A Layer is an public abstract class representing a visual element of a game.
 * Taken from MIDP 2.0 javax.microedition.lcdui.game.Layer:
 * 
 * Each Layer has position (in terms of the upper-left corner of
 * its visual bounds), width, height, and can be made visible or
 * invisible. Layer subclasses must implement a paint(Graphics)
 * method so that they can be rendered.
 *
 * The Layer's (x,y) position is always interpreted relative to the
 * coordinate system of the Graphics object that is passed to the
 * Layer's paint() method. This coordinate system is referred to 
 * as the painter's coordinate system. The initial location of a
 * Layer is (0,0).
 */

package beroux.game;
import java.awt.Rectangle;
import java.awt.Graphics;

public abstract class Layer
{
// Attributes
	// Gets the current height of this layer, in pixels.
	public int getHeight() {
		return _height;
	}

	// Gets the current width of this layer, in pixels.
	public int getWidth() {
		return _width;
	}

	// Gets the horizontal position of this Layer's upper-left corner in the painter's coordinate system.
	public int getX() {
		return _x;
	}

	// Gets the vertical position of this Layer's upper-left corner in the painter's coordinate system.
	public int getY() {
		return _y;
	}

	// Gets the visibility of this Layer.
	public boolean isVisible() {
		return _visible;
	}

	// Sets this Layer's position such that its upper-left corner is located at (x,y) in the painter's coordinate system.
	public void setPosition(int x, int y) {
		_x = x;
		_y = y;
	}

	// Sets the visibility of this Layer.
	public void setVisible(boolean visible) {
		_visible = visible;
	}

// Operations
	// Moves this Layer by the specified horizontal and vertical distances.
	public void move(int dx, int dy) {
		_x += dx;
		_y += dy;
	}

	public Rectangle getBoundingBox()
	{
		return new Rectangle(_x, _y, _width, _height);
	}

// Overridables
	// Paints this Layer if it is visible.
	public abstract void paint(Graphics g);

// Implementation
	protected int 	_x,
			  		_y,
					_width,
					_height;
	private boolean	_visible;
}
