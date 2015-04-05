package beroux.mountnfall;

/** Start a new game.
 * @is.Immutable
 */
final class NewGame extends GameCommand
{
	// Players' names.
	public final String 	whitePlayerName,
							blackPlayerName;
	public final int		startingPlayer;

	public NewGame(String whitePlayerName, String blackPlayerName, int startingPlayer)
	{
		this.whitePlayerName = whitePlayerName;
		this.blackPlayerName = blackPlayerName;
		this.startingPlayer = startingPlayer;
	}

	public String toString()
	{
		return "GameCommand -> NewGame(\""+whitePlayerName+"\", \""+blackPlayerName+"\").";
	}
}
