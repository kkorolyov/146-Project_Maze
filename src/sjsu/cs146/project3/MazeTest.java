package sjsu.cs146.project3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sjsu.cs146.project3.Cell.Wall;
import sjsu.cs146.project3.props.Props;

/**
 * Provides methods for testing {@link Maze} functionality.
 */
public class MazeTest {	
	private static int testSize = 4;
	private static long testSeed = 0;
	private static Maze savedPresetMaze, savedRandomMaze;
	private Maze testMaze;
	
	@BeforeClass
	public static void setUpBeforeClass() {	// To use same random maze across multiple tests
		savedPresetMaze = new Maze(testSize);
		savedPresetMaze.generateRandomPath(testSeed);
		
		savedRandomMaze = new Maze(testSize);
		savedRandomMaze.generateRandomPath();
	}
	
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
	
	@Test
	public void testBuildString() {
		testMaze = new Maze(4);	// Manually-set expected String for 4x4 maze
		String expectedMaze = "+ +-+-+-+\n"
												+ "| | | | |\n"
												+ "+-+-+-+-+\n"
												+ "| | | | |\n"
												+ "+-+-+-+-+\n"
												+ "| | | | |\n"
												+ "+-+-+-+-+\n"
												+ "| | | | |\n"
												+ "+-+-+-+ +\n";
		assertEquals(expectedMaze, testMaze.buildString());
	}
	@Test
	public void testSeed0() {
		testMaze = new Maze(4);	// Manually-set expected String for 4x4 maze
		testMaze.generateRandomPath(testSeed);
		String expectedMaze = "+ +-+-+-+\n"
												+ "| |     |\n"
												+ "+ + +-+-+\n"
												+ "| |     |\n"
												+ "+ +-+-+ +\n"
												+ "|   |   |\n"
												+ "+-+ + + +\n"
												+ "|     | |\n"
												+ "+-+-+-+ +\n";
		
		assertEquals(expectedMaze, testMaze.buildString());
	}
	@Test
	public void testShortestPath() {
		testMaze = new Maze(4);	// Manually-set expected String for 4x4 maze
		testMaze.generateRandomPath(testSeed);
		testMaze.traverseBFS();
		testMaze.traverseDFSStack();
		String expectedMaze = "+ +-+-+-+\n"
												+ "|#|     |\n"
												+ "+ + +-+-+\n"
												+ "|#|     |\n"
												+ "+ +-+-+ +\n"
												+ "|# #|# #|\n"
												+ "+-+ + + +\n"
												+ "|  # #|#|\n"
												+ "+-+-+-+ +\n";
		
		assertEquals(expectedMaze, testMaze.buildStringShortestPathBFS());
		assertEquals(expectedMaze, testMaze.buildStringShortestPathDFSStack());
	}
	
	@Test
	public void displayMaze() {
		if (Props.printMaze) {
			printMaze();
		}
		if (Props.printBFS) {
			printBFS();
		}
		if (Props.printDFSStack) {
			printDFSStack();
		}
		/*if (Props.printDFSRecursive) {
			printDFSRecursive();
		}*/
	}
	
	private void printMaze() {
		if (Props.printPreset) {
			System.out.println(	"Seed = " + String.valueOf(testSeed) + "\n"
												+ savedPresetMaze.buildString());
		}
		if (Props.printRandom) {
			System.out.println(	"Random seed\n"
												+ savedRandomMaze.buildString());
		}
	}
	
	private void printBFS() {
		if (Props.printPreset) {
			savedPresetMaze.traverseBFS();
			
			if (Props.printValuesCentered) {
				System.out.println(	"Seed = " + String.valueOf(testSeed)
													+ "\nBFS");
				printBFSOrder(savedPresetMaze, false);
			}
			if (Props.printValuesChopped) {
				System.out.println(	"Seed = " + String.valueOf(testSeed)
													+ "\nBFS (chopped values)");
				printBFSOrder(savedPresetMaze, true);
			}
			if (Props.printShortestPath) {
				System.out.println(	"Seed = " + String.valueOf(testSeed)
													+ "\nBFS shortest path");
				printBFSShortestPath(savedPresetMaze);
			}
		}
		if (Props.printRandom) {
			savedRandomMaze.traverseBFS();
			
			if (Props.printValuesCentered) {
				System.out.println(	"Random seed"
													+ "\nBFS");
				printBFSOrder(savedRandomMaze, false);
			}
			if (Props.printValuesChopped) {
				System.out.println(	"Random seed"
													+ "\nBFS (chopped values)");
				printBFSOrder(savedRandomMaze, true);
			}
			if (Props.printShortestPath) {
				System.out.println(	"Random seed"
													+ "\nBFS shortest path");
				printBFSShortestPath(savedRandomMaze);
			}
		}
	}
	private void printBFSOrder(Maze maze, boolean uglyStyle) {
		System.out.println(maze.buildStringBFS(uglyStyle));
	}
	private void printBFSShortestPath(Maze maze) {
		System.out.println(maze.buildStringShortestPathBFS());
	}
	
	private void printDFSStack() {
		if (Props.printPreset) {
			savedPresetMaze.traverseDFSStack();
			
			if (Props.printValuesCentered) {
				System.out.println(	"Seed = " + String.valueOf(testSeed)
													+ "\nDFS-Stack");
				printDFSStackOrder(savedPresetMaze, false);
			}
			if (Props.printValuesChopped) {
				System.out.println(	"Seed = " + String.valueOf(testSeed)
													+ "\nDFS-Stack (chopped values)");
				printDFSStackOrder(savedPresetMaze, true);
			}
			if (Props.printShortestPath) {
				System.out.println(	"Seed = " + String.valueOf(testSeed)
													+ "\nDFS-Stack shortest path");
				printDFSStackShortestPath(savedPresetMaze);
			}
		}
		if (Props.printRandom) {
			savedRandomMaze.traverseDFSStack();
			
			if (Props.printValuesCentered) {
				System.out.println(	"Random seed"
													+ "\nDFS-Stack");
				printDFSStackOrder(savedRandomMaze, false);
			}
			if (Props.printValuesChopped) {
				System.out.println(	"Random seed"
													+ "\nDFS-Stack (chopped values)");
				printDFSStackOrder(savedRandomMaze, true);
			}
			if (Props.printShortestPath) {
				System.out.println(	"Random seed"
													+ "\nDFS-Stack shortest path");
				printDFSStackShortestPath(savedRandomMaze);
			}
		}
	}
	private void printDFSStackOrder(Maze maze, boolean uglyStyle) {
		System.out.println(maze.buildStringDFSStack(uglyStyle));
	}
	private void printDFSStackShortestPath(Maze maze) {
		System.out.println(maze.buildStringShortestPathDFSStack());
	}
	
	/*private void printDFSRecursive() {
		if (Props.printPreset) {
			savedPresetMaze.traverseDFSRecursive();
			
			if (Props.printValuesCentered) {
				System.out.println(	"Seed = " + String.valueOf(testSeed)
													+ "\nDFS-Recursive");
				printDFSRecursiveOrder(savedPresetMaze, false);
			}
			if (Props.printValuesChopped) {
				System.out.println(	"Seed = " + String.valueOf(testSeed)
													+ "\nDFS-Recursive (chopped values)");
				printDFSRecursiveOrder(savedPresetMaze, true);
			}
			if (Props.printShortestPath) {
				System.out.println(	"Seed = " + String.valueOf(testSeed)
													+ "\nDFS-Recursive shortest path");
				printDFSRecursiveShortestPath(savedPresetMaze);
			}
		}
		if (Props.printRandom) {
			savedRandomMaze.traverseDFSRecursive();
			
			if (Props.printValuesCentered) {
				System.out.println(	"Random seed"
													+ "\nDFS-Recursive");
				printDFSRecursiveOrder(savedRandomMaze, false);
			}
			if (Props.printValuesChopped) {
				System.out.println(	"Random seed"
													+ "\nDFS-Recursive (chopped values)");
				printDFSRecursiveOrder(savedRandomMaze, true);
			}
			if (Props.printShortestPath) {
				System.out.println(	"Random seed"
													+ "\nDFS-Recursive shortest path");
				printDFSRecursiveShortestPath(savedRandomMaze);
			}
		}
	}
	private void printDFSRecursiveOrder(Maze maze, boolean uglyStyle) {
		System.out.println(maze.buildStringDFSRecursive(uglyStyle));
	}
	private void printDFSRecursiveShortestPath(Maze maze) {
		System.out.println(maze.buildStringShortestPathDFSRecursive());
	}*/
}
