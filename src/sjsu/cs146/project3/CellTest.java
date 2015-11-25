package sjsu.cs146.project3;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import sjsu.cs146.project3.Cell.Wall;

public class CellTest {
	private Cell testCell;
	
	@Before
	public void setUp() {
		testCell = new Cell();
	}

	@Test
	public void testConstructHasAllWalls() {
		for (Wall wall : Wall.values())
			assertTrue(testCell.hasWall(wall));
	}
	
	@Test
	public void testAddWall() {
		for (Wall currentReAdded : Wall.values()) {	// Remove all walls
			testCell.removeWall(currentReAdded);
			assertTrue(!testCell.hasWall(currentReAdded));	// Check removal
			testCell.addWall(currentReAdded);
			assertTrue(testCell.hasWall(currentReAdded));	// Check re-addition
		}
	}
	
	@Test
	public void testRemoveWall() {
		for (Wall currentRemoved : Wall.values()) {	// Test each wall
			assertTrue(testCell.hasWall(currentRemoved));	// Still exists
			testCell.removeWall(currentRemoved);	// Removed
			assertTrue(!testCell.hasWall(currentRemoved));	// Check removed
		}
	}
	@Test
	public void testRemoveOppositeWall() {
		for (Wall currentRemoved : Wall.values()) {
			assertTrue(testCell.hasWall(testCell.getOppositeWall(currentRemoved)));
			testCell.removeOppositeWall(currentRemoved);
			assertTrue(!testCell.hasWall(testCell.getOppositeWall(currentRemoved)));
		}
	}
	
	@Test
	public void testGetOppositeWall() {
		assertEquals(Wall.SOUTH, testCell.getOppositeWall(Wall.NORTH));
		assertEquals(Wall.WEST, testCell.getOppositeWall(Wall.EAST));
		assertEquals(Wall.NORTH, testCell.getOppositeWall(Wall.SOUTH));
		assertEquals(Wall.EAST, testCell.getOppositeWall(Wall.WEST));
	}
	
	@Test
	public void testIsIsolated() {
		assertTrue(testCell.isIsolated());	// All walls intact
		for (Wall currentRemoved : Wall.values()) {
			testCell.removeWall(currentRemoved);
			assertTrue(!testCell.isIsolated());	// 1 removed wall
			testCell.addWall(currentRemoved);	// Re-add to only test 1 removed wall at a time
		}
	}
}
