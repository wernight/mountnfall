package beroux.mountnfall;
import beroux.game.*;
import java.util.Random;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/** Render a fort's tower.
 * The tower represents the number of counters left for the current player.
 * The more less he has counter, the lower the tower will be.
 */
class Tower implements IGameObject
{
// Construction
	/** Create the tower.
	 * @param x				X position of the center of the tower.
	 * @param y				Y position of the base of the tower.
	 * @param tower			Tower image.
	 * @param grassBefore	Grass at the bottom of the tower and in foreground.
	 * @param grassBehind	Grass at the bottom of the tower by behind the tower.
	 */
	public Tower(int x, int y, Sprite tower, Sprite grassBefore, Sprite grassBehind)
	{
		_x = x;
		_y = y;
		_tower = tower;
		_grassFg = grassBefore;
		_grassFg.setRefPixelPosition(x, y);
		_grassBg = grassBehind;
		_grassBg.setRefPixelPosition(x, y);
		setInitialHeight(_tower.getHeight());
		SHAKING = _tower.getWidth() * 0.0006f;
	}
	
// Attributes
	/** Tower's maximum height.
	 * By default it's the height of the tower image.
	 * @param height	Tower's height in pixels.
	 */
	public void setInitialHeight(int height)
	{
		_currentHeight = _finalHeight = height;
	}
	
	/** Define tower's height.
	 * @param scale	[0.0 - 1.0] value. 1.0 = Maximum height.
	 */
	public void setScale(float scale)
	{
		_finalHeight = scale*_tower.getHeight();
	}

	// Tower's base position.
	public float getBaseX()
	{
		return _x;
	}

	// Tower's base position.
	public float getBaseY()
	{
		return _y;
	}

	// Current tower's height in pixels.
	public float getHeight()
	{
		return _currentHeight;
	}

// Operations
	public void update(float dt)
	{
		final float minSpeed = 10.0f;

		float speed = _currentHeight - _finalHeight;
		if (speed < minSpeed)
			speed = minSpeed;
		
		if (_currentHeight > _finalHeight)
			_currentHeight -= speed * dt;
		if (_currentHeight < _finalHeight)
			_currentHeight = _finalHeight;
		
		int x = Math.round(_x + (float)(_rand.nextInt()%0xFFFF)/0xFFFF * SHAKING * (_finalHeight - _currentHeight));
		_tower.setRefPixelPosition(x, Math.round(_y - _currentHeight));
	}
	
	public void paint(Graphics2D g)
	{
		// Make it shaking when going down.
		_grassBg.paint(g);
		if (_currentHeight > 0)
		{
			// Start scissor
			g.setClip(new Rectangle(0, 0, Screen.width(), Math.round(_y)));

			// Render tower
			_tower.paint(g);

			// Disable scissor
			// TODO: Better way?
			g.setClip(new Rectangle(0, 0, Screen.width(), Screen.height()));
		}
		_grassFg.paint(g);
	}

// Implementation
	private float	_x, _y;			// Position of the tower.
	private Sprite	_tower,			// Tower
					_grassFg,		// Grass before the tower.
					_grassBg;		// Grass behind the tower.
	private float	_finalHeight,
					_currentHeight;
	private final float SHAKING;
	private static Random _rand = new Random();
}
