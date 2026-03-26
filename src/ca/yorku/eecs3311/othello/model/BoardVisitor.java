package ca.yorku.eecs3311.othello.model;

/**
 * Visitor for traversing an OthelloBoard.
 * Implementations can render, serialize or collect stats without changing the board.
 */
public interface BoardVisitor {
	void visit(int row, int col, char token);
}
