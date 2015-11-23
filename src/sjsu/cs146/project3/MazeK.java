package sjsu.cs146.project3;

import java.util.LinkedList;
import java.util.List;

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
		if (neighbor < 0 || neighbor > size * size)
			throw new IndexOutOfBoundsException(String.valueOf(neighbor));
		return neighbor;
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
