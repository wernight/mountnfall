package beroux.game;
import java.awt.Toolkit;
import java.awt.Point;

/** Mouse cursor.
 * @is.Immutable
 */
public final class Cursor
{
	public static final int
		DEFAULT = 0,
		HAND = 1,
		WAIT = 2,
		TEXT = 3;
	
	public Cursor(int predefinedCursor)
	{
		switch (predefinedCursor)
		{
		case HAND:
			_awtCursor = java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR);
			break;

		case WAIT:
			_awtCursor = java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR);
			break;

		case TEXT:
			_awtCursor = java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.TEXT_CURSOR);
			break;

		case DEFAULT:
		default:
			_awtCursor = java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR);
		}
	}

	public Cursor(String uri, int hotSpotX, int hotSpotY)
	{
		Sprite spr = new Sprite(uri);

		_awtCursor = Toolkit.getDefaultToolkit().createCustomCursor(
			spr.getImage(), new Point(hotSpotX, hotSpotY), spr.toString());
	}

// OS Dependant
	public java.awt.Cursor getAwtCursor()
	{
		return _awtCursor;
	}

// Implementation
	private final java.awt.Cursor _awtCursor;
}
