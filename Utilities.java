import java.io.File;
import java.util.*;

/*
 * Utilities class for branch-and-bound. Contains method to read a file for
 * a graph and two methods for computing the 2-approx bound
 * 
 * @author Matthew Berman
 * 
 */
public class Utilities {
	
	/*
	 * Empty constructor
	 */
	public Utilities() {
		
	}
	
	/*
	 * Parses a graph in Metis I/O format
	 * 
	 * @filename the name of the file to read
	 * @return the graph defined by the file
	 */
	public Graph parseGraph(String filename) {
		Graph output = null;
		
		try {
			Scanner fileReader = new Scanner(new File(filename));
			String infoLine = fileReader.nextLine();
			String[] graphInfo = infoLine.split(" ");
			int vertices = Integer.parseInt(graphInfo[0]);
			int edges = Integer.parseInt(graphInfo[1]);
			ArrayList<Vertex> vertexList = new ArrayList<Vertex>(vertices);
			
			ArrayList<BBEdge> edgeSet = new ArrayList<BBEdge>(edges * 2);


			for (int i = 1; i <= vertices; i++) {
				vertexList.add(new Vertex(i));
			}
			for (int j = 1; j <= vertices; j++) {
				
				String iLine = fileReader.nextLine();
				String[] iLineEdges = iLine.split(" ");
				ArrayList<Vertex> destinations = new ArrayList<Vertex>(iLineEdges.length);
				for (int k = 0; k < iLineEdges.length; k++) {
					if (!(iLineEdges[k].equals(""))) {
						destinations.add(vertexList.get((Integer.parseInt(iLineEdges[k]) - 1)));
						int v2 = Integer.parseInt(iLineEdges[k]) - 1;
						edgeSet.add(new BBEdge(vertexList.get(j - 1), vertexList.get(v2)));
					}
			
				}
				Vertex v = vertexList.get(j - 1);
				v.updateAdjacents(destinations);
				vertexList.set(j - 1, v);
				
			}
			output = new Graph(vertices, edges, vertexList, edgeSet);
			
			fileReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}
	
	
	/*
	 * Computes the 2-approx of the graph g
	 * 
	 * @param g the graph to compute 2-approx of
	 * @return int value of the 2-approx
	 */
	public int lowerBoundApprox(Graph g) {
		ArrayList<Vertex> output = new ArrayList<Vertex>();
		ArrayList<BBEdge> unused = new ArrayList<BBEdge>();
		for (BBEdge a: g.unusedEdges) {
			unused.add(a);
		}
		while (unused.size() != 0) {
			BBEdge toRemove = unused.get(0);
			Vertex toAdd = toRemove.v1;
			Vertex addTo = toRemove.v2;
			output.add(toAdd);
			output.add(addTo);
			ArrayList<BBEdge> edgesToRemove = new ArrayList<BBEdge>();
			for (BBEdge e: unused) {
				if ((e.contains(toAdd)) || (e.contains(addTo))) {
					edgesToRemove.add(e);
				}
			}
			unused.removeAll(edgesToRemove);
		}
		return output.size();
		
	}
		
}
