package beroux.mountnfall;

/** Game over.
 * @is.Immutable
 */
final class GameOver extends GameCommand
{
	// Game winner's colour.
	public final int winner;

	public GameOver(int winner)
	{
		this.winner = winner;
	}

	public String toString()
	{
		return "GameCommand -> GameOver winner: "+(winner==Board.Counter.Colour.WHITE?"white":"black")+" player.";
	}
}
