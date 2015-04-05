package beroux.game;
import java.lang.*;
import java.awt.*;

public class FontString extends Layer implements Cloneable
{
// Construction
	public FontString(String fontUri) {
		_width = 0;
		_height = 0;
		_sprites = new Sprite[128];

		// Load other sprites
		for (int i=0; i<_sprites.length; ++i)
		{
			// Try to load sprite
			try
			{
				if (i == 0)
					_sprites[0] = new Sprite(fontUri+"/default");	// Default sprite
				else
				{
					String spriteUri = Integer.toString(i, 16).toUpperCase();
					while (spriteUri.length() < 4)
						spriteUri = "0" + spriteUri;
					_sprites[i] = new Sprite(fontUri+"/"+spriteUri);
				}
			}
			catch (RuntimeException e)
			{
				if (i == 0)
					throw new RuntimeException("Could not load font "+fontUri);
				else
					// Use default sprite if couldn't load it
					_sprites[i] = _sprites[0];
			}
			
			// Set bottom left pixel as reference pixel
			_sprites[i].defineReferencePixel(0, _sprites[i].getHeight());
			
			// Find maximum sprite height
			if (_sprites[i].getHeight() > _height)
				_height = _sprites[i].getHeight();
		}

		// Caption
		setString("");
	}

// Attributes
	public void setString(String caption) {
		_caption = caption;

		// Calculate width
		_width = 0;
		for (int i=0; i<_caption.length(); ++i)
		{
			// Get sprite corresponding to the character
			int sprite = _caption.charAt(i);
			if (sprite >= _sprites.length)
				sprite = 0;

			// Increment width
			_width += _sprites[sprite].getWidth();
		}
	}

	public String getString() {
		return _caption;
	}

// Operations
	// Paints this Layer if it is visible.
	public void paint(Graphics g) {
		int x = _x;

		// Draw string
		for (int i=0; i<_caption.length(); ++i)
		{
			// Get sprite corresponding to the character
			int sprite = _caption.charAt(i);
			if (sprite >= _sprites.length)
				sprite = 0;

			// Paint the character's sprite
			_sprites[sprite].setRefPixelPosition(x, _y);
			_sprites[sprite].paint(g);
			x += _sprites[sprite].getWidth();
		}
	}

	public Object clone()
	{
		try
		{
			FontString clone = (FontString) super.clone();
			clone._sprites = new Sprite[_sprites.length];
			System.arraycopy(_sprites, 0, clone._sprites, 0, _sprites.length);
			return clone;
		}
		catch (CloneNotSupportedException e)
		{
			throw new InternalError();
		}
	}

// Implementation
	private Sprite	_sprites[];
	private String	_caption;
}
