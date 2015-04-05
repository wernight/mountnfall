package beroux.game;
import java.awt.*;
import java.awt.font.*;

/** Display text in game.
 */
public class GameFont
{
// Construction
	public GameFont(Font font, Color colour)
	{
		_font = font;
		_fontColour = colour;
	}
	
// Attributes
	public Font getFont()
	{
		return _font;
	}

	public Color getColour()
	{
		return _fontColour;
	}

	public boolean isOutlined()
	{
		return _outlineBorder > 0.0f;
	}

	public float getOutlineSize()
	{
		return _outlineBorder;
	}

	public Color getOutlineColour()
	{
		return _outlineColour;
	}
	
	/** Change font.
	 */
	public void setFont(Font font, Color colour)
	{
		_fontColour = colour;
		_font = font;
	}

	/** Define font outline.
	 * @param border	Size of the outline.
	 * @param colour	Outline colour.
	 */
	public void setOutline(float border, Color colour)
	{
		_outlineBorder = border;
		_outlineColour = colour;
	}

// Operations
	/** Return the length of the string.
	 */
	public int stringWidth(String str)
	{
		FontMetrics fm = Screen.getInstance().getGraphics().getFontMetrics(_font);
		return fm.stringWidth(str);
	}
	
// Implementation
	private Font			_font;
	private Color			_fontColour;
	private float			_outlineBorder;
	private Color			_outlineColour;
}

