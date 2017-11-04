import java.util.List;

/**
 * This class represents the Othello game itself. It stores and administers the
 * board, players and possible moves of the Othello game.
 * @author Florian Mueller
 */
public class Othello {

	/** Othello playing board */
	private Board board;

	/** player one of the Othello game */
	private Player playerOne;

	/** player two of the Othello game */
	private Player playerTwo;

	/** active player */
	private Player activePlayer;

	/** flag if game has started */
	private boolean gameStarted;

	/** list of possible moves of the active player */
	private List<PossibleMove> possibleMoves;

	/**
	 * Othello standard constructor.
	 */
	public Othello() {
	}

	/**
	 * Starts a new Othello game with constructing a new playing board.
	 * @param lines line length of the new playing board
	 * @param columns column length of the new playing board
	 * @param playBoard optional setting of the playing board
	 * @return message if one or both players can make no move
	 */
	public String newGame(int lines, int columns, List<String> playBoard) {
		if (lines == 0 || lines % 2 != 0 || lines < 2 || lines > 98) {
			throw new IllegalArgumentException("Error! Line length is invalid.");
		} else if (columns == 0 || columns % 2 != 0 || columns < 2 || columns > 26) {
			throw new IllegalArgumentException(
					"Error! Column length is invalid.");
		} else if (this.gameIsActive()) {
			throw new IllegalArgumentException(
					"Error! There is already an active game.");
		} else if (playBoard != null) {

			for (String s : playBoard) {
				if (!s.matches("[W,B,#,,-]+")) {
					throw new IllegalArgumentException(
							"Error! Invalid board parameters.");
				} else if (s.length() != columns) {
					throw new IllegalArgumentException(
							"Error! Board columns not equal to columns.");
				}
			}

			if (playBoard.size() != lines) {
				throw new IllegalArgumentException(
						"Error! Board lines not equal to lines.");
			}
		}

		this.playerOne = new Player("Black", 'B');
		this.playerTwo = new Player("White", 'W');
		this.activePlayer = playerOne;
		this.board = new Board(lines, columns, playBoard);
		this.gameStarted = false;
		this.possibleMoves = board.getPossibleMoves(this.activePlayer.getColor());
		return this.checkPossibleMoves();
	}

	/**
	 * Check whether an active game is currently running.
	 * @return {@code true} if an active game is currently running, {@code false} otherwise
	 */
	public boolean gameIsActive() {
		return board != null;
	}

	/**
	 * Check whether an active game has started.
	 * @return {@code true} if a game has started, {@code false} otherwise
	 */
	public boolean gameHasStarted() {
		return gameStarted;
	}

	/**
	 * Checks whether a move is possible.
	 * @param column column of the move
	 * @param line line of the move
	 * @return {@code true} if a move is possible, {@code false} otherwise
	 */
	public boolean moveIsPossible(char column, int line) {
		if (!this.gameIsActive()) {
			throw new IllegalArgumentException("Error! No active game.");
		}

		boolean result = false;
		PossibleMove posMove = new PossibleMove(column, line);
		for (PossibleMove p : this.possibleMoves) {
			if (p.equals(posMove)) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * Prints the playing board and the active player.
	 * @return playing board with active player
	 */
	public List<String> print() {
		if (!this.gameIsActive()) {
			throw new IllegalArgumentException("Error! No active game.");
		}

		List<String> result = board.getField();
		result.add("turn: " + this.activePlayer.getName().toLowerCase());
		return result;
	}

	/**
	 * Set of rectangular holes on the playing board.
	 * @param colOne column of the first position
	 * @param lineOne line of the first position
	 * @param colTwo column of the second position
	 * @param lineTwo line of the second position
	 */
	public void hole(char colOne, int lineOne, char colTwo, int lineTwo) {
		if (!this.gameIsActive()) {
			throw new IllegalArgumentException("Error! No active game.");
		} else if (gameStarted) {
			throw new IllegalArgumentException(
					"Error! Cannot add hole area. Game has already started!");
		} else if (!board.containsPoint(colOne, lineOne)) {
			throw new IllegalArgumentException("Error! Point: " + colOne
					+ Integer.toString(lineOne) + " does not exist.");
		} else if (!board.containsPoint(colTwo, lineTwo)) {
			throw new IllegalArgumentException("Error! Point: " + colTwo
					+ Integer.toString(lineTwo) + " does not exist.");
		} else if (!board.isRectangle(colOne, lineOne, colTwo, lineTwo)) {
			throw new IllegalArgumentException(
					"Error! The entry is not a rectangle.");
		} else if (!board.holeIsAllowed(colOne, lineOne, colTwo, lineTwo)) {
			throw new IllegalArgumentException(
					"Error! Hole is not allowed. Stones between the hole!");
		}

		board.setHole(colOne, lineOne, colTwo, lineTwo);
	}
	
	/**
	 * Check if hole is allowed at this position.
	 * @param columnOne column of the first position
	 * @param lineOne line of the first position
	 * @param columnTwo column of the second position
	 * @param lineTwo line of the second position
	 * @return {@code true} if hole is allowed, {@code false} otherwise
	 */
	public boolean boardHoleIsAllowed(char columnOne, int lineOne, char columnTwo, int lineTwo) {
		if (!this.gameIsActive()) {
			throw new IllegalArgumentException("Error! No active game.");
		}
		
		return board.holeIsAllowed(columnOne, lineOne, columnTwo, lineTwo);
	}

	/**
	 * Check whether the board contains this point.
	 * @param column column of the point
	 * @param line line of the point
	 * @return {@code true} if there exists the point, {@code false} otherwise
	 */
	public boolean boardContainsPoint(char column, int line) {
		if (!this.gameIsActive()) {
			throw new IllegalArgumentException("Error! No active game.");
		}
		
		return board.containsPoint(column, line);
	}

	/**
	 * Check whether the positions form a rectangle.
	 * @param colOne column of the first position
	 * @param lineOne line of the first position
	 * @param colTwo column of the second position
	 * @param lineTwo line of the second position
	 * @return {@code true} if it is a rectangle, {@code false} otherwise
	 */
	public boolean holeIsRectangle(char colOne, int lineOne, char colTwo,
			int lineTwo) {
		if (!this.gameIsActive()) {
			throw new IllegalArgumentException("Error! No active game.");
		}

		return board.isRectangle(colOne, lineOne, colTwo, lineTwo);
	}

	/**
	 * Moves a stone of the player to the position of the board.
	 * @param column column of the position
	 * @param line line of the position
	 * @return message if one or both players can make no move after this one
	 */
	public String move(char column, int line) {
		if (!this.gameIsActive()) {
			throw new IllegalArgumentException("Error! No active game.");
		} else if (!board.containsPoint(column, line)) {
			throw new IllegalArgumentException("Error! Point does not exist.");
		}

		String result = null;
		this.gameStarted = true;

		if (this.moveIsPossible(column, line)) {
			board.moveStone(column, line, this.activePlayer.getColor());
			this.changePlayer();
			this.possibleMoves = board.getPossibleMoves(this.activePlayer.getColor());
			result = this.checkPossibleMoves();
		} else {
			result = "Move not possible.";
		}
		return result;
	}

	/**
	 * Returns all possible moves for the current player.
	 * @return list of all possible moves
	 */
	public String possibleMoves() {
		if (!this.gameIsActive()) {
			throw new IllegalArgumentException("Error! No active game.");
		}

		List<PossibleMove> possibleMoves = board.getPossibleMoves(this.activePlayer.getColor());
		String posMoves = null;
		boolean flag = false;
		for (PossibleMove p : possibleMoves) {
			if (!flag) {
				posMoves = p.toString();
				flag = true;
			} else {
				posMoves = p.toString() + "," + posMoves;
			}
		}
		posMoves = "Possible moves: " + posMoves;
		return posMoves;
	}

	/**
	 * Finished the current game and announces the winner of the game.
	 * @return message with the winner of the match
	 */
	public String abort() {
		if (!this.gameIsActive()) {
			throw new IllegalArgumentException("Error! No active game.");
		}

		String result = this.gameEnded();
		this.board = null;
		this.activePlayer = null;
		this.playerOne = null;
		this.playerTwo = null;
		this.possibleMoves.clear();
		return result;
	}

	/**
	 * Changes the active player.
	 */
	private void changePlayer() {
		if (this.activePlayer.equals(this.playerOne)) {
			this.activePlayer = this.playerTwo;
		} else {
			this.activePlayer = this.playerOne;
		}
	}

	/**
	 * Checks all possible moves and finish the game if no one can move more.
	 * @return passes or winner of the match
	 */
	private String checkPossibleMoves() {
		String result = null;
		if (this.possibleMoves.size() == 0) {
			result = this.activePlayer.getName().toLowerCase() + " passes.";
			this.activePlayer = playerTwo;
			this.possibleMoves = board.getPossibleMoves(this.activePlayer.getColor());
			
			if (this.possibleMoves.size() == 0) {
				result = this.abort();
			}
		}
		return result;
	}

	/**
	 * Game is over and the winner is evaluated.
	 * @return result and winner of the match
	 */
	private String gameEnded() {
		String result;
		int stonesPlayerOne = board.countStones(playerOne.getColor());
		int stonesPlayerTwo = board.countStones(playerTwo.getColor());

		if (stonesPlayerOne < stonesPlayerTwo) {
			result = "Game Over! " + playerTwo.getName().toLowerCase()
					+ " has won (" + Integer.toString(stonesPlayerTwo) + ":"
					+ Integer.toString(stonesPlayerOne) + ")!";
		} else if (stonesPlayerOne > stonesPlayerTwo) {
			result = "Game Over! " + playerOne.getName().toLowerCase()
					+ " has won (" + Integer.toString(stonesPlayerOne) + ":"
					+ Integer.toString(stonesPlayerTwo) + ")!";
		} else {
			result = "Game has ended in a draw.";
		}
		return result;
	}

}
