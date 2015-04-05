package beroux.mountnfall;

/** Pile up counter to for a tower or increase a tower.
 * @is.Immutable
 */
final class PileUpMove extends Move
{
	// Counter to pile up.
	public final Board.Square	counter;

	// Rock/counter to pile up on.
	public final Board.Square	destination;

	public PileUpMove(Board.Square counter, Board.Square destination)
	{
		this.counter = counter;
		this.destination = destination;
	}
	
	public String toString()
	{
		return "GameCommand -> Move -> PileUpMove from "+counter+" to "+destination+".";
	}

	public boolean equals(Object o)
	{
		if (o == null || o.getClass() != PileUpMove.class)
			return false;

		PileUpMove move = (PileUpMove) o;
		
		return counter.equals(move.counter) &&
			destination.equals(move.destination);
	}
}
