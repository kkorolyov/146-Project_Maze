package sjsu.cs146.project3;

import java.util.*;

import sjsu.cs146.project3.Cell.Wall;

public class Maze {
	public static final int OUT_OF_BOUNDS = -1;	// Marker for out of bounds cell
	
	private int size;
	private Cell[] rooms;	// All Cell objects
	private List<Integer>[] openNeighbors;	// All adjacent cell indices for each source cell index
	
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
	 * @return index of neighboring cell in the specified direction, or -1 if out of bounds
	 */
	public int getNeighbor(int cell, Wall direction) {
		int neighbor = OUT_OF_BOUNDS;	// Default to out of bounds
		switch (direction) {
			case NORTH:
				neighbor = cell - size;
				break;
			case EAST:
				neighbor = cell + 1;
				break;
			case SOUTH:
				neighbor = cell + size;
				break;
			case WEST:
				neighbor = cell - 1;
				break;
		}
		if (neighbor < 0 || neighbor > getLength() - 1)
			neighbor = OUT_OF_BOUNDS;
		return neighbor;
	}
	/**
	 * @param cell source cell index
	 * @param neighbor neighbor cell index
	 * @return direction to move from source cell to neighbor
	 */
	public Wall getDirection(int cell, int neighbor) {
		if (neighbor == (cell - size))
			return Wall.NORTH;
		else if (neighbor == (cell + 1))
			return Wall.EAST;
		else if (neighbor == (cell + size))
			return Wall.SOUTH;
		else if (neighbor == (cell - 1))
			return Wall.WEST;
		
		return null;	// Specified neighbor is not a neighbor
	}
	
	public Integer[] getLonelyNeighbors(int cell) {
		List<Integer> lonelyNeighbors = new ArrayList<>(4);	// Max 4 neighbors
		for (Wall direction : Wall.values()) {
			int currentNeighborIndex = getNeighbor(cell, direction);
			if (currentNeighborIndex != OUT_OF_BOUNDS && rooms[currentNeighborIndex].isIsolated())	// If neighbor exists and is isolated
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
}
