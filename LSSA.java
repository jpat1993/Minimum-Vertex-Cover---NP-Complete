/*
CSE6140 Project 
Jay Patel - 902836504

Local Search - Simulated Annealing


This class performs the local search with Simulated Annealing for the
Minimum Vertex Cover Problem, which is a NP Problem. This simAnnealing class
has the whole algorithm in it, along with other helper functions.

*/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;


/**
 * This class contains the simulated annealing algorithm for the local search.
 * 
 * @author Jay Patel
 *
 */

public class LSSA {

    // The files that will be the input.
    private static String currDir = System.getProperty("user.dir"); 
    private static String hcOutFilePath;
    private static String hcTraceFilePath;
    private static PrintWriter hcOut;
    private static PrintWriter hcTrace;
	private static boolean result; 



    // Main Method or testing.
    public static void main(String []args) throws FileNotFoundException, IOException {

        // Parameter Testing
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
        if (args[1].equals("-LS2")) {
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
            if(randSeedParam<0) {
                System.out.println("Random Seed must be positive integer or zero.");
            }
        } catch (Exception e) {
            System.out.println("Please enter valid randdom seed.");
            return;
        }
        String inputFile = inputFileParam;
        String method = methodParam;
        int cutOff = cutOffParam;
        int randSeed = randSeedParam;


        //~~~~~~~~~~~~~~~~~Start the simAnnealing~~~~~~~~~~~~~~~~~~~~~~//
        simAnneal(inputFile, method,cutOff,randSeed);


    //~~~~~~~~~~~~~~~~~~~~~~~END OF MAIN~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

    }


    /**
     * Simulted Annealing Begin: Takes the input graph and then simulates the algorithm
     *
     * @param inputFile  the file that is inputed
     * @param method  type of algo
     * @param cutOff  cut off time
     * @param randSeed  the seed that is randomized
     *
     */

    public static void simAnneal(String inputFile, String method, int cutOff, int randSeed) throws IOException {

        // Set up Output Files
        hcOutFilePath = currDir + "/output/" + inputFile + "_" + method + "_" + Integer.toString(cutOff) + "_" + Integer.toString(randSeed) + ".sol";
        hcTraceFilePath = currDir + "/output/" + inputFile + "_" + method + "_" + Integer.toString(cutOff) + "_" + Integer.toString(randSeed) + ".trace";

        hcOut = new PrintWriter(hcOutFilePath);
        hcTrace = new PrintWriter(hcTraceFilePath);


        // Perform the Simulated Annealing Algorithm
        ArrayList<String> vc = simulate(getGraph(inputFile), cutOff, randSeed);

        // Print Final Solution with the VC vertices
        hcOut.printf("%d%n",vc.size());

        for (int i = 0; i < vc.size(); i++) {

            hcOut.printf("%s",vc.get(i));

            if (i < vc.size()-1) {

                hcOut.printf(",");

            }

        }

        // Close the output files
        hcOut.close();
        hcTrace.close();
    }


    /**
     * Simulted Annealing of the Graph
     *
     * @param input  The hashmap of the graph
     * @return  result MVC of the graph
     *
     */

    public static ArrayList<String> simulate(HashMap<String, ArrayList<String>> graph, int cutoff, int randSeed){

        // Time Measure
        long start = System.currentTimeMillis();
        long endTime;
        long elapsed = 0;

        float elapsedFinal = 0;


        // Temperature Start and Cooling
        double temp = 10000.0;
        double cooling = 0.9999;
        double absTemp = 0.00001;


        // intitial solution: Using Max Degree Greedy Algorithm
        ArrayList<String> curr = maxDegGreedy(graph);

        // initializes the cost as the curr size;
        int currCost = curr.size();

        // counter to see how many iterations of loop
        int iter = 0;

        // init Random Generator
        Random randomno = new Random();
        randomno.setSeed(randSeed);

        // init nextSol and deltasize init
        ArrayList<String> nextSol = new ArrayList<String>();
        double deltaSize = 0.0;

        // static check to see if VC
        boolean[] check = new boolean[1];

        // static cost var passed in with vertex to measure cost
        int[] cost = new int[1];


        // Loop using Sim Annealing - temp and as long as time is below cutoff
        while (temp > absTemp && elapsedFinal < cutoff) {

            //Gets the next neighboring Solution
            nextSol = getNextSol(graph, curr, randomno, check, cost);


            // if cost == -1 then not VC, so only continue if real cost
            if(cost[0] != -1) {

                // Check the difference in costs of prev and curr VCs
                deltaSize = cost[0] - currCost;

                // If new cost is smaller then take it OR use Acceptance Function: Metropolis Condition
                if ((deltaSize < 0) || (currCost > 0 && Math.exp(-Math.abs(deltaSize) / temp) > randomno.nextDouble())) {
                    curr = nextSol;
                    currCost = cost[0];

                }


            }

            // Cool the Temperature
            temp *= cooling;

            iter++;

            // Time Measure
            endTime = System.currentTimeMillis();
            elapsed =  endTime - start;
            elapsedFinal = elapsed / 1000F;

            // If a Real VC then print it
            if(check[0]) {
                // System.out.println("iterations :" + iter + "  elapsed Time: " + elapsedFinal + "  curr size: " + curr.size() +
                //                 " TEMP : " + temp);

                hcTrace.printf("%.3f,%d%n", elapsedFinal, curr.size());
            }

            
            
        }


        //System.out.println("Total time:   "  + elapsedFinal + "  VC size : " + curr.size());

        // Return final VC
        return curr;
    }    


    /**
     * Gets the next Solution for the Simulated annealing by
     * picking a random vertex to remove and measure its
     * cost by counting number of adjacent vertices (degree).
     *
     * @param Graph  the graph HashMap
     * @param curr  the current solution
     * @param randomno  the random generator
     * @param check boolean array to input if VC or not
     * @param cost the cost variable to return
     * @return  ArrayList of another solution
     *
     */

    public static ArrayList<String> getNextSol(HashMap<String, ArrayList<String>> graph, ArrayList<String> curr, Random randomno, boolean[] check, int[] cost) {
        
        // Make a copy of the curr solution
        ArrayList<String> copy = new ArrayList<String>(curr);

        // Pick random vertex from curr solution to remove
        int index = randomno.nextInt(copy.size());
        String vertex = copy.get(index);


        // Make sure the graph is still a VC after removal
        for (int i = 0  ; i < graph.get(vertex).size(); i++) {

            // If the Solution does not contain the vertices that were adjacent to the one we removed
            if (!copy.contains(graph.get(vertex).get(i))) {

                result = false;
                break;
            }
        }

        // If the new one is still VC then remove that vertex
        if (result) { 
            copy.remove(vertex);

            // if good vertex then set the cost as the degree     
            cost[0] = graph.get(vertex).size();
            
        } else {
            // then ignore solution - don't accept
            cost[0] = -1;
        }


        // change the check to see if VC or not
        check[0] = result;
     
        // return new Solution
        return copy; 


    }


    /**
     * Reads the Graph file and then puts it into a HashMap
     *
     * @param file  The file name of the graph file
     * @return  HashMap of the graph
     * @throws FileNotFoundException, IOException
     *
     */

    public static HashMap<String, ArrayList<String>> getGraph(String file) throws FileNotFoundException, IOException{
        // open the file
        String filePath =  currDir + "/Data/" + file + ".graph"; // the input file path
        BufferedReader br = new BufferedReader(new FileReader(filePath));   // opens the file
        String line = br.readLine();                                    //first line |V| |E| 0
        String[] split = line.split(" ");

        // Init Output Hashmap and put the vertices in there
        HashMap<String,ArrayList<String>> output = new HashMap<String,ArrayList<String>>(); 
        output.put("0",  new ArrayList<String>(Arrays.asList(split))); // puts the V and E in 0
        int lineNum = 1;        

        // iterate through the graph
        while (lineNum <= Integer.parseInt(output.get("0").get(0))) {
            line = br.readLine();
            split = line.split(" ");

            // append the content in the current line to the output
            output.put(Integer.toString(lineNum),  new ArrayList<String>(Arrays.asList(split)));
            lineNum++; // continue to next line
        }
        output.remove("0");                     // remove 0 line with the V and E
        br.close();

        // return the HashMap of the graph
        return output;        

    }


    /**
     * Gets a list of the Edges from the given graph
     *
     * @param graph HashMap of the given graph
     * @return ArrayList of the Edges
     *
     */

    public static ArrayList<Edge> getEdges(HashMap<String,ArrayList<String>> graph) {

        // Init output arraylist
        ArrayList<Edge> output = new ArrayList<Edge>();

        // The current Vertex and the List of Vertices connected to it
        String currVertex; 
        ArrayList<String> listVertices; 

        // Temporary Edge
        Edge tempEdge;

        // Loop through the EntrySet of HashMap
        for (Map.Entry<String,ArrayList<String>> entry : graph.entrySet()) { 

            currVertex = entry.getKey();
            listVertices = entry.getValue();

            // Go through the list of vertices and create the edges
            for (int i = 0; i < listVertices.size(); i++) {

                if (!listVertices.get(i).equals("")) {

                    tempEdge = new Edge(currVertex, listVertices.get(i));

                    // Add to Arraylist of Edges
                    if (!output.contains(tempEdge)) {
                        output.add(tempEdge);
                    }
                }
            }
        }

        // Return the list of all the edges
        return output;
    } 



    /**
     * Gets an Initial Solution for Vertex Cover - Max Degree Greedy Algorithm
     * Takes the max degree vertex and adds it to the VC.
     *
     * @param graph The graph that we want to get a VC
     * @return ArrayList of the Vertex Cover
     *
     */

    public static ArrayList<String> maxDegGreedy(HashMap<String,ArrayList<String>> graph) {

        // measure initial time for VC
        long start = System.currentTimeMillis();
        long elapsed;
        float elapsedFinal = 0;

        // init output
        ArrayList<String> output = new ArrayList<String>();

        // make copy of graph
        HashMap<String,ArrayList<String>> copyGraph = new HashMap<String,ArrayList<String>>(graph);
        //get edges from graph
        ArrayList<Edge> edges = getEdges(copyGraph);

        // List of all Vertices sorted by degree (MAx -> Min);
        ArrayList<String> sortedVertices = sortVertices(graph);
        
        // start of get vertices
        int i = 0; 
        String tempVertex;

        // Loop through all the edges and put into VC
        while (!edges.isEmpty() && i < sortedVertices.size()) {

            // Get Vertex with Max Degree
            tempVertex = sortedVertices.get(i);

            // Add vertex to output
            output.add(tempVertex);

            // remove edges from Edge list
            removeEdgesFromList(edges, tempVertex);

            // remove vertex from graph
            copyGraph.remove(tempVertex);

            i++;

        }   

        // End Time Measure
        long endTime = System.currentTimeMillis();
        elapsed =  endTime - start;
        elapsedFinal = elapsed / 1000F;

        // Print out time
        // System.out.println("elapsed time: " + elapsedFinal);
        hcTrace.printf("%.3f,%d%n", elapsedFinal, output.size());

        // return output VC
        return output;

    } 

    /**
     * Sorts the Vertices
     *
     * @param graph HashMap of the given graph
     * @return Arraylist of Vertices Sorted 
     *
     */

    public static ArrayList<String> sortVertices(HashMap<String,ArrayList<String>> graph) {


        // Collections.sort =  O(n log n) algorithm that sorts the vertices by their degree
        ArrayList<String> vertices = new ArrayList<String>(graph.keySet());
        Collections.sort(vertices, new Comparator<String>() {
            public int compare(String a, String b) {
                return graph.get(b).size() - graph.get(a).size();
            }
        });

        // Return list of vertices in order from Max -> Min Degree
        return vertices;


    } 


    /**
     * Removes the Vertices from Edge List
     *
     * @param Edges list of all the edges
     * @param String of the Vertex to be delete all adj edges
     *
     */

    public static void removeEdgesFromList(ArrayList<Edge> edges, String vertex) {
        
        //iterate through edges and check if an edge contains the vertex
        ArrayList<Edge> copy = new ArrayList<Edge>(edges);
        for(Edge temp : copy) {
            if (temp.contains(vertex)) {
                edges.remove(temp);
            }
        }

    } 


    

}
