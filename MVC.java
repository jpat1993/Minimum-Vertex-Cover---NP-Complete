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
 * This class is the main class of Hill Climbing Local Search for MVC problem.
 * This class also contains various helper methods to support the main algorithm.
 * 
 * @author Yaxiong Liu
 *
 */
public class MVC {
	private static String currDir = System.getProperty("user.dir"); 
	private static String hcOutFilePath;
	private static String hcTraceFilePath;
	private static PrintWriter hcOut;
	private static PrintWriter hcTrace;
	private static int randSeed = 0;
	
	// Main method
	public static void main(String[] args) throws IOException {
		String inputFileParam, methodParam; // variables that store two argument
		int cutOffParam, randSeedParam;
		// read the first argument which indicates the input filename
		if ((args[1].equals("-LS1") || args[1].equals("-LS2")) && args.length != 4) {
			System.out.println("Please enter valid parameters.");
			return;
		}
		if ((args[1].equals("-Approx") || args[1].equals("-BnB")) && args.length != 3) {
			System.out.println("Please enter valid parameters.");
			return;
		}
		/**
		if (args[0].equals("-jazz.graph") || args[0].equals("-karate.graph") || args[0].equals("-football.graph") || args[0].equals("-as-22july06") ||
				args[0].equals("-hep-th.graph") || args[0].equals("-star.graph") || args[0].equals("-star2.graph") ||
				args[0].equals("-netscience.graph") || args[0].equals("-email.graph") || args[0].equals("-delaunay_n10.graph") || args[0].equals("-power.graph")) {
			inputFileParam = args[0].substring(1, args[0].length()-6);
		} else { // the the input filename is not valid, prompt and return
			System.out.println("Please enter valid file name.");
			return;
		}
		**/
		if(args[0].length()>0) {
			inputFileParam = args[0].substring(1, args[0].length()-6);
			String filePath =  System.getProperty("user.dir") + "/Data/" + inputFileParam + ".graph"; // the input file path
			try {
				BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
				br.close();
			} catch (Exception e) {
				System.out.println("Please enter valid file name");
				return;
			}
		} else {
			System.out.println("Please enter valid file name");
			return;
		}
		// read the second argument which indicates the algorithm
		if (args[1].equals("-LS1") || args[1].equals("-Approx") || args[1].equals("-LS2") || args[1].equals("-BnB")) {
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
		if(inputFileParam.equals("-LSHC") || inputFileParam.equals("-LSSA")){
			try {
				randSeedParam = Integer.parseInt(args[3].substring(1, args[3].length()));
				randSeed = randSeedParam;
				if(randSeedParam<0) {
					System.out.println("Random Seed must be positive integer or zero.");
				}
			} catch (Exception e) {
				System.out.println("Please enter valid randdom seed.");
				return;
			}
		}
		String inputFile = inputFileParam;
		String method = methodParam;
		int cutOff = cutOffParam;
		if (method.equals("LS1")){
			hcMVCmain(inputFile,method,cutOff,randSeed);
		} else if (method.equals("Approx")){
			APPR.dfsMVCmain(inputFile,method,cutOff);
		} else if (method.equals("LS2")){
			LSSA.simAnneal(inputFile,method,cutOff,randSeed);
		} else if (method.equals("BnB")){
			String BnBInputPath =  System.getProperty("user.dir") + "/Data/" + inputFile + ".graph"; // the input file path
			Utilities u = new Utilities();
    	    Graph g = u.parseGraph(BnBInputPath);
		    BranchAndBound bb = new BranchAndBound(g, cutOff, inputFile);
            bb.getVertexCover(g);
		    bb.writeSol();
		    bb.traceWriter.close();
		    bb.solWriter.close();
		}
		
		//ArrayList<String> vc = hcMVC(readGraph("power"),3600); // get MVC of the given file
		//System.out.println("The size of MVC is " + Integer.toString(vc.size())); // print out the numbder of the vertices in the MVC
	}// end main
	
	
	public static void hcMVCmain(String inputFile, String method, int cutOff, int randSeed) throws IOException {
		hcOutFilePath = currDir + "/output/" + inputFile + "_" + method + "_" + Integer.toString(cutOff) + "_" + Integer.toString(randSeed) + ".sol";
		hcTraceFilePath = currDir + "/output/" + inputFile + "_" + method + "_" + Integer.toString(cutOff) + "_" + Integer.toString(randSeed) + ".trace";
		hcOut = new PrintWriter(hcOutFilePath);
		hcTrace = new PrintWriter(hcTraceFilePath);
		ArrayList<String> vc = hcMVC(readGraph(inputFile),cutOff,randSeed);
		hcOut.printf("%d%n",vc.size());
		for (int i = 0; i < vc.size(); i++) {
			hcOut.printf("%s",vc.get(i));
			if (i<vc.size()-1) {
				hcOut.printf(",");
			}
		}
		hcOut.close();
		hcTrace.close();
	}
	
	/**
	 * The method of calculating MVC of the given graph
	 * @param graph  HashMap of the graph
	 * @return  The minimum vertex cover of the give graph
	 */
	public static ArrayList<String> hcMVC(HashMap<String,ArrayList<String>> graph, double cutoff, int randSeed){
		long startTime = System.currentTimeMillis();
		long elapsedmili;
		float elapsed = 0;
		ArrayList<Edge> edgeList = toEdges(graph); // Get the list of edges of the given graph
		ArrayList<String> vc = edgeDel(edgeList, randSeed); // get the initial vertex cover that is produced by Edge Deletion algorithm;
		ArrayList<String> vertexList = sortVertices(graph,vc); // Sort the vertices in the initial vertex cover
		boolean temp = false; // Boolean value indicates if there if a vertex is successfully deleted from the current vertex cover
		int i = 0;
		while (i<vertexList.size() && elapsed < cutoff) {
			temp = removeVertex(graph,vertexList,vertexList.get(i)); // Attempt to delete current vertex from the current vertex cover
			elapsedmili = System.currentTimeMillis() - startTime;
			elapsed = elapsedmili/1000F;
			if (!temp){ // if the vertex cannot be removed, then goto next vertex
				i++;
			} else {
				hcTrace.printf("%.3f,%d%n", elapsed, vertexList.size());
			}
		}
		return vertexList; // return the vertex cover
	} // end hcMVC
	
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
		result.put("0",  new ArrayList<String>(Arrays.asList(split))); // append the first line to the return
		int lineNum = 1; // next line we will read
		while (lineNum <= Integer.parseInt(result.get("0").get(0))) { // iterate all lines
			line = br.readLine();
			split=line.split(" ");
			//if (split.length<0) {
			//	System.out.println("Found empty");
			//}
			result.put(Integer.toString(lineNum),  new ArrayList<String>(Arrays.asList(split))); // append the content in the current line to the return
			lineNum++; // continue to next line
		}
		result.remove("0"); // remove the first line which indicates the number of vertices and edges
		br.close();
		//System.out.println("Reading the graph ... Complete");
		return result; // return the result
	} // end readGraph
	
	/**
	 * Attempt to remove a vertex from the current vertex cover
	 * @param edges  All edges that exist in the graph
	 * @param vertices  The current vertex cover
	 * @param vertex  The vertex that is attempted to be removed
	 * @return  True if the given vertex is removed from the vertex cover, false otherwise.
	 */
	public static boolean removeVertex(HashMap<String,ArrayList<String>> graph, ArrayList<String> vertices, String vertex) {
		boolean result = true; // initialize return
		//ArrayList<String> localVertices = new ArrayList<String>(vertices); // copy of the vertex cover
		//localVertices.remove(vertex); // remove the vertex from the copy of the vertex cover
		//result = isVC(edges,localVertices); // Check if the result is still a vertex cover
		for (int i=0; i<graph.get(vertex).size();i++){
			if (!vertices.contains(graph.get(vertex).get(i))){
				result = false;
				break;
			}
		}
		if (result) { // if it is vertex cover, then we remove that vertex from the given vertex cover
			vertices.remove(vertex);
			//System.out.println("Successfully removed a vertex from the current VC.");
			//System.out.println("The size of current VC is " + Integer.toString(vertices.size()));
		}
		return result; // return result
	} // end removeVertex
	
	/**
	 * Remove all edges that contain a given vertex from a list of edges.
	 * @param edges  The list of edges
	 * @param vertex  All of edges contain this vertex will be removed
	 */
	public static void removeVertexFromEdgesList(ArrayList<Edge> edges, String vertex) {
		ArrayList<Edge> copyOfEdges = new ArrayList<Edge>(edges); // get the copy of the edges list
		for(Edge e: copyOfEdges) { // iterate all edge
			if(e.contains(vertex)){ // if the edge contains the given vertex
				edges.remove(e); // then remove that edge
			}
		}
	}// end removeVertexFromEdgesList
	
	/**
	 * Sorted a list of vertices from smallest degree to the largest
	 * @param graph  The HashMap of the graph
	 * @param vertices  The list of vertices to be sorted
	 * @return  The sorted list of the vertices
	 */
	public static ArrayList<String> sortVertices(HashMap<String,ArrayList<String>> graph, ArrayList<String> vertices) {
		ArrayList<String> result = new ArrayList<String>(); // initialize the return
		ArrayList<String> localVertex = new ArrayList<String>(vertices); // get a copy of the vertices list
		String curr = localVertex.get(0); // get the first vertex as the vertex with the smallest degree
		while(!localVertex.isEmpty()){ // keep removing vertices from the copy of the vertices list, until it is empty
			curr = localVertex.get(0); // get the first vertex in the list
			for (int i=0;i<localVertex.size();i++){ // find the vertex with the smallest degree
				//System.out.println(localVertex.get(i) + " get");
				if(graph.get(localVertex.get(i)).size()<graph.get(curr).size()){
					curr = localVertex.get(i); 
				}
			}
			result.add(curr); // add the vertex to the return
			localVertex.remove(curr); // remove the vertex from the copy of vertices list, so we will not look at it again
		}
		//System.out.println("Soring the initial VC... Complete");
		return result; // return the result
	} // end sortedVertex
	
	/**
	 * Create a list of edges based on the given graph
	 * @param graph  HashMap of the give ngraph
	 * @return  The list of edges
	 */
	public static ArrayList<Edge> toEdges(HashMap<String,ArrayList<String>> graph) {
		ArrayList<Edge> result = new ArrayList<Edge>(); // initialize return
		String a; // current vertex
		ArrayList<String> b; // the list of vertices which connect to the current vertex
		Edge tempEdge;
		for (Map.Entry<String,ArrayList<String>> line : graph.entrySet()){ // iterate all vertices in the graph
			a = line.getKey();
			b = line.getValue();
			for (int i = 0;i<b.size();i++) { // store the edges if they are not in the edges list
				if(!b.get(i).equals("")){
					tempEdge = new Edge(a,b.get(i));
					if (!result.contains(tempEdge)) {
						result.add(tempEdge);
					}
				}
			}
		}
		//System.out.println("Convert the graph to a list of edges ... Complete");
		//for (int i=0; i<result.size();i++) {
		//	result.get(i).printEdge();
		//}
		return result; // return the result
	} // end toEdges
	/**
	 * Method of Edge Deletion
	 * @param edges  All edges in the graph
	 * @return  a VC
	 */
	public static ArrayList<String> edgeDel(ArrayList<Edge> edges, int start) {
		long startTime = System.currentTimeMillis();
		long elapsedmili;
		float elapsed = 0;
		ArrayList<String> result = new ArrayList<String>(); // initialize return
		ArrayList<Edge> localEdges = new ArrayList<Edge>(edges); // get a copy of the edges
		Edge tempEdge;
		int currPos = start%localEdges.size();
		while (!localEdges.isEmpty()) { // Add of the both ends of the current edge, and remove all edges that contain the ends
			tempEdge = localEdges.get(currPos);
			result.add(tempEdge.a); 
			removeVertexFromEdgesList(localEdges,tempEdge.a);
			result.add(tempEdge.b);
			removeVertexFromEdgesList(localEdges,tempEdge.b);
			if (currPos >= localEdges.size()) {
				currPos = 0;
			}
		}
		elapsedmili = System.currentTimeMillis() - startTime;
		elapsed = elapsedmili/1000F;
		hcTrace.printf("%.3f,%d%n", elapsed, result.size());
		return result; // return result
	} // end edgeDel
}
