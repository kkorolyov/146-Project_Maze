package sjsu.cs146.project3;

public class Maze {
    private LinkedList<Integer>[] adjList; //adjacency list
    private int[][] maze; //maze to be numbered row one: 0, 1, 2, 3,..., n; row two: n + 1, n + 2, ...; and so on
    private int listCols;
    private int listRows;
    private int size;
    
    public Maze(int s) {
        size = s;
        int rooms = size*size;
        adjList = new LinkedList[rooms];
        maze = new int[size][size];
        int mazeRooms = 1;
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                maze[i][j] = mazeRooms++;
            }
        }
        //these two for loops initialize all of the rooms to point to the rooms to the left and below
        //I wasn't sure if we should have the vertices point to all of the rooms around them, so
        //I made them one directional for now
        for (int i = 0; i < rooms-1; i++) {
            adjList[i] = new LinkedList<Integer>();
        }
        for (int i = 0; i < rooms-1; i++) {
            if(i%size != 3)
                adjList[i].add(i+1);
            if(i < size * size - size)
                adjList[i].add(i+4);
        } 
    }
}
