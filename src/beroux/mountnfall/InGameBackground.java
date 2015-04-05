package beroux.mountnfall;
import beroux.game.*;
import java.awt.Graphics2D;

/** An accelerated version of Background.
 * Doesn't render the background behind the game's board.
 */
class InGameBackground extends Pattern
{
	public InGameBackground(Sprite pattern)
	{
		super(pattern);
	}
	
	public void paint(Graphics2D g)
	{
		float width = _pattern.getWidth(),
			  height = _pattern.getHeight();

		if (_pattern == null)
			return;

		// Render the background.
		for (int x=Math.round(_originX); x<Screen.width(); x+=width)
			for (int y=Math.round(_originY); y<_grassTop; y+=height)
			{
				if (x > _boardLeft && x+width < _boardRight &&
					y > _boardTop && y+height < _boardBottom)
					continue;
				
				_pattern.setRefPixelPosition(x, y);
				_pattern.paint(g);
			}
	}

	private static final int
		_boardLeft = 20*Screen.width()/100,
		_boardRight= Screen.width() - _boardLeft,
		_boardTop = 20*Screen.height()/100,
		_boardBottom = Screen.height() - _boardTop;
	private static final int
		_grassTop = 400*Screen.height()/640;
}
