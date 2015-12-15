/**
 * This class is the class for object Edge
 * 
 * @author Yaxiong Liu
 *
 */

public class Edge {
		public String a, b; // two end points of an edge
		/**
		 * Constructor of an edge object
		 * @param a  One end of an edge
		 * @param b  The other end of an edge
		 */
		public Edge(String a, String b) {
			this.a=a;
			this.b=b;
		}
	
		/**
		 * Method to check if two edges all equal.
		 * Two edges are equal if they have the same end points regardless the order
		 * @param o  The edge to compare with
		 * @return  True if the given edge is the same as this edge, false otherwise
		 */
		@Override
		public boolean equals(Object o) {
		    boolean retVal = false;
		    if (o instanceof Edge){
		        Edge ptr = (Edge) o;
		        if ( (this.a.equals(ptr.b) && this.b.equals(ptr.a)) ||
		        	 (this.a.equals(ptr.a) && this.b.equals(ptr.b)) ) {retVal = true;}
		    }
		    return retVal;
		}
		/**
		 * Method to check if an edge has a given end point
		 * @param s  the end point
		 * @return  True if this edge has one end point is the given end point
		 */
		public boolean contains(String s) {
			return this.a.equals(s) || this.b.equals(s);
		}
		/**
		 * Method to print out an edge
		 */
		public void printEdge(){
			System.out.println("("+this.a+", "+this.b+")");
		}
	}