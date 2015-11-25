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
	public void breakWall() {
		Wall toBreak = Wall.SOUTH;
		int cell1 = testMaze.getLinearPosition(0, 0), cell2 = testMaze.getLinearPosition(0, 1);
		
		assertTrue(testMaze.getCell(cell1).hasWall(toBreak));	// Has south wall
		assertTrue(testMaze.getCell(cell2).hasWall(testMaze.getCell(cell2).getOppositeWall(toBreak)));	// Has north wall
		testMaze.breakWall(cell1, toBreak);	// Break wall south of cell1
		assertTrue(!testMaze.getCell(cell1).hasWall(toBreak));	// No more south wall
		assertTrue(!testMaze.getCell(cell2).hasWall(testMaze.getCell(cell2).getOppositeWall(toBreak)));	// Also breaks neighbor's side of wall
	}
	
	@Test
	public void testGetNeighbor() {
		int start = 0;
		int northNeighbor = Maze.OUT_OF_BOUNDS, westNeighbor = Maze.OUT_OF_BOUNDS;	// No north or west neighbors
		int eastNeighbor = testMaze.getLinearPosition(1, 0), southNeighbor = testMaze.getLinearPosition(0, 1);
		
		assertEquals(northNeighbor, testMaze.getNeighbor(start, Wall.NORTH));
		assertEquals(westNeighbor, testMaze.getNeighbor(start, Wall.WEST));
		assertEquals(eastNeighbor, testMaze.getNeighbor(start, Wall.EAST));
		assertEquals(southNeighbor, testMaze.getNeighbor(start, Wall.SOUTH));
	}
	@Test
	public void testGetNeighborNull() {
		int upperRight = testMaze.getLinearPosition(testSize - 1, 0), lowerLeft = upperRight + 1;
		assertEquals(Maze.OUT_OF_BOUNDS, testMaze.getNeighbor(upperRight, Wall.EAST));
		assertEquals(Maze.OUT_OF_BOUNDS, testMaze.getNeighbor(lowerLeft, Wall.WEST));
	}
	
	@Test
	public void testGetLinearPosition() {
		int start = 0, end = testMaze.getLength() - 1;
		assertEquals(start, testMaze.getLinearPosition(0, 0));
		assertEquals(end, testMaze.getLinearPosition(testSize - 1, testSize - 1));
	}
	
	@Test
	public void testGetLength() {
		assertEquals(testSize * testSize, testMaze.getLength());
	}
	
	@Test public void testBuildString() {
		testMaze.generateRandomPath();
		System.out.println(testMaze.buildString());
	}
}
