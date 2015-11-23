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
        //these two for loops initialize all of the rooms to point to the rooms to the left, to the right, above and below
        //if the adjacent room is within the maze. 
        //EX: room 0 is the top left corner room and points at rooms 1 and 4; room 6 points to 3, 5, 7, and 10 
        for (int i = 0; i < rooms; i++) {
            adjList[i] = new LinkedList<Integer>();
        }
        for (int i = 0; i < rooms-1; i++) {
            System.out.println("check " + i);
            if(i%size != 3) {
                adjList[i].add(i+1);
                adjList[i+1].add(i);
            }
            if(i < size * size - size) {
                adjList[i].add(i+4);
                adjList[i+4].add(i);
            }
        } 
    }
}
