//Alex Le Blanc
//260803654
//No collaborators
import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.math.*;
import static java.lang.System.out;

public class mancala {
  
  public static void main(String[] args) {
    //long start = System.currentTimeMillis();
    int[][] moves = createMoves();
    ArrayList<int[]> allBoards = new ArrayList<int[]>(getVals());
    for (int i=0; i<allBoards.size(); i++) {
      int absMin = allMoves(allBoards.get(i), moves, 12);
      outputData(absMin);
    }
    //long time = System.currentTimeMillis() - start;
    //System.out.println(time);
  }
  
  public static int[][] createMoves() { //method that generates array of possible moves (each "move" is an array with, in order, the piece that's moving, then the piece that's being removed, then the destination of the moving piece)
    int[][] moves = new int[20][];
    moves[0] = new int[]{0,1,2};
    moves[1] = new int[]{1,2,3};
    moves[18] = new int[]{10,9,8};
    moves[19] = new int[]{11,10,9};
    for (int i=2; i<10; i++) {
      moves[2*i-2] = new int[]{i,i-1,i-2};
      moves[2*i-1] = new int[]{i,i+1,i+2};
    }
    return moves;
  }
  
  public static int allMoves(int[] board, int[][] moves, int min) { //method that performs all possible sequences of moves and returns minimum number of remaining full slots
    if (occSpaces(board)<min) {
      min = occSpaces(board);
    }
    for (int i=0; i<20; i++) {
      if (isMoveValid(board,moves[i])) {
        doMove(board, moves[i]);
        min = allMoves(board, moves, min);
        undoMove(board, moves[i]);
      }
    }
    return min;
  }
  
  public static int occSpaces(int[] board) { //method that checks number of occupied spaces on the board
    int occ = 0;
    for (int i=0; i<12; i++) {
      if (board[i]==1) {
        occ++;
      }
    }
    return occ;
  }
  
  public static int[] doMove(int[] board, int[] moves) { //method that performs a single move
    board[moves[0]]=0;
    board[moves[1]]=0;
    board[moves[2]]=1;
    return board;
  }
  
  public static int[] undoMove(int[] board, int[] moves) { //method that undoes a single move
    board[moves[0]]=1;
    board[moves[1]]=1;
    board[moves[2]]=0;
    return board;
  }
  
  public static boolean isMoveValid(int[] board, int[] moves) { //method that returns true if a specific move can be done
    if (board[moves[0]]==1 && board[moves[1]]==1 && board[moves[2]]==0) {
      return true;
    }
    else {
      return false;
    }
  }
  
  public static List<String> getLines() { //method that reads input file line by line and returns list where each element is a line from the file
    List<String> lines = new ArrayList<String>();
    try {
      BufferedReader in = new BufferedReader(new FileReader("testMancala.txt"));
      String str;
      while((str = in.readLine()) != null){
        lines.add(str);
      }
    }
    catch(Exception e) {
      System.out.println("EXCEPTION!!!!");
    }
    return lines;
  }
  
  public static ArrayList<int[]> getVals() { //method that returns arraylist where each element is the Mancala board for the given problem
    ArrayList<int[]> vals = new ArrayList<int[]>();
    ArrayList<String> lines = new ArrayList<String>(getLines());
    int size = lines.size();
    for (int i=1; i<size; i++) {
      String[] temp = lines.get(i).split("\\s+");
      int[] subVals = new int[temp.length];
      for (int j=0; j<temp.length; j++) {
        subVals[j] = Integer.parseInt(temp[j]);
      }
      vals.add(subVals);
    }
    return vals;
  }
  
  public static void outputData(int data) { //takes an integer and appends it to specified file
    try
    {
      String filename= "testMancala_solution.txt";
      FileWriter fw = new FileWriter(filename,true);
      fw.write(Integer.toString(data)+"\n");
      fw.close();
    }
    catch(IOException e)
    {
      System.out.println("IOException!!!");
    }
  }
  
}