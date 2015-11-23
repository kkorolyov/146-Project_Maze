package sjsu.cs146.project3;

import java.util.*;

public class MazeK {
	private int size;
	private Cell[] rooms;	// All Cell objects
	private List<Integer>[] openNeighbors;	// All adjacent cell indices for each source cell index
	
	@SuppressWarnings("unchecked")
	public MazeK(int s) {
		size = s;
		rooms = new Cell[size * size];
		openNeighbors = new LinkedList[size * size];
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
		int currentCell = 0, visitedCells = 1, totalCells = size * size;
		
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
	 * @return index of neighboring cell in the specified direction
	 */
	public int getNeighbor(int cell, Wall direction) {
		int neighbor = -1;
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
		if (neighbor < 0 || neighbor > size * size - 1)
			throw new IndexOutOfBoundsException(String.valueOf(neighbor));
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
			if (rooms[currentNeighborIndex].isIsolated())
				lonelyNeighbors.add(currentNeighborIndex);
		}
		return lonelyNeighbors.toArray(new Integer[lonelyNeighbors.size()]);
	}
	
	private class Cell {
		private Wall[] walls = new Wall[4];	// Cell has up to 4 walls
		
		/**
		 * Constructs a new, completely-enclosed {@code Cell} (4 walls)
		 */
		public Cell() {
			resetWalls();
		}
		
		/**
		 * Adds a wall to the Cell.
		 * @param addWall wall to add
		 * @return {@code true} if wall added successfully, {@code false} if otherwise
		 */
		public boolean addWall(Wall addWall) {
			if (hasWall(addWall))	// Check if wall already exists
				return false;	// No layered walls
			return addWallOverride(addWall);
		}
		private boolean addWallOverride(Wall addWall) {	// Add wall without checking for prior existence
			int addWallIndex = getWallIndex(null);	// Locate 1st empty slot
			if (addWallIndex >= 0) {
				walls[addWallIndex] = addWall;
				return true;
			}
			return false;	// Nowhere to add wall (Should not happen)
		}
		
		/**
		 * Resets the Cell's walls.
		 * Results in a completely-enclosed Cell.
		 */
		public void resetWalls() {
			for (int i = 0; i < walls.length; i++) {
				walls[i] = Wall.values()[i];
			}
		}
		
		/**
		 * Removes the specified wall from the Cell.
		 * @param removeWall wall to remove
		 * @return {@code true} if wall located and removed, {@code false} if otherwise
		 */
		public boolean removeWall(Wall removeWall) {
			int removeWallIndex = getWallIndex(removeWall);
			if (removeWallIndex >= 0) {
				walls[removeWallIndex] = null;
				return true;
			}
			return false;
		}
		/**
		 * Removes the wall opposite the specified wall from the Cell.
		 * @param removeWall wall to remove opposite of
		 * @return {@code true} if opposite wall located and removed, {@code false} if otherwise
		 */
		public boolean removeOppositeWall(Wall removeWall) {
			Wall oppositeWall = null;
			switch (removeWall) {
				case NORTH:
					oppositeWall = Wall.SOUTH;
					break;
				case EAST:
					oppositeWall = Wall.WEST;
					break;
				case SOUTH:
					oppositeWall = Wall.NORTH;
					break;
				case WEST:
					oppositeWall = Wall.EAST;
					break;
			}
			return removeWall(oppositeWall);
		}
		
		/**
		 * @param checkWall wall to check if exists
		 * @return {@code true} if wall found, {@code false} if otherwise
		 */
		public boolean hasWall(Wall checkWall) {
			if (getWallIndex(checkWall) >= 0)	// Wall found
				return true;
			return false;
		}
		
		/**
		 * @return {@code true} if all walls intact, {@code false} if at least 1 wall not intact
		 */
		public boolean isIsolated() {
			for (Wall wall : Wall.values()) {
				if (!hasWall(wall))
					return true;	// At least 1 wall does not exist
			}
			return true;	// All walls exist
		}
		
		/**
		 * @param wall wall to locate
		 * @return index of the specified wall in the Cell's list of walls
		 */
		public int getWallIndex(Wall wall) {
			for (int i = 0; i < walls.length; i++) {
				if (walls[i] == wall)
					return i;
			}
			return -1;
		}
	}
	
	private enum Wall {
		NORTH, EAST, WEST, SOUTH;
	}
}
