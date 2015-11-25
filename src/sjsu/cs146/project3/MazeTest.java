package sjsu.cs146.project3;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import sjsu.cs146.project3.Cell.Wall;

public class MazeTest {
	private int testSize = 4;
	private Maze testMaze;
	
	@Before
	public void setUp() {
		testMaze = new Maze(testSize);
	}

	@Test
	public void testOnConstructSelectiveIsolation() {
		assertTrue(!testMaze.getCell(0).isIsolated());	// Start has open NORTH
		assertTrue(!testMaze.getCell(testMaze.getLength() - 1).isIsolated());	// End has open SOUTH
		for (int i = 1; i < testMaze.getLength() - 2; i++)
			assertTrue(testMaze.getCell(i).isIsolated());	// Inner cells isolated
	}
	
	@Test
	public void testOnConstructStartOpenNorth() {
		Cell start = testMaze.getCell(0);
		for (Wall wall : Wall.values()) {
			if (wall == Wall.NORTH)
				assertTrue(!start.hasWall(wall));	// No NORTH wall
			else
				assertTrue(start.hasWall(wall));	// Has other walls
		}
	}
	@Test
	public void testOnConstructEndOpenSouth() {
		Cell end = testMaze.getCell(testMaze.getLength() - 1);
		for (Wall wall : Wall.values()) {
			if (wall == Wall.SOUTH)
				assertTrue(!end.hasWall(wall));	// No SOUTH wall
			else
				assertTrue(end.hasWall(wall));	// Has other walls
		}
	}
	
	@Test
	public void testGetLength() {
		assertEquals(testSize * testSize, testMaze.getLength());
	}
}
