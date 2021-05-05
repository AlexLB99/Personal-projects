//Alex Le Blanc
//260803654
//No collaborators
import java.util.*;
import java.io.*;
import java.util.regex.*;
import java.math.*;
import static java.lang.System.out;

public class islands {
  
  public static void main(String[] args) {
    //long start = System.currentTimeMillis();
    ArrayList<String> lines = new ArrayList<String>(getLines());
    ArrayList<String[][]> allMaps = new ArrayList<String[][]>(getVals());
    for(int i=0; i<allMaps.size(); i++) {
      outputData(compress(allMaps.get(i)));
    }
    //long time = System.currentTimeMillis() - start;
    //System.out.println(time);
  }
  
  public static int compress(String[][] map) { //method that iterates through the map and counts every island it encounters, while "infecting" them to not count islands twice
    int count=0;
    for (int i=0; i<map.length; i++) {
      for (int j=0; j<map[i].length; j++) {
        if (map[i][j].equals("-")) {
          count++;
          map = infect(map, i, j);
        }
      }
    }
    return count;
  }
  
  public static String[][] infect(String[][] map, int i, int j) { //method that recursively spreads to all adjacent "-" elements and changes them to "@"
    map[i][j] = "@";
    if (i>0 && map[i-1][j].equals("-")) {
      infect(map, i-1, j);
    }
    if (i<map.length-1 && map[i+1][j].equals("-")) {
      infect(map, i+1, j);
    }
    if (j>0 && map[i][j-1].equals("-")) {
      infect(map, i, j-1);
    }
    if (j<map[i].length-1 && map[i][j+1].equals("-")) {
      infect(map, i, j+1);
    }
    return map;
  }
  
  public static List<String> getLines() { //method that reads input file line by line and returns list where each element is a line from the file
    List<String> lines = new ArrayList<String>();
    try {
      BufferedReader in = new BufferedReader(new FileReader("testIslands.txt"));
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
  
  public static ArrayList<String[][]> getVals() { //method that returns arraylist where each element is the map for the given problem
    ArrayList<String[][]> vals = new ArrayList<String[][]>();
    ArrayList<String> lines = new ArrayList<String>(getLines());
    int size = Integer.parseInt(lines.get(0));
    int dataLine=1;
    for (int i=0; i<size; i++) {
      String[] myData = lines.get(dataLine).split("\\s+");
      String[][] map = new String[Integer.parseInt(myData[0])][Integer.parseInt(myData[1])];
      for (int x=(dataLine+1); x<=(dataLine+(Integer.parseInt(myData[0]))); x++) {
        String[] myLine = lines.get(x).split("(?!^)");
        for (int y=0; y<(Integer.parseInt(myData[1])); y++) {
          map[x-dataLine-1][y] = myLine[y];
        }
      }
      dataLine = dataLine+(Integer.parseInt(myData[0]))+1;
      vals.add(map);
    }
    return vals;
  }
  
  public static void outputData(int data) { //takes an integer and appends it to specified file
    try
    {
      String filename= "testIslands_solution.txt";
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