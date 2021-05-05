//Alex Le Blanc
//260803654
//No collaborators
import java.util.*;
import java.io.*;
import java.util.regex.*;
import java.math.*;
import static java.lang.System.out;

public class balloon {
  
  public static void main(String[] args) {
    //long start = System.currentTimeMillis();
    ArrayList<int[]> balloons = new ArrayList<int[]>(getVals());
    for (int i=0; i<balloons.size(); i++) {
      int[] corr = balloons.get(i).clone();
      int absMin = minArrows(corr);
      outputData(absMin);
    }
    //long time = System.currentTimeMillis() - start;
    //System.out.println(time);
  }
  
  public static int minArrows(int[] heights) { //method that returns the minimum required arrows needed to be shot for a given problem
    int min=0;
    int count=0;
    while (notDone(heights)) {
      int myHeight = arrayMax(heights);
      for (int i=0; i<heights.length; i++) {
        if (heights[i] == myHeight) {
          heights[i] = -10;
          myHeight--;
          count++;
        }
      }
      if (count>0) {
        min++;
      }
      count=0;
    }
    return min;
  }
  
  public static int arrayMax(int[] arr) { //method that returns the maximum value in an int array
    int max=0;
    for (int i=0; i<arr.length; i++) {
      if (arr[i]>max) {
        max = arr[i];
      }
    }
    return max;
  }
  
  public static boolean notDone(int[] arr) { //method that returns true if there exists an element in the array that is not -10
    for (int i=0; i<arr.length; i++) {
      if (arr[i]!=-10) {
        return true;
      }
    }
    return false;
  }
  
  public static List<String> getLines() { //method that reads input file line by line and returns list where each element is a line from the file
    List<String> lines = new ArrayList<String>();
    try {
      BufferedReader in = new BufferedReader(new FileReader("testBalloons.txt"));
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
  
  public static ArrayList<int[]> getVals() { //method that returns arraylist where each element is the list of the heights of each balloon for a given problem
    ArrayList<int[]> vals = new ArrayList<int[]>();
    ArrayList<String> lines = new ArrayList<String>(getLines());
    int size = lines.size();
    for (int i=2; i<size; i++) {
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
      String filename= "testBalloons_solution.txt";
      FileWriter fw = new FileWriter(filename,true);
      fw.write(Integer.toString(data)+"\n");
      fw.close();
    }
    catch(IOException ioe)
    {
      System.err.println("IOException: " + ioe.getMessage());
    }
  }
  
}