package sjsu.cs146.project3.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import sjsu.cs146.project3.Maze;

public class MazeFrame extends JFrame implements MazePrintListener {
	private static final long serialVersionUID = 9084829050844998479L;
	private static final int[] MAZE_SIZES = {2, 4, 8, 16};
	
	private JTextArea mazeDisplay = new JTextArea();
	private JPanel sizeButtonPanel = new JPanel(), traverseButtonPanel = new JPanel();
	private JButton[] sizeButtons = new JButton[MAZE_SIZES.length];
	private JButton bfsButton = new JButton("BFS"), dfsButton = new JButton("DFS");
	
	private Maze maze;
	
	public MazeFrame(String title, int width, int height) {
		super(title);
		setPreferredSize(new Dimension(width, height));
		setLayout(new BorderLayout());
		
		maze = new Maze(MAZE_SIZES[0]);	// Default to smallest size
		maze.addMazePrintListener(this);
		
		buildFrame();		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	private void buildFrame() {
		buildMazeDisplay();
		buildSizeButtonPanel();
		buildTraverseButtonPanel();
		pack();
	}
	private void buildMazeDisplay() {
		mazeDisplay.setFont(new Font("Monospaced", Font.BOLD, 12));
		add(mazeDisplay, BorderLayout.CENTER);
	}
	private void buildSizeButtonPanel() {
		for (int i = 0; i < sizeButtons.length; i++) {
			final int currentSize = MAZE_SIZES[i];
			sizeButtons[i] = new JButton(String.valueOf(currentSize));
			sizeButtons[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					maze = new Maze(currentSize);
					maze.addMazePrintListener(MazeFrame.this);
					String size = String.valueOf(currentSize);
					mazeDisplay.setText("Created " + size + "x" + size + " maze");
				}
			});
			sizeButtonPanel.add(sizeButtons[i]);
		}
		add(sizeButtonPanel, BorderLayout.NORTH);
	}
	private void buildTraverseButtonPanel() {
		bfsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					public void run() {
						maze.generateRandomPath();	// Regenerate
						String size = String.valueOf(maze.getEdgeLength());
						mazeDisplay.setText(	maze.buildStringForDisplay()
																+ size + "x" + size + " BFS");
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						maze.traverseBFS();
					}
				}.start();
			}
		});
		dfsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					public void run() {
						maze.generateRandomPath();	// Regenerate
						String size = String.valueOf(maze.getEdgeLength());
						mazeDisplay.setText(	maze.buildStringForDisplay()
																+ size + "x" + size + " DFS");
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						maze.traverseDFSStack();
					}
				}.start();
			}
		});
		
		traverseButtonPanel.add(bfsButton);
		traverseButtonPanel.add(dfsButton);
		add(traverseButtonPanel, BorderLayout.SOUTH);
	}

	@Override
	public void updateMaze(String newMaze) {
		mazeDisplay.setText(newMaze);
	}
}
