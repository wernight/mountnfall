package beroux.game;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import javax.imageio.ImageIO;
import java.lang.CloneNotSupportedException;

/** 2D image that can be renderer on screen.
 */
public final class Sprite extends Layer implements Cloneable
{
// Construction
	/** Create a new sprite.
	 * The reference pixel is by default (0,0).
	 * @param uri	Location of the image.
	 */
	public Sprite(String uri)
	{
		_uri = uri;

		// Load image
		Image img; 
		if ((img = load("data/"+uri+".gif")) == null)
			img = load("data/"+uri+".jpg");
		_image = img;

		if (_image != null)
		{
			_width = _image.getWidth(null);
			_height = _image.getHeight(null);
		}
		else
		{
			_width = _height = -1;
			throw new RuntimeException("Could not load sprite `"+uri+"'");
		}
	}
	
// Attributes
	/** Defines the reference pixel for this Sprite.
	 * @param x	The horizontal location of the reference pixel, relative to the left edge.
	 * @param y The vertical location of the reference pixel, relative to the top edge.
	 */
	public void defineReferencePixel(int x, int y)
	{
		_refX = x;
		_refY = y;
	} 

	/** Return a java Image for the screen.
	 */
	Image getImage()
	{
		return _image;
	}
	
    /** Gets the horizontal position of this Sprite's reference pixel in the painter's coordinate system.
	 */
	public int getRefPixelX()
	{
		return _refX;
	}
	
    /** Gets the vertical position of this Sprite's reference pixel in the painter's coordinate system.
	 */
	public int getRefPixelY()
	{
		return _refY;
	}

    /** Sets this Sprite's position such that its reference pixel is located at (x,y) in the painter's coordinate system.
	 * @param x the horizontal location at which to place the reference pixel
	 * @param y the vertical location at which to place the reference pixel
	 */
	public void setRefPixelPosition(int x, int y)
	{
		_x = x - _refX;
		_y = y - _refY;
	}
	
// Operations
	public String toString()
	{
		return _uri;
	}

	public final void paint(Graphics g)
	{
		g.drawImage(_image, _x, _y, null);
	}

	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new InternalError();
		}
	}

// Implementation
	/** Retrieve a sprite from the store.
	 * @param ref The reference to the image to use for the sprite
	 */
	private Image load(String ref)
	{
		Canvas mainFrame = Screen.getInstance().getCanvas();
		
		URL url = mainFrame.getClass().getResource(ref);
		if (url == null)
			return null;
		Image image = mainFrame.getToolkit().getImage(url);
		try {
			MediaTracker tracker = new MediaTracker(mainFrame);
			tracker.addImage(image, 0);
			tracker.waitForID(0);
		} catch (Exception e) { return null; }
		
		return image;

		/** Previous way:
		 * - doesn't support animated gifs
		 * - slower (wtf? it should be faster!).

		// Go away and grab the sprite from the resource loader
		BufferedImage sourceImage = null;
		
		try
		{
			// The ClassLoader.getResource() ensures we get the sprite
			// from the appropriate place, this helps with deploying the game
			// with things like webstart. You could equally do a file look
			// up here.
			URL url = this.getClass().getClassLoader().getResource(ref);
			if (url == null)
				return null;	// fail("Can't find ref: "+ref);
			
			// use ImageIO to read the image in
			sourceImage = ImageIO.read(url);
		}
		catch (IOException e)
		{
			return null;	// fail("Failed to load: "+ref);
		}
		
		// create an accelerated image of the right size to store our sprite in
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		Image image = gc.createCompatibleImage(sourceImage.getWidth(),sourceImage.getHeight(),Transparency.BITMASK);
		
		// draw our source image into the accelerated image
		image.getGraphics().drawImage(sourceImage, 0, 0, null);

		return image;
		*/
	}
	
	private final String _uri;
	private final Image _image;
	private int _refX, _refY;
}
