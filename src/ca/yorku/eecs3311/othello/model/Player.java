package ca.yorku.eecs3311.othello.model;

/**
 * Strategy base type: encapsulates how a player chooses a move.
 * Concrete strategies include human input and AI (random/greedy).
 */
public abstract class Player {
	protected Othello othello;
	protected char player;

	public Player(Othello othello, char player) {
		this.othello=othello;
		this.player=player;
	}
	public char getPlayer() {
		return this.player;
	}
	public abstract Move getMove();
}
