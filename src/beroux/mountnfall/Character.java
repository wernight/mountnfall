package beroux.mountnfall;
import java.awt.Graphics2D;

/** Simple animated character in game.
 * Draws a game character/actor.
 * The character has different animations sequences that can be
 * started any time.
 */
public class Character implements IGameObject
{
// Construction
	/** Create the character.
	 * @param characterXml	Character's XML data file.
	 */
	public Character(String characterXml)
	{
	}

// Attributes

// Operations
	// Update
	public void update(float dt)
	{
		// TODO
	}

	// Render
	public void paint(Graphics2D g)
	{
		// TODO
	}

	/** Change character's state.
	 * Each state can contain specific animations.
	 * @param stateName	State's name in the XML data file.
	 * @return True if state found, else go to default state.
	 */
	public boolean gotoState(String stateName)
	{
		// TODO
		return false;
	}

// Implementation

}
