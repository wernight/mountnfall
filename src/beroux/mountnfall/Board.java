package beroux.mountnfall;
import java.util.NoSuchElementException;
import java.io.*;

/** Classical 8x8 game board.
 * Game board meant for 2 players (one playing whites and the other playing blacks).
 * In each square, one or more counters <b>of the same colour</b> can be piled up.
 */
public final class Board implements Cloneable, Serializable
{
	/** Locate a square on the board.
	 * @is.Immutable
	 */
	public static final class Square
	{
	// Construction	
		public Square(int row, int column)
		{
			_row = row;
			_column = column;
		}

	// Attributes
		public int row()
		{
			return _row;
		}

		public int column()
		{
			return _column;
		}

		public boolean isInsideBoard()
		{
			return (_row >= 0 && _row < Board.rows() &&
				_column >= 0 && _column < Board.columns());
		}
		
	// Opeartions
		public Square shift(int rows, int columns)
		{
			return new Square(_row+rows, _column+columns);
		}
		
		public boolean equals(Object o)
		{
			if (o == null || o.getClass() != Square.class)
				return false;
			Square sq = (Square) o;
			return _row == sq._row && _column == sq._column;
		}

		public String toString()
		{
			return (char)('A'+_column) + "" + (_row+1);
		}

	// Implementation
		// Position of a square in the game's board.
		private final int _row, _column;
	}

	/** Iterator through all the board squares.
	 * Works almost like an iterator not supporting a remove() operation
	 * but next() returns explicitely Squares.
	 */
	public static class SquaresIterator
	{
	// Construction
		public SquaresIterator()
		{
			_row = Board.rows() - 1;
			_column = Board.columns() - 1;
		}

	// Attributes
		// Returns true if the iteration has more elements.
		public boolean hasNext()
		{
			return _row-1 >= 0 || _column-1 >= 0;
		}

	// Operations
		// Returns the next element in the iteration.
		public Square next() throws NoSuchElementException
		{
			Square sq = new Square(_row, _column);

			if (--_column < 0)
			{
				if (_row-- < 0)
					throw new NoSuchElementException();
				_column = Board.columns() - 1;
			}
			return sq;
		}

	// Implementation
		private int _row,
					_column;
	}

	/** Counter(s) inside of a board square.
	 * If square is empty, then count is null,
	 * else count defines how many counter of the colour are present in the square.
	 *
	 * @is.Immutable
	 */
	public static final class Counter implements Serializable
	{
		public static interface Colour
		{
			static final int EMPTY = -1,	// <=> count == 0
						 BLACK = 0,
						 WHITE = 1;
		}

	// Construction
		public Counter(int count, int colour)
		{
			if (count == 0 || colour == Colour.EMPTY) {
				_count = 0;
				_colour = Colour.EMPTY;
			} else {
				_count = count;
				_colour = colour;
			}
		}
	
	// Attributes
		public int getCount() {
			return _count;
		}

		public int getColour() {
			return _colour;
		}

	// Operations
		public boolean equals(Object o)
		{
			if (o == null || o.getClass() != getClass())
				return false;
			Counter c = (Counter) o;
			return _count == c.getCount() &&
				(_count == 0 || _colour == c.getColour());
		}

	// Implementation
		private final int _count;	// Number of counter in the square.
		private final int _colour;	// Colour of the counters.
	}
	
// Construction
	public static final Counter emptyCounter = new Counter(0, Counter.Colour.EMPTY);
	public static final Counter whiteCounter = new Counter(1, Counter.Colour.WHITE);
	public static final Counter blackCounter = new Counter(1, Counter.Colour.BLACK);
	
	public Board()
	{
		_counters = new Counter[8*8];
		clear();
	}
	
// Attributes
	// Number of rows.
	public static final int rows()
	{
		return 8;
	}
	
	// Number of columns.
	public static final int columns()
	{
		return 8;
	}

	/** Return counter at the specified square.
	 * Because it is immutable, a reference of the counter can be
	 * used (no need to clone).
	 */
	public Counter getCounter(Square pos)
	{
		return _counters[(pos.row()<<3) + pos.column()];
	}
	
	/** Define counter at the specified square.
	 * Because it is immutable, a reference of the counter can be
	 * used (no need to clone).
	 */
	public void setCounter(Square pos, Counter c)
	{
		_counters[(pos.row()<<3) + pos.column()] = c;
	}

// Operations
	// Remove all the counter from the board.
	public void clear()
	{
		for (int i=_counters.length-1; i>=0; --i)
			_counters[i] = emptyCounter;
	}

	public Object clone()
	{
		try
		{
			Board clone = (Board) super.clone();

			clone._counters = new Counter[_counters.length];
			System.arraycopy(_counters, 0, clone._counters, 0, _counters.length);
			
			return clone;
		}
		catch (CloneNotSupportedException e)
		{
			throw new InternalError();
		}
	}

	public boolean equals(Object o)
	{
		if (o == null || o.getClass() != getClass())
			return false;
		Board b = (Board) o;
		SquaresIterator iter = new SquaresIterator();
		while (iter.hasNext())
		{
			Square sq = iter.next();
			if (!getCounter(sq).equals(b.getCounter(sq)))
				return false;
		}
		return true;
	}

// Implementation
	private Counter[]	_counters;
}
