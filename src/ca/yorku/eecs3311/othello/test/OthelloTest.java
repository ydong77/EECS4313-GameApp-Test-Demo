package ca.yorku.eecs3311.othello.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ca.yorku.eecs3311.othello.model.Move;
import ca.yorku.eecs3311.othello.model.Othello;
import ca.yorku.eecs3311.othello.model.OthelloBoard;

public class OthelloTest {
	Othello othello;
	Move[] moves = { new Move(2, 4), new Move(2, 5), new Move(2, 6), new Move(2, 3), new Move(2, 2), new Move(3, 2),
			new Move(4, 2), new Move(5, 4), new Move(6, 4) };

	@Before
	public void setUp() throws Exception {
		othello = new Othello();
		othello.move(2, 4);
		othello.move(2, 5);
		othello.move(2, 6);
		othello.move(2, 3);
		
		// Board now looks like
		//   0 1 2 3 4 5 6 7
		//  +-+-+-+-+-+-+-+-+
		// 0| | | | | | | | |0
		//  +-+-+-+-+-+-+-+-+
		// 1| | | | | | | | |1
		//  +-+-+-+-+-+-+-+-+
		// 2| | | |O|X|X|X| |2
		//  +-+-+-+-+-+-+-+-+
		// 3| | | |O|O| | | |3
		//  +-+-+-+-+-+-+-+-+
		// 4| | | |O|X| | | |4
		//  +-+-+-+-+-+-+-+-+
		// 5| | | | | | | | |5
		//  +-+-+-+-+-+-+-+-+
		// 6| | | | | | | | |6
		// +-+-+-+-+-+-+-+-+
		// 7| | | | | | | | |7
		//  +-+-+-+-+-+-+-+-+
		//   0 1 2 3 4 5 6 7
		//
		// X:4 O:4  X moves next
		// row: col: X makes move (2,2)
		//
		//   0 1 2 3 4 5 6 7
		//  +-+-+-+-+-+-+-+-+
		// 0| | | | | | | | |0
		//  +-+-+-+-+-+-+-+-+
		// 1| | | | | | | | |1
		//  +-+-+-+-+-+-+-+-+
		// 2| | |X|X|X|X|X| |2
		//  +-+-+-+-+-+-+-+-+
		// 3| | | |X|O| | | |3
		//  +-+-+-+-+-+-+-+-+
		// 4| | | |O|X| | | |4
		//  +-+-+-+-+-+-+-+-+
		// 5| | | | | | | | |5
		//  +-+-+-+-+-+-+-+-+
		// 6| | | | | | | | |6
		//  +-+-+-+-+-+-+-+-+
		// 7| | | | | | | | |7
		//  +-+-+-+-+-+-+-+-+
		//   0 1 2 3 4 5 6 7
		//
		// X:7 O:2  O moves next

	}

	@Test
	public void testGetWhosTurn() {
		assertEquals(othello.getWhosTurn(),OthelloBoard.P1);
		othello.move(2, 2);
		assertEquals(othello.getWhosTurn(),OthelloBoard.P2);
	}

	@Test
	public void testGetCount() {
		assertEquals(othello.getCount(OthelloBoard.P1), 4);
		assertEquals(othello.getCount(OthelloBoard.P2), 4);
		othello.move(2, 2);
		assertEquals(othello.getCount(OthelloBoard.P1), 7);
		assertEquals(othello.getCount(OthelloBoard.P2), 2);
	}

	@Test
	public void testGetWinner() {
		Othello o=new Othello();
		for(int i=0;i<moves.length;i++) {
			assertEquals("During play", o.getWinner(), OthelloBoard.EMPTY);
			o.move(moves[i].getRow(), moves[i].getCol());
		}
		assertEquals("After winner", o.getWinner(), OthelloBoard.P1);
	}

	@Test
	public void testIsGameOver() {
		Othello o=new Othello();
		for(int i=0;i<moves.length;i++) {
			assertEquals("During play", o.isGameOver(), false);
			o.move(moves[i].getRow(), moves[i].getCol());
		}
		assertEquals("After winner", o.isGameOver(), true);
	}

	/*
	// AI-generated additional JUnit tests
	@Test
	public void testInvalidMoveDoesNotChangeTurn() {
		assertEquals(OthelloBoard.P1, othello.getWhosTurn());
		assertFalse(othello.move(0, 0));
		assertEquals(OthelloBoard.P1, othello.getWhosTurn());
	}

	// AI-generated additional JUnit tests
	@Test
	public void testInvalidMoveDoesNotIncreaseMoveCount() {
		assertEquals(4, othello.getNumMoves());
		assertFalse(othello.move(0, 0));
		assertEquals(4, othello.getNumMoves());
	}

	// AI-generated additional JUnit tests
	@Test
	public void testTieGameReturnsNoWinner() {
		char[][] grid = {
				{'X','O','X','O','X','O','X','O'},
				{'O','X','O','X','O','X','O','X'},
				{'X','O','X','O','X','O','X','O'},
				{'O','X','O','X','O','X','O','X'},
				{'X','O','X','O','X','O','X','O'},
				{'O','X','O','X','O','X','O','X'},
				{'X','O','X','O','X','O','X','O'},
				{'O','X','O','X','O','X','O','X'}
		};
		Othello tieGame = new Othello(new OthelloBoard(grid), OthelloBoard.EMPTY, 60);

		assertTrue(tieGame.isGameOver());
		assertEquals(32, tieGame.getCount(OthelloBoard.P1));
		assertEquals(32, tieGame.getCount(OthelloBoard.P2));
		assertEquals(OthelloBoard.EMPTY, tieGame.getWinner());
	}

	// AI-generated additional JUnit tests
	@Test
	public void testTurnStaysWithCurrentPlayerWhenOnlyTheyHaveAMove() {
		char[][] grid = filledBoard(OthelloBoard.P1);
		grid[0][0] = OthelloBoard.EMPTY;
		grid[0][1] = OthelloBoard.P2;
		grid[0][2] = OthelloBoard.P1;
		grid[7][0] = OthelloBoard.EMPTY;
		grid[7][1] = OthelloBoard.P2;
		grid[7][2] = OthelloBoard.P1;
		Othello onlyXCanMove = new Othello(new OthelloBoard(grid), OthelloBoard.P1, 10);

		assertTrue(onlyXCanMove.move(0, 0));
		assertEquals(OthelloBoard.P1, onlyXCanMove.getWhosTurn());
		assertFalse(onlyXCanMove.isGameOver());
	}
	

	private char[][] filledBoard(char token) {
		char[][] grid = new char[Othello.DIMENSION][Othello.DIMENSION];
		for (int r = 0; r < Othello.DIMENSION; r++) {
			for (int c = 0; c < Othello.DIMENSION; c++) {
				grid[r][c] = token;
			}
		}
		return grid;
	}
	 */
}
