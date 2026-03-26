package ca.yorku.eecs3311.othello.model;

/**
 * Command object encapsulating an Othello move for undo/redo.
 * Stores pre/post game state snapshots and can execute/undo by returning copies.
 */
public class OthelloMoveCommand {
	private final Othello before;
	private final Othello after;

	public OthelloMoveCommand(Othello before, Othello after) {
		this.before = before.copy();
		this.after = after.copy();
	}

	public Othello execute() {
		return after.copy();
	}

	public Othello undo() {
		return before.copy();
	}
}
