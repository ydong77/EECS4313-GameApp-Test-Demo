package ca.yorku.eecs3311.othello.model;

/**
 * Visitor that renders the board into the same formatted string as toString().
 */
public class RenderVisitor implements BoardVisitor {
	private final int dim;
	private final StringBuilder sb = new StringBuilder();
	private int currentRow = -1;

	public RenderVisitor(int dim) {
		this.dim = dim;
		// Column header
		sb.append("  ");
		for (int col = 0; col < dim; col++) {
			sb.append(col).append(" ");
		}
		sb.append('\n');

		// Top border
		sb.append(" +");
		for (int col = 0; col < dim; col++) {
			sb.append("-+");
		}
		sb.append('\n');
	}

	@Override
	public void visit(int row, int col, char token) {
		// Start a new row
		if (row != currentRow) {
			if (currentRow >= 0) {
				// close previous row
				sb.append(currentRow).append("\n");
				appendBorder();
			}
			currentRow = row;
			sb.append(row).append("|");
		}
		sb.append(token).append("|");
		// If this is the last column, append row label and border is added on next row start or finish()
		if (col == dim - 1) {
			// defer appending row label handled either in next row start or finish()
		}
	}

	public String finish() {
		// Close last row if any visits occurred
		if (currentRow >= 0) {
			sb.append(currentRow).append("\n");
			appendBorder();
		}
		// Bottom column header
		sb.append("  ");
		for (int col = 0; col < dim; col++) {
			sb.append(col).append(" ");
		}
		sb.append('\n');
		return sb.toString();
	}

	private void appendBorder() {
		sb.append(" +");
		for (int col = 0; col < dim; col++) {
			sb.append("-+");
		}
		sb.append('\n');
	}

	@Override
	public String toString() {
		return sb.toString();
	}
}
