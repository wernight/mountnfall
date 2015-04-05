package beroux.game;
import java.applet.*;
import java.awt.*;	// For Graphics, etc.
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.font.*;

/** Virtual screen to display graphics on.
 * The screen resolution doesn't have to be equal to the real
 * screen's resolution.
 *
 * @is.Singleton
 */
public class Screen
{
// Construction
	Screen()
	{
		_ratio = 1.0f;
	}

	public static Screen getInstance()
	{
		if (_instance == null)
			_instance = new Screen();
		return _instance;
	}
	
// Attributes
	/** Virtual screen width.
	 */
	public static final int width()
	{
		return 640;
	}

	/** Virtual screen height.
	 */
	public static final int height()
	{
		return 640;
	}

	public float getRatio()
	{
		return _ratio;
	}

	public void setRatio(float screenRatio)
	{
		_ratio = screenRatio;
	}

// Opeartions
	/** Change the screen cursor.
	 * @param cursor	A cursor.
	 */
	public void setCursor(Cursor cursor)
	{
		_mainFrame.setCursor( cursor.getAwtCursor() );
	}

	/** Draw a text string.
	 * @param font	Game font.
	 * @param str	String to display.
	 * @param x		X-coord on screen.
	 * @param y		Y-coord on screen.
	 */
	public void drawString(GameFont font, String str, float x, float y)
	{
		if (font.isOutlined())
		{
			FontRenderContext frc = _g2d.getFontRenderContext();
			TextLayout tl = new TextLayout(str, font.getFont(), frc);
			Shape shape = tl.getOutline(
				AffineTransform.getTranslateInstance(x, y));

			_g2d.setFont(font.getFont());
			_g2d.setColor(font.getOutlineColour());
			_g2d.setStroke(new BasicStroke(font.getOutlineSize()));
			_g2d.draw(shape);
			
			_g2d.setColor(font.getColour());
			_g2d.fill(shape);
		}
		else
		{
			_g2d.setFont(font.getFont());
			_g2d.setColor(font.getColour());
			_g2d.drawString(str, Math.round(x), Math.round(y));
		}
	}

// OS Dependant
	public void setCanvas(Canvas mainFrame) {
		_mainFrame = mainFrame;
	}

	public Canvas getCanvas() {
		return _mainFrame;
	}

	// Define graphic on witch to draw on.
	public void setGraphics(Graphics2D g2d)
	{
		_g2d = g2d;
	}

	Graphics2D getGraphics()
	{
		return _g2d;
	}

// Implementation
	private float			_ratio;
	private Canvas			_mainFrame;
	private Graphics2D		_g2d;
	private Sprite			_cursor;
	private static Screen	_instance = new Screen();
}
