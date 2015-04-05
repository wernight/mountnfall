package beroux.mountnfall;

abstract class Player implements java.io.Serializable
{
// Construction
	Player(String name)
	{
		_name = name;
	}

// Attributes
	public String getName()
	{
		return _name;
	}

// Operations
	public String toString()
	{
		return _name;
	}

// Overridables
	// Tell the player that it's his turn to play.
	public void onPlay(GameLogic logic)
	{
	}
	
	// Request the player's move choice. Can be null if player hasn't choosen yet.
	public abstract Move getPlayed();

// Implementation
	private String _name;
}
