import java.util.ArrayList;
import java.util.List;

/**
 * This class implements a simple shell to test the functionalities of the
 * Othello game.
 * @author Florian Mueller
 */
public class Shell {

	/** the prompt of this shell */
	private static final String PROMPT = "othello> ";

	/** command to start a new Othello game */
	private static final String CMD_NEW_GAME = "newGame";

	/** command to add a hole range to the playing board */
	private static final String CMD_HOLE = "hole";

	/** command to place a hole */
	private static final String CMD_MOVE = "move";

	/** command returns the current position and the player's turn */
	private static final String CMD_PRINT = "print";

	/** command terminate the current game */
	private static final String CMD_ABORT = "abort";

	/** command displays the possible moves of the current player */
	private static final String CMD_POSSIBLE_MOVES = "possibleMoves";

	/** command to terminate the shell */
	private static final String CMD_QUIT = "quit";

	/**
	 * Private Constructor. This is a Utility class that should not be
	 * instantiated.
	 */
	private Shell() {
	}

	/**
	 * main method - realizes the shell
	 * @param args command line arguments - not used here !
	 */
	public static void main(String[] args) {
		boolean quit = false;
		Othello othello = new Othello();

		while (!quit) {
			final String tokens[] = Terminal.askString(PROMPT).trim().split("\\s+");
			final String cmd = tokens[0];

			if (CMD_NEW_GAME.equals(cmd)) {
				newGame(tokens, othello);

			} else if (CMD_HOLE.equals(cmd)) {
				hole(tokens, othello);

			} else if (CMD_MOVE.equals(cmd)) {
				move(tokens, othello);

			} else if (CMD_PRINT.equals(cmd)) {
				if (tokens.length == 1) {
					if (othello.gameIsActive()) {
						for (String s : othello.print()) {
							println(s);
						}
					} else {
						error("No active game.");
					}
				} else {
					error("Wrong number of parameters. One parameter expected.");
				}

			} else if (CMD_POSSIBLE_MOVES.equals(cmd)) {
				if (tokens.length == 1) {
					if (othello.gameIsActive()) {
						println(othello.possibleMoves());
					} else {
						error("No active game.");
					}
				} else {
					error("Wrong number of parameters. One parameter expected.");
				}

			} else if (CMD_ABORT.equals(cmd)) {
				if (tokens.length == 1) {
					if (othello.gameIsActive()) {
						String out = othello.abort();
						if (out != null) {
							println(out);
						}
					} else {
						error("No active game.");
					}
				} else {
					error("Wrong number of parameters. One parameter expected.");
				}

			} else if (CMD_QUIT.equals(cmd)) {
				if (tokens.length == 1) {
					quit = true;
				} else {
					error("Wrong number of parameters. One parameter expected.");
				}
			} else {
				error("Unknown command: '" + cmd + "'");
			}
		}
	}

	/**
	 * Performs the newGame command on the given Othello game.
	 * @param tokens command and parameters
	 * @param othello othello to operate on
	 */
	private static void newGame(String[] tokens, Othello othello) {
		if (tokens.length == 3 || tokens.length == 4) {
			if (!othello.gameIsActive()) {
				String strColumns = tokens[1];
				String strLines = tokens[2];

				if (strLines.matches("[0-9]+")) {
					if (strColumns.matches("[0-9]+")) {
						int lines = Integer.parseInt(strLines);
						int columns = Integer.parseInt(strColumns);

						if (lines % 2 == 0 && lines > 1 && lines < 99) {
							if (columns % 2 == 0 && columns > 1 && columns < 27) {
								if (tokens.length == 4) {
									String board = tokens[3];
									boardSetting(lines, columns, board, othello);
								} else {
									String out = othello.newGame(lines, columns, null);
									if (out != null) {
										println(out);
									}
								}
							} else {
								error("Expected an even column number between 2-26.");
							}
						} else {
							error("Expected an even line number between 2-98.");
						}
					} else {
						error("Invalid columns number for character.");
					}
				} else {
					error("Invalid lines number for character.");
				}
			} else {
				error("There is already an active game.");
			}
		} else {
			error("Wrong number of parameters. Three or four parameters expected.");
		}
	}

	/**
	 * Performs the hole command on the given Othello game.
	 * @param tokens command and parameters
	 * @param othello othello to operate on
	 */
	private static void hole(String[] tokens, Othello othello) {
		if (tokens.length == 2) {
			if (othello.gameIsActive()) {
				if (!othello.gameHasStarted()) {
					String holes = tokens[1];
					if (holes.matches("[A-Z]{1}[0-9]{0,1}[0-9]{1}[:]{1}"
							+ "[A-Z]{1}[0-9]{0,1}[0-9]{1}")) {
						setHole(holes, othello);
					} else {
						error("Invalid hole parameters.");
					}
				} else {
					error("Cannot add hole area. Game has already started!");
				}
			} else {
				error("No active game.");
			}
		} else {
			error("Wrong number of parameters. Two parameters expected.");
		}
	}

	/**
	 * Performs the move command on the given Othello game.
	 * @param tokens command and parameters
	 * @param othello othello to operate on
	 */
	private static void move(String[] tokens, Othello othello) {
		if (tokens.length == 2) {
			if (othello.gameIsActive()) {
				String point = tokens[1];
				if (point.matches("[A-Z]{1}[0-9]{0,1}[0-9]{1}")) {
					char column = point.charAt(0);
					int line = getLinePoint(point);
					if (othello.boardContainsPoint(column, line)) {
						if (othello.moveIsPossible(column, line)) {
							String out = othello.move(column, line);
							if (out != null) {
								println(out);
							}
						} else {
							println("Move not possible.");
						}

					} else {
						error("Point does not exist.");
					}

				} else {
					error("Invalid move parameters.");
				}

			} else {
				error("No active game.");
			}
		} else {
			error("Wrong number of parameters. Two parameters expected.");
		}
	}

	/**
	 * Creates a new board with given settings.
	 * @param lines line length of the new board
	 * @param columns column length of the new board
	 * @param board board settings
	 * @param othello othello to operate on
	 */
	private static void boardSetting(int lines, int columns, String board, Othello othello) {
		assert othello != null;
		assert board != null;
		assert lines > 1 && lines < 100 && lines % 2 == 0;
		assert columns > 1 && columns < 27 && columns % 2 == 0;

		boolean result = true;
		List<String> playBoard = toList(board);
		
		if (board.matches("[W,B,#,,-]+")) {
			for (String s : playBoard) {
				if (s.length() % 2 != 0 || s.length() != columns) {
					result = false;
				}
			}
			
			if (!result) {
				error("Invalid column length.");				
			}
			if (playBoard.size() % 2 != 0 || playBoard.size() != lines) {
				if (result) {
					error("Invalid line length.");
				}
				result = false;
			}
		} else {
			result = false;
			error("Invalid board parameters. Expected: B, W, #, -, ,");
		}
		if (result) {
			String out = othello.newGame(lines, columns, playBoard);
			if (out != null) {
				println(out);
			}
		}
	}

	/**
	 * Split and add a string to the list.
	 * @param board board setting
	 * @return list with board settings
	 */
	private static List<String> toList(String board) {
		assert board != null;
		assert board.matches("[W,B,#,-,,]+");
		
		List<String> toList = new ArrayList<String>();
		String tokens[] = board.trim().split(",");
		for (String s : tokens) {
			toList.add(s);
		}
		return toList;
	}

	/**
	 * Set of rectangular holes on the playing board.
	 * @param holes hole rectangle
	 * @param othello othello to operate on
	 */
	private static void setHole(String holes, Othello othello) {
		assert holes.matches("[A-Z]{1}[0-9]{0,1}[0-9]{1}[:]{1}"
				+ "[A-Z]{1}[0-9]{0,1}[0-9]{1}");

		String tokens[] = holes.trim().split(":");
		char colOne = tokens[0].charAt(0);
		char colTwo = tokens[1].charAt(0);
		int lineOne = getLinePoint(tokens, 0);
		int lineTwo = getLinePoint(tokens, 1);

		if (othello.boardContainsPoint(colOne, lineOne)) {
			if (othello.boardContainsPoint(colTwo, lineTwo)) {
				if (othello.holeIsRectangle(colOne, lineOne, colTwo, lineTwo)) {
					if (othello.boardHoleIsAllowed(colOne, lineOne, colTwo, lineTwo)) {
						othello.hole(colOne, lineOne, colTwo, lineTwo);
					} else {
						error("Hole is not allowed. Stones between the hole!");
					}
				} else {
					error("The entry is not a rectangle.");
				}
			} else {
				error("Point: " + colTwo + Integer.toString(lineTwo)
						+ " does not exist.");
			}
		} else {
			error("Point: " + colOne + Integer.toString(lineOne)
					+ " does not exist.");
		}
	}

	/**
	 * Returns line parameter of the given string.
	 * @param tokens string included line parameter
	 * @param index index of string
	 * @return line parameter of the given string
	 */
	private static int getLinePoint(String[] tokens, int index) {
		assert tokens != null;

		int result;
		if (tokens[index].length() == 3) {
			result = (tokens[index].codePointAt(1) - 48) * 10
					+ tokens[index].codePointAt(2) - 48;
		} else {
			result = tokens[index].codePointAt(1) - 48;
		}
		return result;
	}

	/**
	 * Returns line parameter of the given string.
	 * @param tokens string included line parameter
	 * @return line parameter of the given string
	 */
	private static int getLinePoint(String tokens) {
		assert tokens != null;

		int result;
		if (tokens.length() == 3) {
			result = (tokens.codePointAt(1) - 48) * 10 + tokens.codePointAt(2) - 48;
		} else {
			result = tokens.codePointAt(1) - 48;
		}
		return result;
	}

	/**
	 * Prints an error message.
	 * @param err error message to print
	 */
	private static void error(String err) {
		println("Error! " + err);
	}

	/**
	 * Prints a message.
	 * @param s string to print
	 */
	private static void println(String s) {
		System.out.println(s);
	}

}
