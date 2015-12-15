import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;

/*
 * Contains the method to get mvc using branch and bound
 * 
 * @author Matthew Berman
 */
public class BranchAndBound {
	int lower;
	
	int cutoff;
	long elapsed;
	long start;

	String traceName;
	String solName;
	FileWriter traceWriter;
	FileWriter solWriter;
	Utilities u;
	ArrayList<Vertex> candidateList;

	boolean solution = false;
	
	/*
	 * Constructor for branch and bound
	 * 
	 * @param original - graph to get MVC of
	 * @param cutoff time
	 * @param filename for creating trace and sol files
	 */
	public BranchAndBound(Graph original, int cutoff, String filename) throws IOException {
		candidateList = new ArrayList<Vertex>();
		u = new Utilities();
		lower = u.lowerBoundApprox(original);
		
		this.cutoff = (cutoff * 1000);
		start = System.currentTimeMillis();
		//String[] split = filename.split("\\.");
		//String name = split[0];
		
		//traceName = name + "_BnB_" + cutoff +".trace";
		//solName = name + "_BnB_" + cutoff +".sol";
		
		solName =  System.getProperty("user.dir") + "/output/" + filename + "_BnB_" + Integer.toString(cutoff) + ".sol";
		traceName =  System.getProperty("user.dir") + "/output/" + filename + "_BnB_" + Integer.toString(cutoff) + ".trace";
		
		traceWriter = new FileWriter(traceName);
		solWriter = new FileWriter(solName);
		
	}
	
	/*
	 * Checks if the time cutoff has been reached
	 * 
	 * @return true if cutoff reached, false if not
	 */
	public boolean isCutoff() {
		return (elapsed >= cutoff);
	}
	
	/*
	 * Gets the MVC of the given graph, stops upon cutoff or finding best solution
	 */
	public void getVertexCover(Graph g) throws IOException {
		elapsed = System.currentTimeMillis() - start;
		if (isCutoff()) {
			writeSol();
			traceWriter.close();
			solWriter.close();
			System.exit(0);
		}

		if (g.usedVertices.size() >= lower) {
			return;
		}
		
		if (g.unusedEdges.size() == 0) {
			candidateList = g.usedVertices;
			lower = candidateList.size();
			solution = true;
			System.out.println("vertex cover found - size " + lower);
			writeTrace();
			return;
		}
		
		Vertex v = g.getHighestDegree(g.usedVertices);
		Graph left = new Graph(g);
		Graph right = new Graph(g);
		left.switchVertex(v);
		for (Vertex z: v.adjacentVertices) {
			right.switchVertex(z);	
		}
		int rightVal = right.usedVertices.size() + (u.lowerBoundApprox(right));
		int leftVal =  left.usedVertices.size() + (u.lowerBoundApprox(left));
		if ((right.usedVertices.size() + (u.lowerBoundApprox(right) ) <= lower ) 
		&& (left.usedVertices.size() + (u.lowerBoundApprox(left))  <=  lower )) {
			if (leftVal < rightVal) {
				getVertexCover(left);
				getVertexCover(right);
				return;
			} else {
				getVertexCover(right);
				getVertexCover(left);
				return;
			}
		} else if ((left.usedVertices.size() + (u.lowerBoundApprox(left)) / 2 <= lower)) {
			getVertexCover(left);
			return;
		} else if((right.usedVertices.size() + (u.lowerBoundApprox(right)) / 2 <= lower)) {
			getVertexCover(right);
			return;
		} else {
			return;
		}
					
	}
		
	
	/*
	 * Writes the best solution found to the sol file
	 */
	public void writeSol() throws IOException {
		String solSize = Integer.toString(lower);
		solWriter.append(solSize + System.getProperty("line.separator"));
		String verticesIncluded = "";
		for (int i = 0; i < candidateList.size() - 1; i++) {
			verticesIncluded += Integer.toString(candidateList.get(i).num) + ", ";
	
		}
		verticesIncluded += Integer.toString(candidateList.get(candidateList.size() - 1).num);
		solWriter.append(verticesIncluded);
	}
	
	/*
	 * Writes to the trace file every time a new vertex cover is found
	 */
	public void writeTrace() {
		long current = System.currentTimeMillis();
		double time = (current - start) / 1000.0;
		String toAdd = Double.toString(time) + ", " + Integer.toString(lower);
		System.out.println(toAdd);
		try {
			traceWriter.append(toAdd + System.getProperty("line.separator"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}