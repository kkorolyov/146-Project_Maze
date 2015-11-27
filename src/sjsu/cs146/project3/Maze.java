package sjsu.cs146.project3;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

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
	
	public Map<Integer, Integer> BFS() {
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
					colors.remove(neighbor);
					colors.put(neighbor, Color.GREY);	// Neighbor discovered
					discoveryTimes.put(neighbor, discoveryTimes.get(currentSource) + 1);	// Neighbor discovery time
					q.add(neighbor);	// Enqueue neighbor
				}
				colors.remove(currentSource);
				colors.put(currentSource, Color.BLACK);	// Source fully explored
			}
		}
		return discoveryTimes;
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
	 * @return direction to move from source cell to neighbor
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
	
	public String buildString() {
		return buildString(null);
	}
	public String buildString(Map<Integer, Integer> values) {	// TODO Optimize
		int cellWidth = 1;	// Default cell width
		if (values != null)	// Expand cell width to accommodate values
			cellWidth = String.valueOf(getLength() - 1).length();	// Number of chars in greatest value
		String display = "";
		for (int j = 0; j < size; j++) {
			if (j == 0) {	// Each row's SOUTH walls are next row's NORTH walls, only print NORTH walls for top row
				for (int i = 0; i < size; i++) {	// Top part of cells
					Cell currentCell = getCell(getLinearPosition(i, j));
					if (i == 0)
						display += "+";
					if (currentCell.hasWall(Wall.NORTH))
						display += expand('-', cellWidth);
					else
						display += expand(' ', cellWidth);
					display += "+";
				}
				display += "\n";
			}
			for (int i = 0; i < size; i++) {	// Mid part of cells
				Cell currentCell = getCell(getLinearPosition(i, j));
				if (i == 0) {	// Each column's EAST walls are next column's WEST walls, only print WEST walls for leftmost column
					if (currentCell.hasWall(Wall.WEST))
						display += "|";
					else
						display += " ";
				}
				if (values == null)	// Only print maze walls
					display += expand(' ', cellWidth);
				else
					display += expand(values.get(getLinearPosition(i, j)), cellWidth);	// Print value for the current cell
				if (currentCell.hasWall(Wall.EAST))
					display += "|";
				else
					display += " ";
			}
			display += "\n";
			for (int i = 0; i < size; i++) {	// Bottom part of cells
				Cell currentCell = getCell(getLinearPosition(i, j));
				if (i == 0)
					display += "+";
				if (currentCell.hasWall(Wall.SOUTH))
					display += expand('-', cellWidth);
				else
					display += expand(' ', cellWidth);
				display += "+";
			}
			display += "\n";
		}
		return display;
	}
	
	private String expand(char source, int length) {	// Expands a single character
		String expanded = "";
		for (int i = 0; i < length; i++)
			expanded += source;
		return expanded;
	}
	private String expand(int source, int length) {	// Adds filler on either side of a number
		String expanded = String.valueOf(source);
		boolean addToEnd = true;
		if (expanded.length() < length) {
			for (int i = expanded.length(); i < length; i++) {
				if (addToEnd)	// Add filler to end
					expanded += " ";	// Fill with spaces
				else	// Add filler to beginning
					expanded = " " + expanded;
				addToEnd = !addToEnd;	// Alternate adding to end and start
			}
		}
		return expanded;
	}
	
	public enum Color {
		WHITE, GREY, BLACK;
	}
}
