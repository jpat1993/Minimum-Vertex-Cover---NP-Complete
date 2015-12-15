/*
 *Edge class for branch-and-bound algorithm
 *
 * @author Matthew Berman
 * 
 */
public class BBEdge {
	Vertex v1;
	Vertex v2;
	
	/*
	 * Constructor for edge
	 * 
	 * @param v1 first vertex covered by edge
	 * @param v2 second vertex covered by edge
	 */
	public BBEdge(Vertex v1, Vertex v2) {
		this.v1 = v1;
		this.v2 = v2;
	}
	
	/*
	 * Copy constructor for edge
	 * 
	 * @param e the edge to copy
	 */
	public BBEdge(BBEdge e) {
		this.v1 = new Vertex(e.v1);
		this.v2 = new Vertex(e.v2);
	}
	
	/*
	 * Method checks if this edge has vertex i as one of its endpoints
	 * 
	 * @param i the int representing the vertex to check
	 * @return true if i is one of the endpoints of the edge
	 */
	public boolean contains(int i) {
		return((v1.num == i) || (v2.num == i));
	}
	
	/*
	 * Method checks if this edge has vertex i as one of its endpoints
	 * 
	 * @param v the vertex to check
	 * @return true if i is one of the endpoints of the edge
	 */
	public boolean contains(Vertex v) {
		return((v.equals(v1)) || (v.equals(v2)));
	}
	

	public boolean equals(Object o) {
		if (o instanceof BBEdge) {
			BBEdge e = (BBEdge) o;
			return ((this.v1.num == e.v1.num && e.v2.num == this.v2.num) || 
					(this.v1.num == e.v2.num && this.v2.num == e.v1.num));
		} else {
			return false;
		}
	}
	
	//For debugging purposes
	public String toString() {
		return ("Includes " + v1.num + " and " + v2.num);
	}

}
