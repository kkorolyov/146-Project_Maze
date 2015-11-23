package sjsu.cs146.project3;

public class MazeK {
	private Cell[] rooms;
	
	public MazeK(int size) {
		rooms = new Cell[size * size];
		for (int i = 0; i < rooms.length; i++)	// Populate with completely enclosed cells
			rooms[i] = new Cell();
		
		rooms[0].removeWall(Wall.NORTH);	// Start cell
		rooms[rooms.length - 1].removeWall(Wall.SOUTH);	// End cell
	}
	
	private class Cell {
		private Wall[] walls = new Wall[4];	// Cell has up to 4 walls
		
		/**
		 * Constructs a completely-enclosed {@code Cell} (4 walls)
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
		 * @return {@code true} if wall located and removed
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
