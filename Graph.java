import java.util.ArrayList;

/*
 * Graph class for branch-and-bound algorithm
 * 
 * @author Matthew Berman
 *
 */
public class Graph {
	int verticeCount;
	int edgeCount;
	
	ArrayList<BBEdge> edges;
	ArrayList<Vertex> vertices;
	
	ArrayList<Vertex> usedVertices;
	ArrayList<Vertex> unusedVertices;
	
	ArrayList<BBEdge> usedEdges;
	ArrayList<BBEdge> unusedEdges;
	
	/*
	 * Constructor for graph
	 * 
	 * @param v number of vertices
	 * @param e number of edges
	 * @param vertices array list containing every vertex
	 * @param edges array list containing every edge
	 */
	public Graph(int v, int e, ArrayList<Vertex> vertices, ArrayList<BBEdge> edges) {
		verticeCount = v;
		edgeCount = e;
		this.vertices = vertices;
		this.edges = edges;
		
		usedVertices = new ArrayList<Vertex>();
		unusedVertices = new ArrayList<Vertex>();
		for (Vertex i: vertices) {
			unusedVertices.add(new Vertex(i));
		}
		
		usedEdges = new ArrayList<BBEdge>();
		unusedEdges = edges;
	}
	
	/*
	 * Copy constructor for graph
	 * 
	 * @param g the graph to copy
	 */
	public Graph(Graph g) {
		this.verticeCount = g.verticeCount;
		this.edgeCount = g.edgeCount;
		
		this.unusedEdges = new ArrayList<BBEdge>();
		this.usedEdges = new ArrayList<BBEdge>();
		
		this.vertices = new ArrayList<Vertex>();
		this.edges = new ArrayList<BBEdge>();
		this.usedVertices = new ArrayList<Vertex>();
		this.unusedVertices = new ArrayList<Vertex>();
		
		for (Vertex i: g.vertices)
			this.vertices.add(new Vertex(i));
		for (Vertex v: g.usedVertices)
			this.usedVertices.add(new Vertex(v));
		for (Vertex u: g.unusedVertices)
			this.unusedVertices.add(new Vertex(u));
		
		for (BBEdge e: g.edges)
			this.edges.add(new BBEdge(e));
		for (BBEdge e: g.usedEdges)
			this.usedEdges.add(new BBEdge(e));
		for (BBEdge e: g.unusedEdges)
			this.unusedEdges.add(new BBEdge(e));
	}
	

	/*
	 * Returns the Vertex with the highest degree of uncovered neighbors)
	 * 
	 * @return vertex with most uncovered neighbors
	 */
	public Vertex getHighestDegree(ArrayList<Vertex> covered) {
		Vertex output = null;
		int maxDegree = 0;
		for (Vertex v: unusedVertices) {
			ArrayList<Vertex> temp = new ArrayList<Vertex>();
			for (Vertex i: v.adjacentVertices) {
				temp.add(new Vertex(i));
			}
			temp.removeAll(covered);
			
			if (temp.size() > maxDegree) {
				output = new Vertex(v);
				output.adjacentVertices = temp;
				maxDegree = temp.size();
			}
		}
		if (output== null) {
			return null;
		} else {
			return output;
		}
	}
	
	/*
	 * Switches a vertex from unused to used
	 * Updates all relevant data structures
	 * 
	 * @return the vertex switched, or null if unsuccessful
	 */
	public Vertex switchVertex(Vertex v) {
		Vertex toSwitch = null;
		for (Vertex i: unusedVertices) {
			if (v.equals(i)) {
				toSwitch = i;
			}
		}
		if (toSwitch == null) {
			return null;
		}
		this.usedVertices.add(toSwitch);
		this.unusedVertices.remove(toSwitch);
		ArrayList<BBEdge> switchedEdges = new ArrayList<BBEdge>();
		for (BBEdge e: this.unusedEdges) {
			if (e.contains(toSwitch)) {
				switchedEdges.add(e);
			}
		}
		unusedEdges.removeAll(switchedEdges);
		usedEdges.addAll(switchedEdges);
		return v;
	}
	
	/*
	 * Switches a vertex from used to unused
	 * 
	 * @return the vertex switched, or null if unsuccessful
	 */
	public Vertex switchBack(Vertex v, ArrayList<Vertex> covered) {
		Vertex toSwitch = null;
		for (Vertex i: usedVertices) {
			if (v.equals(i)) {
				toSwitch = i;
			}
		}
		if (toSwitch == null) {
			return null;
		}
		this.unusedVertices.add(toSwitch);
		this.usedVertices.remove(toSwitch);
		ArrayList<BBEdge> switchedEdges = new ArrayList<BBEdge>();
		for (BBEdge e: this.usedEdges) {
			if (e.contains(toSwitch)) {
				for (Vertex a: covered) {
					if (!a.equals(v) && (!e.contains(a))) {
						switchedEdges.add(e);
					}
				}
			}
		}
		usedEdges.removeAll(switchedEdges);
		unusedEdges.addAll(switchedEdges);
		return v;
	}
}
