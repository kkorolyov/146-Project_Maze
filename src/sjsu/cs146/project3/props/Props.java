package sjsu.cs146.project3.props;

/**
 * Lazy container class for MazeTest properties.
 * By default, match project requirements.
 */
public class Props {
	/**
	 * If {@code true}, print maze generated from preset seed
	 * */
	public static boolean printPreset = true;
	/** 
	 * If {@code true}, print maze generated from random seed
	 */
	public static boolean printRandom = false; 
	
	/**
	 * If {@code true}, print bare maze
	 */
	public static boolean printMaze = true;
	/**
	 * If {@code true}. print BFS traversal
	 */
	public static boolean printBFS = true;
	/**
	 * If {@code true}, print DFS-stack traversal
	 */
	public static boolean printDFSStack = true;
	/**
	 * If {@code true}, print DFS-recursive traversal
	 */
	public static boolean printDFSRecursive = false;

	/**
	 * If {@code true}, print maze traversal with centered values
	 */
	public static boolean printValuesCentered = false;
	/**
	 * If {@code true}, print maze traversal with last digit of values
	 */
	public static boolean printValuesChopped = true;
	/**
	 * If {@code true}, print shortest path found by traversals
	 */
	public static boolean printShortestPath = true;
}
