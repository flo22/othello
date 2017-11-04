import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class represents the board of the Othello game. It stores and
 * administers the board settings and moves.
 * @author Florian Mueller
 */
public class Board {

	/** playBoard for the othello game */
	private char[][] playBoard;

	/** list of possible moves for the next round */
	private List<PossibleMove> possibleMoves;

	/**
	 * Constructs a new Board with line length and column length. Optionally,
	 * have a standards-setting can be made by the board.
	 * @param lines line length of the board
	 * @param columns column length of the board
	 * @param board optional board
	 */
	public Board(int lines, int columns, List<String> board) {
		if (lines == 0 || lines % 2 != 0 || lines < 2 || lines > 98) {
			throw new IllegalArgumentException("Error! Line length is invalid.");
		} else if (columns == 0 || columns % 2 != 0 || columns < 2 || columns > 26) {
			throw new IllegalArgumentException(
					"Error! Column length is invalid.");
		} else if (board != null) {

			for (String s : board) {
				if (!s.matches("[W,B,#,,-]+")) {
					throw new IllegalArgumentException(
							"Error! Invalid board parameters.");
				} else if (s.length() != columns) {
					throw new IllegalArgumentException(
							"Error! Board columns not equal to columns.");
				}
			}

			if (board.size() != lines) {
				throw new IllegalArgumentException(
						"Error! Board lines not equal to lines.");
			}
		}

		this.playBoard = new char[lines][columns];
		this.possibleMoves = new ArrayList<PossibleMove>();

		if (board == null) {
			this.init();
		} else {
			for (int i = 0; i < playBoard.length; i++) {
				for (int j = 0; j < playBoard[0].length; j++) {
					playBoard[i][j] = board.get(i).charAt(j);
				}
			}
		}
	}

	/**
	 * Set of rectangular holes on the playing board.
	 * @param columnOne column of the first position
	 * @param lineOne line of the first position
	 * @param columnTwo column of the second position
	 * @param lineTwo line of the second position
	 */
	public void setHole(char columnOne, int lineOne, char columnTwo, int lineTwo) {
		if (!this.containsPoint(columnOne, lineOne)) {
			throw new IllegalArgumentException("Error! Point: " + columnOne
					+ Integer.toString(lineOne) + " does not exist.");
		} else if (!this.containsPoint(columnTwo, lineTwo)) {
			throw new IllegalArgumentException("Error! Point: " + columnTwo
					+ Integer.toString(lineTwo) + " does not exist.");
		} else if (!this.isRectangle(columnOne, lineOne, columnTwo, lineTwo)) {
			throw new IllegalArgumentException(
					"Error! The entry is not a rectangle.");
		} else if (!this.holeIsAllowed(columnOne, lineOne, columnTwo, lineTwo)) {
			throw new IllegalArgumentException(
					"Error! Hole is not allowed. Stones between the hole!");
		}

		int colOne = this.getColumn(columnOne);
		int colTwo = this.getColumn(columnTwo);

		for (int i = 0; i < lineTwo - lineOne + 1; i++) {
			for (int j = 0; j < colTwo - colOne + 1; j++) {
				playBoard[lineOne + i - 1][colOne + j] = '#';
			}
		}
	}
	
	/**
	 * Check if hole is allowed at this position.
	 * @param columnOne column of the first position
	 * @param lineOne line of the first position
	 * @param columnTwo column of the second position
	 * @param lineTwo line of the second position
	 * @return {@code true} if hole is allowed, {@code false} otherwise
	 */
	public boolean holeIsAllowed(char columnOne, int lineOne, char columnTwo, int lineTwo) {
		boolean allowed = true;
		int colOne = this.getColumn(columnOne);
		int colTwo = this.getColumn(columnTwo);
		
		if (this.containsPoint(columnOne, lineOne)) {
			if (this.containsPoint(columnTwo, lineTwo)) {
				if (this.isRectangle(columnOne, lineOne, columnTwo, lineTwo)) {
					for (int i = 0; i < lineTwo - lineOne + 1 && allowed; i++) {
						for (int j = 0; j < colTwo - colOne + 1 && allowed; j++) {
							if (playBoard[lineOne + i - 1][colOne + j] == 'W') {
								allowed = false;
							} else if (playBoard[lineOne + i - 1][colOne + j] == 'B') {
								allowed = false;
							}
						}
					}
				} else {
					allowed = false;
				}
			} else {
				allowed = false;
			}
		} else {
			allowed = false;
		}
		return allowed;
	}

	/**
	 * Returns all possible moves for the current player.
	 * @param color color of the current player
	 * @return list of all possible moves
	 */
	public List<PossibleMove> getPossibleMoves(char color) {
		if (color != 'B' && color != 'W') {
			throw new IllegalArgumentException(
					"Error! Invalid color. Expected B or W.");
		}

		List<PossibleMove> posMoves = new ArrayList<PossibleMove>();
		this.findPossibleMoves(color);
		Collections.sort(possibleMoves);
		for (PossibleMove p : possibleMoves) {
			posMoves.add(new PossibleMove(p.getColumn(), p.getLine()));
		}
		return posMoves;
	}

	/**
	 * Moves a stone of the player to the position of the board.
	 * @param column column of the position
	 * @param line line of the position
	 * @param color color of the current player
	 */
	public void moveStone(char column, int line, char color) {
		if (!this.containsPoint(column, line)) {
			throw new IllegalArgumentException("Error! Point does not exist.");
		} else if (color != 'B' && color != 'W') {
			throw new IllegalArgumentException(
					"Error! Invalid color. Expected B or W.");
		}

		boolean move = true;
		playBoard[line - 1][this.getColumn(column)] = color;
		this.vectorMoveRoutine(line - 1, this.getColumn(column), color, move);
	}

	/**
	 * Check whether the positions form a rectangle.
	 * @param columnOne column of the first position
	 * @param lineOne line of the first position
	 * @param columnTwo column of the second position
	 * @param lineTwo line of the second position
	 * @return {@code true} if it is a rectangle, {@code false} otherwise
	 */
	public boolean isRectangle(char columnOne, int lineOne, char columnTwo, int lineTwo) {
		boolean result = true;
		int colOne = this.getColumn(columnOne);
		int colTwo = this.getColumn(columnTwo);
		if (colOne < 0 || colOne > colTwo) {
			result = false;
		} else if (lineOne - 1 < 0 || lineOne > lineTwo) {
			result = false;
		}
		return result;
	}

	/**
	 * Check whether the board contains this point.
	 * @param columnName column of the point
	 * @param line line of the point
	 * @return {@code true} if there exists the point, {@code false} otherwise
	 */
	public boolean containsPoint(char columnName, int line) {
		int column = this.getColumn(columnName);
		boolean result = true;
		if (line - 1 < 0 || line > playBoard.length) {
			result = false;
		} else if (column < 0 || column > playBoard[0].length) {
			result = false;
		}
		return result;
	}

	/**
	 * Returns the fields of the board to print.
	 * @return the fields of the board to print
	 */
	public List<String> getField() {
		List<String> field = new ArrayList<String>();
		String line = null;
		for (int i = 0; i < playBoard.length; i++) {
			for (int j = 0; j < playBoard[0].length; j++) {
				if (j == 0) {
					line = String.valueOf(playBoard[i][j]);
				} else {
					line = line + playBoard[i][j];
				}
			}
			field.add(line);
			line = null;
		}
		return field;
	}

	/**
	 * Count the stones of the specified color.
	 * @param color color of the player
	 * @return number of stones, with the color which are on the board
	 */
	public int countStones(char color) {
		if (color != 'B' && color != 'W') {
			throw new IllegalArgumentException(
					"Error! Invalid color. Expected B or W.");
		}

		int counter = 0;
		for (int i = 0; i < playBoard.length; i++) {
			for (int j = 0; j < playBoard[0].length; j++) {
				if (playBoard[i][j] == color) {
					counter++;
				}
			}
		}
		return counter;
	}

	/**
	 * Searches for all possible moves.
	 * @param color color of the player
	 */
	private void findPossibleMoves(char color) {
		assert color == 'B' || color == 'W';

		this.possibleMoves.clear();
		for (int i = 0; i < playBoard.length; i++) {
			for (int j = 0; j < playBoard[0].length; j++) {
				if (playBoard[i][j] == color) {
					findPossibleMovesAt(i, j, color);
				}
			}
		}
	}

	/**
	 * Searches for all possible moves from the point.
	 * @param line line of the point
	 * @param column column of the point
	 * @param color color of the player
	 */
	private void findPossibleMovesAt(int line, int column, char color) {
		assert color == 'B' || color == 'W';
		assert line >= 0 && line < playBoard.length;
		assert column >= 0 && column < playBoard[0].length;

		boolean move = false;
		this.vectorMoveRoutine(line, column, color, move);
	}

	/**
	 * Vector routine in which all eight vectors point from which to be tested.
	 * @param line line of the point
	 * @param column column of the point
	 * @param color color of the player
	 * @param move true if it is a move, false otherwise
	 */
	private void vectorMoveRoutine(int line, int column, char color, boolean move) {
		assert color == 'B' || color == 'W';
		assert line >= 0 && line < playBoard.length;
		assert column >= 0 && column < playBoard[0].length;

		this.vectorMove(line, column, color, -1, -1, move);
		this.vectorMove(line, column, color, -1, 0, move);
		this.vectorMove(line, column, color, -1, 1, move);
		this.vectorMove(line, column, color, 0, 1, move);
		this.vectorMove(line, column, color, 1, 1, move);
		this.vectorMove(line, column, color, 1, 0, move);
		this.vectorMove(line, column, color, 1, -1, move);
		this.vectorMove(line, column, color, 0, -1, move);
	}

	/**
	 * Vector movement in all eight directions from a point of.
	 * @param line line of the point
	 * @param column column of the point
	 * @param color color of the player
	 * @param vectorL vertical vector
	 * @param vectorC horizontal vector
	 * @param move true if it is a move, false otherwise
	 */
	private void vectorMove(int line, int column, char color, int vectorL, int vectorC, boolean move) {
		assert line >= 0 && line < playBoard.length;
		assert column >= 0 && column < playBoard[0].length;
		assert color == 'B' || color == 'W';
		assert vectorL == 1 || vectorL == 0 || vectorL == -1;
		assert vectorC == 1 || vectorC == 0 || vectorC == -1;

		int counter = 2;
		if (line + vectorL * 2 < playBoard.length
				&& column + vectorC * 2 < playBoard[0].length) {
			if (line + vectorL * 2 >= 0 && column + vectorC * 2 >= 0) {
				if (playBoard[line + vectorL][column + vectorC] == this.invertColor(color)) {
					counter = this.incVector(line, column, color, vectorL, vectorC);
					if (!move) {
						if (playBoard[line + vectorL * counter][column + vectorC * counter] == '-') {
							PossibleMove posMov = new PossibleMove(
									this.getColumnName(column + vectorC	
											* counter), line + vectorL
											* counter + 1);
							this.addMove(posMov);
						}
					} else if (playBoard[line + vectorL * counter][column + vectorC * counter] == color) {
						playBoard[line + vectorL][column + vectorC] = color;
						while (counter > 2) {
							playBoard[line + vectorL * (counter - 1)][column + vectorC * (counter - 1)] = color;
							counter--;
						}
					}
				}
			}
		}
	}

	/**
	 * Increments the vector to get the possible moves.
	 * @param line line of the point
	 * @param column column of the point
	 * @param color color of the player
	 * @param vectorL vertical vector
	 * @param vectorC horizontal vector
	 * @return counter with number of possible increments
	 */
	private int incVector(int line, int column, char color, int vectorL, int vectorC) {
		assert line >= 0 && line < playBoard.length;
		assert column >= 0 && column < playBoard[0].length;
		assert color == 'B' || color == 'W';
		assert vectorL == 1 || vectorL == 0 || vectorL == -1;
		assert vectorC == 1 || vectorC == 0 || vectorC == -1;

		boolean stop = false;
		int counter = 1;
		while (!stop
				&& playBoard[line + vectorL * counter][column + vectorC
						* counter] == this.invertColor(color)) {
			counter++;
			if (line + vectorL * (counter + 1) >= playBoard.length
					|| column + vectorC * (counter + 1) >= playBoard[0].length) {
				stop = true;
			} else if (line + vectorL * (counter + 1) < 0
					|| column + vectorC * (counter + 1) < 0) {
				stop = true;
			}
		}
		return counter;
	}

	/**
	 * Adds a move to the board.
	 * @param posMov possible move of the current player
	 */
	private void addMove(PossibleMove posMov) {
		assert posMov != null;

		boolean uniqueMove = true;
		for (PossibleMove p : possibleMoves) {
			if (p.equals(posMov)) {
				uniqueMove = false;
			}
		}
		if (uniqueMove) {
			possibleMoves.add(posMov);
		}
	}

	/**
	 * Inverts the color of the player
	 * @param color of the player
	 * @return inverted color of the player
	 */
	private char invertColor(char color) {
		assert color == 'B' || color == 'W';
		char invertedColor = 'W';
		if (color == 'W') {
			invertedColor = 'B';
		}
		return invertedColor;
	}

	/**
	 * Gets the column from the column identifiers
	 * @param columnName column identifiers
	 * @return column of the column identifiers
	 */
	private int getColumn(char columnName) {
		int result = -1;
		if (columnName > 64 && columnName < 91) {
			result = columnName - 65;
		}
		return result;
	}

	/**
	 * Gets the column identifiers from the column
	 * @param column on the board
	 * @return column identifiers
	 */
	private char getColumnName(int column) {
		char result = '0';
		if (column >= 0 && column <= playBoard[0].length) {
			result = (char) (column + 65);
		}
		return result;
	}

	/**
	 * Initializes the board if there are no optional settings available.
	 */
	private void init() {
		int linePos = this.playBoard.length / 2;
		int columnPos = this.playBoard[0].length / 2;
		for (int i = 0; i < playBoard.length; i++) {
			for (int j = 0; j < playBoard[0].length; j++) {
				playBoard[i][j] = '-';
			}
		}
		playBoard[linePos - 1][columnPos - 1] = 'W';
		playBoard[linePos - 1][columnPos] = 'B';
		playBoard[linePos][columnPos] = 'W';
		playBoard[linePos][columnPos - 1] = 'B';
	}

}
