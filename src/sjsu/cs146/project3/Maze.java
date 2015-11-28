package sjsu.cs146.project3;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import sjsu.cs146.project3.Cell.Wall;

/**
 * A randomly-generated perfect maze.
 * Supplies methods for solving itself.
 */
public class Maze {	// TODO DFS, # markers for shortest path (Tweak buildString() to accept String, not ints for reusability)
	public static final int OUT_OF_BOUNDS = -1;	// Marker for out of bounds cell
	
	private int size;	// Size of each edge of maze
	private boolean generated;	// If maze has been generated
	private Cell[] rooms;	// All cells in maze
	private List<Integer>[] openNeighbors;	// All adjacent cell indices for each source cell index
	
	/**
	 * Constructs a new, square, s x s sized maze.
	 * @param s length of each side of maze
	 */
	@SuppressWarnings("unchecked")
	public Maze(int s) {
		size = s;
		rooms = new Cell[getLength()];
		openNeighbors = new LinkedList[getLength()];
		for (int i = 0; i < openNeighbors.length; i++)	// Initialize adjacency lists
			openNeighbors[i] = new LinkedList<>();
		for (int i = 0; i < rooms.length; i++)	// Populate with completely enclosed cells
			rooms[i] = new Cell();
		
		rooms[0].removeWall(Wall.NORTH);	// Start cell
		rooms[rooms.length - 1].removeWall(Wall.SOUTH);	// End cell
	}
	
	/**
	 * Traverses the maze until the exit cell is encountered.
	 * @return map of cell indices and their discovery times, or {@code null} if no maze to traverse
	 */
	public Map<Integer, Integer> traverseBFS() {
		if (!generated)	// Can't traverse a non-existent maze
			return null;
		Map<Integer, Integer> discoveryTimes = new HashMap<>();	// Will return
		Map<Integer, Color> colors = new HashMap<>();	// Track colors of vertices by index
		for (int i = 0; i < openNeighbors.length; i++)
			colors.put(i, Color.WHITE);	// Initialize all vertices white
		Queue<Integer> q = new LinkedBlockingQueue<>();
		colors.put(0, Color.GREY);	// Discovered source vertex
		discoveryTimes.put(0, 0);	// Source vertex discovery time = 0
		q.add(0);	// Enqueue source vertex
		
		while (!q.isEmpty()) {
			int currentSource = q.remove();
			for (int neighbor : openNeighbors[currentSource]) {	// All adjacent cells
				if (colors.get(neighbor) == Color.WHITE) {	// Unexplored vertex
					colors.put(neighbor, Color.GREY);	// Neighbor discovered
					discoveryTimes.put(neighbor, discoveryTimes.get(currentSource) + 1);	// Neighbor discovery time
					
					if (neighbor == getLength() - 1)	// Located end cell
						q.clear();	// Force early BFS loop termination
					else
						q.add(neighbor);	// Enqueue neighbor
				}
				colors.put(currentSource, Color.BLACK);	// Source fully explored
			}
		}
		return discoveryTimes;
	}
	
	public Map<Integer, Integer> traverseDFS() {
		if (!generated)	// Can't traverse a non-existent maze
			return null;
		Map<Integer, Integer> discoveryTimes = new HashMap<>();
		Map<Integer, Color> colors = new HashMap<>();
		for (int i = 0; i < openNeighbors.length; i++)
			colors.put(i, Color.WHITE);
		Stack<Integer> s = new Stack<>();
		int time = 0;
		s.push(0);
		
		while (!s.isEmpty()) {
			int currentSource = s.pop();
			time++;
			colors.put(currentSource, Color.GREY);
			discoveryTimes.put(currentSource, time);
			
			for (int neighbor : openNeighbors[currentSource]) {
				if (colors.get(neighbor) == Color.WHITE) {
					time++;
					colors.put(neighbor, Color.GREY);
					s.push(neighbor);
				}
			}
			time++;
			colors.put(currentSource, Color.BLACK);
			discoveryTimes.put(currentSource, time);
		}
		return discoveryTimes;
	}
	
	public Map<Integer, Integer> DFS() {
	  Map<Integer, Integer> discoveryTimes = new HashMap<>(); // Will return
	  Map<Integer, Color> colors = new HashMap<>(); // Track colors of vertices by index
	  for (int i = 0; i < openNeighbors.length; i++)
	   colors.put(i, Color.WHITE); // Initialize all vertices white
	  int time = 0;
	  colors.put(0, Color.GREY); // Discovered source vertex
	  discoveryTimes.put(0, 0);
	  for(int i : openNeighbors[0]) {
	   
	   if (colors.get(i) == Color.WHITE) {
	    
	    DFSVisit(colors, discoveryTimes, i, time);
	   }
	  }
	  
	  return discoveryTimes;
	 }
	 
	 public void DFSVisit(Map<Integer, Color> colors, Map<Integer, Integer> discoveryTimes, int i, int time) {
	  time++;
	  discoveryTimes.put(i, time);
	  //System.out.println(time);
	  //System.out.println(discoveryTimes.get(i));

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
			generated = true;
		}
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
	 * @return total number of cells
	 */
	public int getLength() {
		return size * size;
	}
	
	/**
	 * @return String representation of maze
	 */
	public String buildString() {
		return buildString(null, false);
	}
	/**
	 * @param cellValues cell indices and respective values
	 * @param uglyStyle only print lowest digit of each cell value
	 * @return String representation of maze, with appropriate cell value printed in center of each cell
	 */
	public String buildString(Map<Integer, Integer> cellValues, boolean uglyStyle) {
		int cellWidth = 1;	// Default cell width
		if (cellValues != null && !uglyStyle)	// Expand cell width to accommodate values
			cellWidth = String.valueOf(Misc.max(cellValues)).length();	// Number of chars in greatest value
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
					if (uglyStyle)
						display += Misc.chop(String.valueOf(cellValues.get(getLinearPosition(i, j))));	// Print last digit of value
					else
						display += Misc.center(String.valueOf(cellValues.get(getLinearPosition(i, j))), cellWidth);	// Center value
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
	
	public enum Color {
		WHITE, GREY, BLACK;
	}
}
