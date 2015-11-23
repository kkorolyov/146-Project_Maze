package sjsu.cs146.project3;

import java.util.ArrayList;
import java.util.List;

public class MazeK {
	private Cell[] rooms;
	
	public MazeK(int size) {
		rooms = new Cell[size * size];
		for (int i = 0; i < rooms.length; i++)	// Populate with completely enclosed cells
			rooms[i] = new Cell();
	}
	
	private class Cell {
		private Wall[] walls = new Wall[4];	// Cell has up to 4 walls
		private List<Cell> adjacentCells = new ArrayList<>();
		
		/**
		 * Constructs a completely-enclosed {@code Cell} (4 walls)
		 */
		public Cell() {
			walls[0] = Wall.NORTH;
			walls[1] = Wall.EAST;
			walls[2] = Wall.SOUTH;
			walls[3] = Wall.WEST;
		}
		
		public boolean hasWall(Wall checkWall) {
			for (Wall wall : walls) {
				if (wall == checkWall)
					return true;
			}
			return false;
		}
	}
	private enum Wall {
		NORTH, EAST, WEST, SOUTH;
	}
}
