package ca.yorku.eecs3311.othello.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ca.yorku.eecs3311.othello.model.*;

public class MoveTest {
	Move move;
	@Before
	public void setUp() throws Exception {
		move=new Move(3,5);
	}

	@Test
	public void testGetRow() {
		assertEquals("getRow", move.getRow(),3);
	}

	@Test
	public void testGetCol() {
		assertEquals("getCol", move.getCol(),5);
	}

	@Test
	public void testToString() {
		assertEquals("toString", move.toString(),"(3,5)");
	}

}
