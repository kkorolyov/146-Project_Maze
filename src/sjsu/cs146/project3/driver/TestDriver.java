package sjsu.cs146.project3.driver;

import java.util.Scanner;

import org.junit.runner.JUnitCore;

import sjsu.cs146.project3.MazeTest;
import sjsu.cs146.project3.props.Props;

/**
 * Provides a terminal interface for setting {@link MazeTest} properties and running {@link MazeTest}.
 */
public class TestDriver {
	private static final String RUN_TEST_CMD = "0";
	private static final char SWITCH_PRESET = '1', SWITCH_RANDOM = '2', SWITCH_MAZE = '3', SWITCH_BFS = '4', SWITCH_DFS_STACK = '5', SWITCH_DFS_RECURSIVE = '6', SWITCH_CENTERED = '7', SWITCH_CHOPPED = '8', SWITCH_SHORTEST = '9'; 

	public static void main(String[] args) {
		setProps();
		JUnitCore.main(MazeTest.class.getName());
	}
	
	private static void setProps() {
		Scanner input = new Scanner(System.in);
		String cmd = "";
		
		while (!cmd.equals(RUN_TEST_CMD) ) {
			System.out.println(	"Enter the appropriate number to activate/deactivate\n"
												+ "Enter '0' or nothing to run MazeTest with current properties\n"
												+ "Current Props:\n"
												+ SWITCH_PRESET + ". print maze generated from preset seed----" + String.valueOf(Props.printPreset).toUpperCase() + "\n"
												+ SWITCH_RANDOM + ". print maze generated from random seed----" + String.valueOf(Props.printRandom).toUpperCase() + "\n"
												+ SWITCH_MAZE + ". print bare maze----" + String.valueOf(Props.printMaze).toUpperCase() + "\n"
												+ SWITCH_BFS + ". print BFS traversal----" + String.valueOf(Props.printBFS).toUpperCase() + "\n"
												+ SWITCH_DFS_STACK + ". print DFS-stack traversal----" + String.valueOf(Props.printDFSStack).toUpperCase() + "\n"
												//+ SWITCH_DFS_RECURSIVE + ". print DFS-recursive traversal----" + String.valueOf(Props.printDFSRecursive).toUpperCase() + "\n"
												+ SWITCH_CENTERED + ". print maze traversal with centered discovery values----" + String.valueOf(Props.printValuesCentered).toUpperCase() + "\n"
												+ SWITCH_CHOPPED + ". print maze traversal with last digit of discovery values----" + String.valueOf(Props.printValuesChopped).toUpperCase() + "\n"
												+ SWITCH_SHORTEST + ". print shortest path found by traversals----" + String.valueOf(Props.printShortestPath).toUpperCase() + "\n");
			cmd = input.nextLine();
			if (cmd.length() < 1)
				break;
			switch (cmd.charAt(0)) {
				case (SWITCH_PRESET):
					Props.printPreset = !Props.printPreset;
					break;
				case (SWITCH_RANDOM):
					Props.printRandom = !Props.printRandom;
					break;
				case (SWITCH_MAZE):
					Props.printMaze = !Props.printMaze;
					break;
				case (SWITCH_BFS):
					Props.printBFS = !Props.printBFS;
					break;
				case (SWITCH_DFS_STACK):
					Props.printDFSStack = !Props.printDFSStack;
					break;
				case (SWITCH_DFS_RECURSIVE):
					Props.printDFSRecursive = !Props.printDFSRecursive;
					break;
				case (SWITCH_CENTERED):
					Props.printValuesCentered = !Props.printValuesCentered;
					break;
				case (SWITCH_CHOPPED):
					Props.printValuesChopped = !Props.printValuesChopped;
					break;
				case (SWITCH_SHORTEST):
					Props.printShortestPath = !Props.printShortestPath;
					break;
			}
		}		
		input.close();
	}
}
