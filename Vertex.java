import java.util.ArrayList;

/*
 * Vertex class for branch-and-bound
 * 
 * @author Matthew Berman
 * 
 */
public class Vertex {
	int num;
	int edgeCount;
	ArrayList<Vertex> adjacentVertices;
	
	/*
	 * Copy constructor for vertex
	 * 
	 * @param toCopy vertex to copy
	 */
	public Vertex(Vertex toCopy) {
		this.num = toCopy.num;
		this.edgeCount = toCopy.edgeCount;
		this.adjacentVertices = toCopy.adjacentVertices;
	}
	
	/*
	 * Constructor for vertex class
	 * 
	 * @param num vertex number
	 * @param adjacentVertices list of all neighbors
	 */
	public Vertex(int num, ArrayList<Vertex> adjacentVertices) {
		this.num = num;
		this.adjacentVertices = adjacentVertices;
		edgeCount = adjacentVertices.size();
		
	}
	
	/*
	 * Quick constructor for Vertex
	 * 
	 * @param num the vertex num
	 */
	public Vertex(int num) {
		this.num = num;
		edgeCount = -1;
	}
	
	/*
	 * Updates neighbors of a vertex. Used in parsing graph file
	 * 
	 * @param adjacentVertices new neighbors
	 */
	public void updateAdjacents(ArrayList<Vertex> adjacentVertices) {
		this.adjacentVertices = adjacentVertices;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if(o instanceof Vertex) {
			Vertex v = (Vertex) o;
			return (this.num == v.num);
		} else {
			return false;
		}
	}

}
