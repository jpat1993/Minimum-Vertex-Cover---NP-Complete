import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is the main class of approximation algorithm for MVC problem, which is
 * derived from the DFS search. This algorithm is guaranteed to achieve a 2-approximation.
 * Thanks to Yaxiong for help on graph read and main methods.
 * 
 * @author Tianxiao Liu
 *
 */

public class APPR {
	
	static int numVertices;
	static int vcsize = 0;
	static int[] vertexCover;
	private static String currDir = System.getProperty("user.dir"); 
	private static String outFilePath;
	private static String traceFilePath;
	private static PrintWriter dfsOut;
	private static PrintWriter dfsTrace;

	public static void main(String[] args) throws IOException {
		String inputFileParam, methodParam; // variables that store two argument
		int cutOffParam, randSeedParam;
		// read the first argument which indicates the input filename
		if (args.length != 4) {
			System.out.println("Please enter valid parameters.");
			return;
		}
		
		if (args[0].equals("-jazz.graph") || args[0].equals("-karate.graph") || args[0].equals("-football.graph") || args[0].equals("-as-22july06.graph") ||
				args[0].equals("-hep-th.graph") || args[0].equals("-star.graph") || args[0].equals("-star2.graph") ||
				args[0].equals("-netscience.graph") || args[0].equals("-email.graph") || args[0].equals("-delaunay_n10.graph") || args[0].equals("-power.graph")) {
			inputFileParam = args[0].substring(1, args[0].length()-6);
		} else { // the the input filename is not valid, prompt and return
			System.out.println("Please enter valid file name.");
			return;
		}
		// read the second argument which indicates the algorithm
		if (args[1].equals("-Approx")) {
			methodParam = args[1].substring(1, args[1].length()); 
		} else { // the the input filename is not valid, prompt and return
			System.out.println("Please enter valid algorithm name.");
			return;
		}
		try {
			cutOffParam = Integer.parseInt(args[2].substring(1, args[2].length()));
			if(cutOffParam<=0) {
				System.out.println("Cutoff must be positive integer.");
			}
		} catch (Exception e) {
			System.out.println("Please enter valid cutoff.");
			return;
		}
		try {
			randSeedParam = Integer.parseInt(args[3].substring(1, args[3].length()));
			if(randSeedParam!=0) {
				System.out.println("No Random Seed needed here. Please set it to 0.");
			}
		} catch (Exception e) {
			System.out.println("Please enter valid randdom seed.");
			return;
		}
		String inputFile = inputFileParam;
		String method = methodParam;
		int cutOff = cutOffParam;
		dfsMVCmain(inputFile,method,cutOff);
		//dfsvc(readGraph("power")); // get MVC of the given file
		//System.out.println("The size of VC is " + vcsize); // print out the numbder of the vertices in the MVC
	}

	public static void dfsMVCmain(String inputFile, String method, int cutOff) throws IOException {
		outFilePath = currDir + "/output/" + inputFile + "_" + method + "_" + Integer.toString(cutOff) + ".sol";
		traceFilePath = currDir + "/output/" + inputFile + "_" + method + "_" + Integer.toString(cutOff) + ".trace";
		dfsOut = new PrintWriter(outFilePath);
		dfsTrace = new PrintWriter(traceFilePath);
		double runningTime = dfsvc(readGraph(inputFile));
		if (runningTime > cutOff) {
			dfsTrace.printf("Could not produce a result within given cutoff time.");
			dfsOut.printf("Could not produce a result within given cutoff time.");
		} else {
			dfsOut.printf("%d%n",vcsize);
			for (int i = 1; i < vcsize; i++) {
				dfsOut.printf("%s",vertexCover[i]);
			    if (i<vcsize-1) {
			    	dfsOut.printf(",");
			    }
		    }
		    dfsTrace.printf("%s,%s", runningTime, vcsize);
		}
	
		dfsOut.close();
		dfsTrace.close();
	}
	

	public static double dfsvc(HashMap<String,ArrayList<String>> graph) {
		boolean adj[][] = toMatrix(graph);
		boolean visi[] = new boolean[numVertices+1];
		for (int i = 1; i< numVertices; i++) {
			visi[i] = false;
		}

		long startTime = System.nanoTime();
		// since the graph could be unconnected, need to traverse all the vertices
		for (int i = 1; i< numVertices; i++) {
			dfs(i, visi, adj);
		}

		double endTime = (System.nanoTime() - startTime)/1000000000.0;
		return endTime;
		//System.out.println("Running time is " + Double.toString(endTime));
	}

	public static void dfs(int i, boolean[] visited, boolean[][] adj) {
		int j;
		int counter = 1;
		visited[i] = true;
		boolean leaf = true;
		// if in this row, i has no unvisited neighbors, then i is a leaf.
		while ((counter < numVertices + 1)&&(leaf)) {
			if (adj[i][counter] && !visited[counter]) {
				leaf = false;
			}
			counter++;
		}
		// if i is not a leaf, add it to the vertex cover set and
		// increment the vertex cover size.
		if (!leaf) {
			vcsize++;
			vertexCover[vcsize]=i;
		}
		for (j = 0; j < numVertices + 1; j++ ) {
			if (adj[i][j] && !visited[j]) {
				dfs(j, visited, adj); 
			}
		}
	}

	public static boolean[][] toMatrix(HashMap<String,ArrayList<String>> graph) {
		boolean[][] adjMatrix = new boolean[numVertices+1][numVertices+1];
		for (int i = 0; i< numVertices; i++) {
			for (int j = 0; j< numVertices; j++) {
				adjMatrix[i][j] = false;
			}
		}
		String a; // current vertex
		int aint;
		ArrayList<String> b; // the list of vertices which connect to the current vertex
		int currV;
		for (Map.Entry<String,ArrayList<String>> line : graph.entrySet()){ // iterate all vertices in the graph
			a = line.getKey();
			aint = Integer.parseInt(a);
			b = line.getValue();
			for (int i = 0;i<b.size();i++) { // store the edges if they are not in the edges list
				if(!b.get(i).equals("")){
					currV = Integer.parseInt(b.get(i));
					adjMatrix[aint][currV] = true;
				}
			}
		}
		// adjMatrix[i][j] is marked true if edge (i, j) exisits.
		return adjMatrix; // return the result
	} 



	/**
	 * Method that reads the graph file
	 * @param filename  The file name of the graph file
	 * @return  HashMap of the graph
	 * @throws IOException
	 */
	public static HashMap<String,ArrayList<String>> readGraph(String filename) throws IOException {
		HashMap<String,ArrayList<String>> result = new HashMap<String,ArrayList<String>>(); // initialize return
		String[] split; 
		String currDir = System.getProperty("user.dir"); // get the current directory
		String filePath =  currDir + "/Data/" + filename + ".graph"; // the input file path
		BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
		
		String line = br.readLine();// get the first line in the file
		split = line.split(" ");

		numVertices = Integer.parseInt(split[0]);
		vertexCover = new int[numVertices];

		result.put("0",  new ArrayList<String>(Arrays.asList(split))); // append the first line to the return
		int lineNum = 1; // next line we will read
		while (lineNum <= Integer.parseInt(result.get("0").get(0))) { // iterate all lines
			line = br.readLine();
			
			split=line.split(" ");

			
			result.put(Integer.toString(lineNum),  new ArrayList<String>(Arrays.asList(split))); // append the content in the current line to the return	
			lineNum++; // continue to next line
		}
		result.remove("0"); // remove the first line which indicates the number of vertices and edges
		br.close();
		//System.out.println("Reading the graph ... Complete");
		return result; // return the result
	} // end readGraph
}