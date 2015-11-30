package sjsu.cs146.project3;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import sjsu.cs146.project3.Cell.Wall;
import sjsu.cs146.project3.gui.MazePrintListener;

/**
 * A randomly-generated perfect maze.
 * Supplies methods for solving itself.
 */
public class Maze {
	public static final int OUT_OF_BOUNDS = -1;	// Marker for out of bounds cell
	public static final int DISPLAY_WIDTH = 5;	// Width of cells in GUI display
	public static final int SHORTEST_PATH_TRIAL_MARKER = -6, SHORTEST_PATH_MARKER = -3;	// int marker for cell along shortest path
	public static final String SHORTEST_PATH_TRIAL_CHAR = "^", SHORTEST_PATH_CHAR = "#";	// Marks cells along shortest path
	
	private int size;	// Size of each edge of maze
	private boolean generated;	// If maze has been generated
	private Cell[] rooms;	// All cells in maze
	private List<Integer>[] openNeighbors;	// All adjacent cell indices for each source cell index
	private Map<Integer, Integer> discoveryOrderBFS = new HashMap<>(), discoveryOrderDFSStack = new HashMap<>(), discoveryOrderDFSRecursive = new HashMap<>();	// Discovery time of each cell
	private Map<Integer, Integer> shortestPathBFS = new HashMap<>(), shortestPathDFSStack = new HashMap<>(), shortestPathDFSRecursive = new HashMap<>();	// Cells along shortest path have marker
	
	private List<MazePrintListener> listeners = new ArrayList<>();	// For realtime maze traversal printing
	
	/**
	 * Constructs a new, square, s x s sized maze.
	 * @param s length of each side of maze
	 */
	@SuppressWarnings("unchecked")
	public Maze(int s) {
		size = s;
		rooms = new Cell[getLength()];
		openNeighbors = new LinkedList[getLength()];
		reset();
	}
	
	private void reset() {	// For reusability
		generated = false;
		
		for (int i = 0; i < openNeighbors.length; i++)	// Initialize adjacency lists
			openNeighbors[i] = new LinkedList<>();
		for (int i = 0; i < rooms.length; i++)	// Populate with completely enclosed cells
			rooms[i] = new Cell();
		
		rooms[0].removeWall(Wall.NORTH);	// Start cell
		rooms[rooms.length - 1].removeWall(Wall.SOUTH);	// End cell
	}
	
	/**
	 * Traverses the maze using BFS until the exit cell is encountered.
	 */
	public void traverseBFS() {
		if (!generated || !discoveryOrderBFS.isEmpty())	// Can't traverse a non-existent maze / Already traversed
			return;
		
		Map<Integer, Integer> shortestPathBFSDisplay = new HashMap<>();	// To display path with 2 types of markers without infringing on shortestPathBFS
		
		Map<Integer, Integer> distance = new HashMap<>();	// For solution
		Map<Integer, Color> colors = new HashMap<>();	// Track colors of vertices by index
		for (int i = 0; i < openNeighbors.length; i++)
			colors.put(i, Color.WHITE);	// Initialize all vertices white
		
		int time = 0;	// Track cell visitation order
		Queue<Integer> q = new LinkedBlockingQueue<>(getLength());	// BFS queue
		
		colors.put(0, Color.GREY);	// Discovered source vertex
		distance.put(0, 0);
		discoveryOrderBFS.put(0, time);	// Source vertex discovery time
		shortestPathBFSDisplay.put(0, SHORTEST_PATH_TRIAL_MARKER);	// Testing phase
		q.add(0);	// Enqueue source vertex
		
		while (!q.isEmpty()) {
			int currentSource = q.remove();	// Get next cell to explore
			for (int neighbor : openNeighbors[currentSource]) {	// All adjacent cells
				if (colors.get(neighbor) == Color.WHITE) {	// Unexplored vertex
					time++;
					colors.put(neighbor, Color.GREY);	// Neighbor discovered
					distance.put(neighbor, distance.get(currentSource) + 1);
					discoveryOrderBFS.put(neighbor, time);	// Neighbor discovery time
					shortestPathBFSDisplay.put(neighbor, SHORTEST_PATH_TRIAL_MARKER);
					
					if (!listeners.isEmpty()) {
						updateMazePrintListeners(shortestPathBFSDisplay);	// New marker placed, update listeners
						try {
							Thread.sleep(100);	// For easier following of displayed path changes
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					if (neighbor == getLength() - 1) {	// Located end cell
						q.clear();	// Force early BFS loop termination
						break;	// Ignore other neighbors
					}
					else
						q.add(neighbor);	// Enqueue neighbor
				}
				colors.put(currentSource, Color.BLACK);	// Source fully explored
			}
		}
		int currentBacktrack = getLength() - 1;	// Start backtracking from end cell
		shortestPathBFS.put(currentBacktrack, SHORTEST_PATH_MARKER);
		shortestPathBFSDisplay.put(currentBacktrack, SHORTEST_PATH_MARKER);	// Backtracking phase
		
		while (currentBacktrack != 0) {	// Backtrack to start cell
			for (int neighbor : openNeighbors[currentBacktrack]) {
				if (distance.get(neighbor) != null && distance.get(neighbor) < distance.get(currentBacktrack)) {	// Going in correct direction
					currentBacktrack = neighbor;
					shortestPathBFS.put(neighbor, SHORTEST_PATH_MARKER);
					shortestPathBFSDisplay.put(currentBacktrack, SHORTEST_PATH_MARKER);

					if (!listeners.isEmpty()) {
						updateMazePrintListeners(shortestPathBFSDisplay);	// New BFS marker placed, update listeners
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	/**
	 * Traverses the maze using a Stack-implemented DFS until the exit cell is encountered.
	 */
	public void traverseDFSStack() {
		if (!generated || !discoveryOrderDFSStack.isEmpty())	// Can't traverse a non-existent maze / Already traversed
			return;
		
		Map<Integer, Color> colors = new HashMap<>();	// Track colors of vertices by index
		for (int i = 0; i < openNeighbors.length; i++)
			colors.put(i, Color.WHITE);	// Initialize all vertices white
		
		Stack<Integer> s = new Stack<>();	// DFS stack
		int currentCell = 0, visitedCells = 1, totalCells = getLength();	// To control cell iteration
		int time = 0;	// Track cell visitation order
		
		colors.put(currentCell, Color.GREY);	// Discover starting cell
		discoveryOrderDFSStack.put(currentCell, time++);	// Starting cell discovery time (then increment)
		shortestPathDFSStack.put(currentCell, SHORTEST_PATH_MARKER);	// Mark starting cell
		s.push(currentCell);	// Available for backtracking to
		
		while (visitedCells < totalCells) {
			boolean backTrack = true;	// Assume no undiscovered neighbors
			for (int neighbor : openNeighbors[currentCell]) {
				if (colors.get(neighbor) == Color.WHITE) {
					visitedCells++;	// Visited new cell
					colors.put(neighbor, Color.GREY);	// Discovered cell
					discoveryOrderDFSStack.put(neighbor, time++);	// Discovery time, then increment
					shortestPathDFSStack.put(neighbor, SHORTEST_PATH_MARKER);	// Mark currently taken path
					s.push(currentCell);	// Available for backtracking to
					currentCell = neighbor;	// Move to new cell
					backTrack = false;	// No need to backtrack
					
					if (!listeners.isEmpty()) {
						updateMazePrintListeners(shortestPathDFSStack);	// New marker placed, update listeners
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					if (neighbor == getLength() - 1)	// Located end cell
						visitedCells = totalCells;	// Force early DFS termination
					
					break;	// Don't care about leftover neighbors
				}
			}
			if (backTrack) {
				shortestPathDFSStack.remove(currentCell);	// Backtracked, so this cell not in shortest path
				
				if (!listeners.isEmpty()) {
					updateMazePrintListeners(shortestPathDFSStack);	// Old marker removed, update listeners
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				currentCell = s.pop();	// No undiscovered neighbors, backtrack
			}
		}
	}
	
	/**
	 * Traverses the maze using a recursively-implemented DFS until the exit cell is encountered.
	 * @deprecated Incomplete, use {@link #traverseDFSStack()}
	 */
	@Deprecated
	public void traverseDFSRecursive() {
		if (!generated || !discoveryOrderDFSRecursive.isEmpty())	// Can't traverse a non-existent maze / Already traversed
			return;
		
	  Map<Integer, Color> colors = new HashMap<>(); // Track colors of vertices by index
	  for (int i = 0; i < openNeighbors.length; i++)
	   colors.put(i, Color.WHITE); // Initialize all vertices white
	  int time = 0;
	  colors.put(0, Color.GREY); // Discovered source vertex
	  discoveryOrderDFSRecursive.put(0, 0);
	  for(int i : openNeighbors[0]) {
	   
	   if (colors.get(i) == Color.WHITE) {
	    
	    DFSVisit(colors, discoveryOrderDFSRecursive, i, time);
	   }
	  }
	 }
	 
	 public void DFSVisit(Map<Integer, Color> colors, Map<Integer, Integer> discoveryTimes, int i, int time) {
	  time++;
	  discoveryTimes.put(i, time);

	  colors.put(i, Color.GREY);

	  for(int neighbor : openNeighbors[i]) {
	   if(i == getLength() -1) {
	    break;
	   }
	   if(colors.get(neighbor) == Color.WHITE) {
	    
	    DFSVisit(colors, discoveryTimes, neighbor, time);
	    
	   }
	  }
	  colors.put(i, Color.BLACK);
	  discoveryTimes.put(i, time);
	  time++;
	  
	 }
	
	/**
	 * Generates a random path from start cell to end cell.
	 */
	public void generateRandomPath() {
		generateRandomPath(new Random());
	}
	/**
	 * Generates a path from start cell to end cell using the specified seed.
	 * @param seed seed to generate random path from
	 */
	public void generateRandomPath(long seed) {
		generateRandomPath(new Random(seed));
	}
	private void generateRandomPath(Random random) {
		reset();	// Reset all path-dependent fields
		
		Stack<Integer> cellStack = new Stack<>();
		int currentCell = 0, visitedCells = 1, totalCells = getLength();
		
		while (visitedCells < totalCells) {
			Integer[] neighbors = getLonelyNeighbors(currentCell);
			if (neighbors.length > 0) {
				int randomNeighbor = neighbors[0];	// Default to first lonely neighbor
				if (neighbors.length > 1)
					randomNeighbor = neighbors[random.nextInt(neighbors.length)];	// Choose random lonely neighbor
				breakWall(currentCell, getDirection(currentCell, randomNeighbor));
				cellStack.push(currentCell);
				currentCell = randomNeighbor;
				visitedCells++;
			}
			else
				currentCell = cellStack.pop();
		}		
		discoveryOrderBFS.clear();	// Old discovery order now invalid
		discoveryOrderDFSStack.clear();
		discoveryOrderDFSRecursive.clear();
		
		shortestPathBFS.clear();	// Old shortest path now invalid
		shortestPathDFSStack.clear();
		shortestPathDFSRecursive.clear();
		
		generated = true;
	}
	
	/**
	 * Breaks the wall between a source cell and its neighbor in the given direction.
	 * @param cell source cell index
	 * @param direction direction of neighbor
	 */
	public void breakWall(int cell, Wall direction) {
		int neighbor = getNeighbor(cell, direction);
		rooms[cell].removeWall(direction);	// Remove first wall
		rooms[neighbor].removeOppositeWall(direction);	// Remove neighbor's opposite wall
		
		openNeighbors[cell].add(neighbor);	// Add to adjacency list
		openNeighbors[neighbor].add(cell);	// Symmetrical add to adjacency list
	}
	
	/**
	 * @param cell source cell index
	 * @param direction direction to check
	 * @return index of neighboring cell in the specified direction, or {@value #OUT_OF_BOUNDS} if out of bounds
	 */
	public int getNeighbor(int cell, Wall direction) {
		int neighbor = OUT_OF_BOUNDS;	// Default to out of bounds
		switch (direction) {
			case NORTH:
				neighbor = cell - size;
				break;
			case EAST:
				if (cell % size < size - 1)	// Avoids rightmost cells having neighbor = leftmost cell in row below it
					neighbor = cell + 1;
				break;
			case SOUTH:
				neighbor = cell + size;
				break;
			case WEST:
				if (cell % size > 0)	// Avoids leftmost cells having neighbor = rightmost cell in row above it
					neighbor = cell - 1;
				break;
		}
		if ((neighbor < 0) || (neighbor > getLength() - 1))
			neighbor = OUT_OF_BOUNDS;
		return neighbor;
	}
	/**
	 * @param cell source cell index
	 * @param neighbor neighbor cell index
	 * @return direction of neighbor with respect to source, or {@code null} if not adjacent
	 */
	public Wall getDirection(int cell, int neighbor) {
		if (neighbor == (cell - size))
			return Wall.NORTH;
		else if ((neighbor == cell + 1) && (cell % size < size - 1))
			return Wall.EAST;
		else if (neighbor == (cell + size))
			return Wall.SOUTH;
		else if ((neighbor == cell - 1) && (cell % size > 0))
			return Wall.WEST;
		
		return null;	// Specified neighbor is not a neighbor
	}
	
	/**
	 * @param cell source cell
	 * @return all isolated, adjacent neighbors
	 */
	public Integer[] getLonelyNeighbors(int cell) {
		List<Integer> lonelyNeighbors = new ArrayList<>(4);	// Max 4 neighbors
		for (Wall direction : Wall.values()) {
			int currentNeighborIndex = getNeighbor(cell, direction);
			if (currentNeighborIndex != OUT_OF_BOUNDS && rooms[currentNeighborIndex].isIsolated())	// If neighbor exists and is isolated
				lonelyNeighbors.add(currentNeighborIndex);
			else if (currentNeighborIndex == getLength() - 1 && rooms[currentNeighborIndex].isIsolated(Wall.SOUTH))	// End cell is 'isolated' if all walls besides exit wall intact
				lonelyNeighbors.add(currentNeighborIndex);
		}
		return lonelyNeighbors.toArray(new Integer[lonelyNeighbors.size()]);
	}
	
	/**
	 * @param i index of cell
	 * @return cell at the specified index
	 */
	public Cell getCell(int i) {
		return rooms[i];
	}
	
	/**
	 * Converts a cell's 2D coordinates to its index in the maze.
	 * @param i x-coordinate
	 * @param j y-coordinate
	 * @return index of cell at the specified coordinates
	 */
	public int getLinearPosition(int i, int j) {
		if ((i < 0) || (i > size - 1) || (j < 0) || (j > size - 1))	// Check bounds
			throw new IndexOutOfBoundsException("(" + String.valueOf(i) + ", " + String.valueOf(j) + ")");
		return (i + (size * j));
	}
	
	/**
	 * @return number of cell along each edge
	 */
	public int getEdgeLength() {
		return size;
	}
	/**
	 * @return total number of cells
	 */
	public int getLength() {
		return size * size;
	}
	
	/**
	 * @return String representation of maze
	 */
	public String buildString() {
		return buildString(null, false, 0);
	}
	public String buildStringForDisplay() {
		return buildStringOverrideWidth(null, DISPLAY_WIDTH);
	}
	/**
	 * @param uglyStyle only print last character of each cell value
	 * @return String representation of maze, with cell discovery time during BFS traversal printed in center of each cell
	 */
	public String buildStringBFS(boolean uglyStyle) {
		return buildString(discoveryOrderBFS, uglyStyle, 0);
	}
	/**
	 * @param uglyStyle only print last character of each cell value
	 * @return String representation of maze, with cell discovery time during DFS-Stack traversal printed in center of each cell
	 */
	public String buildStringDFSStack(boolean uglyStyle) {
		return buildString(discoveryOrderDFSStack, uglyStyle, 0);
	}
	/**
	 * @param uglyStyle only print last character of each cell value
	 * @return String representation of maze, with cell discovery time during DFS-Recursive traversal printed in center of each cell
	 */
	public String buildStringDFSRecursive(boolean uglyStyle) {
		return buildString(discoveryOrderDFSRecursive, uglyStyle, 0);
	}
	
	/**
	 * @return String representation of maze with shortest path found by BFS highlighted by {@value #SHORTEST_PATH_CHAR}.
	 */
	public String buildStringShortestPathBFS() {
		return buildString(shortestPathBFS, false, SHORTEST_PATH_CHAR.length());
	}
	/**
	 * @return String representation of maze with shortest path found by DFS-Stack highlighted by {@value #SHORTEST_PATH_CHAR}.
	 */
	public String buildStringShortestPathDFSStack() {
		return buildString(shortestPathDFSStack, false, SHORTEST_PATH_CHAR.length());
	}
	/**
	 * @return String representation of maze with shortest path found by DFS-Recursive highlighted by {@value #SHORTEST_PATH_CHAR}.
	 */
	public String buildStringShortestPathDFSRecursive() {
		return buildString(shortestPathDFSRecursive, false, SHORTEST_PATH_CHAR.length());
	}

	private String buildStringOverrideWidth(Map<Integer, Integer> cellValues, int overrideWidth) {
		return buildString(cellValues, false, overrideWidth);
	}
	private String buildString(Map<Integer, Integer> cellValues, boolean uglyStyle, int overrideWidth) {
		int cellWidth = 1;	// Default cell width
		if (overrideWidth >= cellWidth)
			cellWidth = overrideWidth;
		else {
			if (cellValues != null && !uglyStyle)	// Expand cell width to accommodate values
				cellWidth = String.valueOf(Misc.max(cellValues)).length();	// Number of chars in greatest value
		}
		String display = "";
		
		for (int i = 0; i < size; i++) {	// Top edge of maze left->right
			Cell currentCell = getCell(getLinearPosition(i, 0));	// Each row's SOUTH walls are next row's NORTH walls, only print NORTH walls for top row
			if (i == 0)	// Print left corner only for leftmost column
				display += "+";
			if (currentCell.hasWall(Wall.NORTH))
				display += Misc.repeat('-', cellWidth);	// NORTH wall
			else
				display += Misc.repeat(' ', cellWidth);	// No NORTH wall
			display += "+";	// Right corner
		}
		display += "\n";	// Top edge of maze done
		
		for (int j = 0; j < size; j++) {	// Iterate top->down by row
			for (int i = 0; i < size; i++) {	// Mid part of current row cells
				Cell currentCell = getCell(getLinearPosition(i, j));
				if (i == 0) {	// Each column's EAST walls are next column's WEST walls, only print WEST walls for leftmost column
					if (currentCell.hasWall(Wall.WEST))
						display += "|";	// WEST wall
					else
						display += " ";	// No WEST wall
				}
				if (cellValues != null && cellValues.get(getLinearPosition(i, j)) != null) {	// Value exists for current cell
					int currentValue = cellValues.get(getLinearPosition(i, j));	// Current value to print
					String currentValueString = String.valueOf(currentValue);	// To String
					if (currentValue == SHORTEST_PATH_MARKER)	// Intercept hash marker (Avoids refactoring method to accept Map<Integer, String>)
						currentValueString = SHORTEST_PATH_CHAR;
					else if (currentValue == SHORTEST_PATH_TRIAL_MARKER)
						currentValueString = SHORTEST_PATH_TRIAL_CHAR;
					
					if (uglyStyle)
						display += Misc.chop(currentValueString);	// Print last digit of value
					else
						display += Misc.center(currentValueString, cellWidth);	// Center value
				}
				else	// No value to print
					display += Misc.repeat(' ', cellWidth);
				
				if (currentCell.hasWall(Wall.EAST))
					display += "|";	// EAST wall
				else
					display += " ";	// No EAST wall
			}
			display += "\n";	// Mid part of current row done
			for (int i = 0; i < size; i++) {	// Bottom part of current row cells
				Cell currentCell = getCell(getLinearPosition(i, j));
				if (i == 0)
					display += "+";
				if (currentCell.hasWall(Wall.SOUTH))
					display += Misc.repeat('-', cellWidth);
				else
					display += Misc.repeat(' ', cellWidth);
				display += "+";
			}
			display += "\n";
		}
		return display;
	}
	
	/**
	 * Adds a listener interested in displaying maze traversals.
	 * @param listener listener to add
	 */
	public void addMazePrintListener(MazePrintListener listener) {
		listeners.add(listener);
	}
	private void updateMazePrintListeners(Map<Integer, Integer> cellValues) {
		for (MazePrintListener listener : listeners)
			listener.updateMaze(buildStringOverrideWidth(cellValues, DISPLAY_WIDTH));
	}
	
	/**
	 * Provides colors for graph marking
	 */
	public enum Color {
		WHITE, GREY, BLACK;
	}
}
