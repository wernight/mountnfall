package beroux.mountnfall;

/** Depile a tower
 * @is.Immutable
 */
final class DepileMove extends Move
{
	// Tower to depile.
	public final Board.Square	tower;

	// Depile direction.
	public final int		dx, dy;

	public DepileMove(Board.Square tower, int dx, int dy)
	{
		this.tower = tower;
		
		// Force unary displacement
		if (dx > 0)
			this.dx = 1;
		else if (dx < 0)
			this.dx = -1;
		else
			this.dx = 0;

		if (dy > 0)
			this.dy = 1;
		else if (dy < 0)
			this.dy = -1;
		else
			this.dy = 0;
	}
	
	public DepileMove(Board.Square tower, Board.Square destination)
	{
		this(tower, destination.column() - tower.column(), destination.row() - tower.row());
	}
	
	public DepileMove(int towerRow, int towerColumn, int dx, int dy)
	{
		this(new Board.Square(towerRow, towerColumn), dx, dy);
	}

	public String toString()
	{
		return "GameCommand -> Move -> DepileMove tower at "+tower+" in "+(dx>0?"south":dx<0?"north":"")+" "+(dy>0?"east":dy<0?"west":"")+" direction.";
	}
	
	public boolean equals(Object o)
	{
		if (o == null || o.getClass() != DepileMove.class)
			return false;

		DepileMove move = (DepileMove) o;
		
		return tower.equals(move.tower) &&
			dx == move.dx &&
			dy == move.dy;
	}
}
