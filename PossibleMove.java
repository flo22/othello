/**
 * This class represents the possible moves of a player. It stores and
 * administers the positions.
 * @author Florian Mueller
 */
public class PossibleMove implements Comparable<PossibleMove> {

	/** column of the possible move */
	private char column;

	/** line of the possible move */
	private int line;

	/**
	 * Constructs a new possible move with column and line.
	 * @param column column of the possible move
	 * @param line line of the possible move
	 */
	public PossibleMove(char column, int line) {
		this.column = column;
		this.line = line;
	}

	/**
	 * Returns the column of the possible move.
	 * @return the column of the possible move
	 */
	public char getColumn() {
		return this.column;
	}

	/**
	 * Returns the line of the possible move.
	 * @return the line of the possible move
	 */
	public int getLine() {
		return this.line;
	}

	/**
	 * Returns the position of the possible move.
	 * @return the position of the possible move
	 */
	public String toString() {
		return this.column + Integer.toString(line);
	}

	/**
	 * Compares two possible move objects.
	 * @param other other possible move
	 * @return result of the compare
	 */
	public int compareTo(PossibleMove other) {
		int result = 0;
		if (other != null && !this.equals(other)) {
			if (this.column > other.column) {
				result = -1;
			} else if (this.column < other.column) {
				result = 1;
			} else if (this.line > other.line) {
				result = -1;
			} else {
				result = 1;
			}
		}
		return result;
	}

	/**
	 * Check if two possible moves are equally.
	 * @param other possible move
	 * @return {@code true} if both are equally, {@code false} otherwise
	 */
	public boolean equals(PossibleMove other) {
		return this.column == other.column && this.line == other.line;
	}

}
