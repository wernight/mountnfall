package beroux.mountnfall;
import java.awt.Graphics2D;

interface IGameObject
{
	/** Update the object.
	 * If no update happens, the rend function should always render the same image.
	 * @param dt	Time difference since last update.
	 */
	void update(float dt);

	/** Render the object on screen.
	 */
	void paint(Graphics2D g);
}
