/**
 * This class represents the players of the Othello game. It stores and
 * administers the players.
 * @author Florian Mueller
 */
public class Player {

	/** name of the player */
	private String name;

	/** color of the player */
	private char color;

	/**
	 * Constructs a new player with his name and color.
	 * @param name name of the player
	 * @param color color of the player
	 */
	public Player(String name, char color) {
		this.name = name;
		this.color = color;
	}

	/**
	 * Returns the name of the player.
	 * @return the name of the player
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the color of the player.
	 * @return the color of the player
	 */
	public char getColor() {
		return this.color;
	}

	/**
	 * Check if two players are equally.
	 * @param other player
	 * @return {@code true} if both are equally, {@code false} otherwise
	 */
	public boolean equals(Player other) {
		return this.name == other.name && this.color == other.color;
	}

}
