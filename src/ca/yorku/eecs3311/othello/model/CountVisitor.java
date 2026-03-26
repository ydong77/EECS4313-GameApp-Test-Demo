package ca.yorku.eecs3311.othello.model;

/**
 * Visitor that counts occurrences of a specific token on the board.
 */
public class CountVisitor implements BoardVisitor {
	private final char target;
	private int count = 0;

	public CountVisitor(char target) {
		this.target = target;
	}

	@Override
	public void visit(int row, int col, char token) {
		if (token == target) count++;
	}

	public int getCount() {
		return count;
	}
}
