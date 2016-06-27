package deliverables;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import GraphStruct.Edge;
import GraphStruct.GraphInput;
import GraphStruct.SimpleGraph;
import GraphStruct.Vertex;

/*
 * Faster version of the algorithm using the gap relabelling heuristic and FIFO style node selection
 * runtime O(n^3);
 */
public class PreFlowPushImproved{

	private static SimpleGraph G;
	private static Hashtable<String, Vertex> vertices;
	private static boolean graphModified;
	private static int resultFlow;
	private static final String BIPARTITE = "/Users/prashantravi/Documents/workspace/TCSS543B/GraphGenerationCode/Bipartite/1000Size.txt";
	private static final String MY_TEST_G1 = "/Users/prashantravi/Documents/workspace/TCSS543B/GraphGenerationCode/MyTests/test1.txt";
	private static final String BIP_G1 = System.getProperty("user.dir") + "/GraphGenerationCode/Bipartite/g1.txt";

	private static Vertex sink;
	private static Vertex source;
	private int labelCount[];
	private int N;
	private static Queue<Vertex> workList;
	private static HashMap<Vertex, Boolean> active;
	
	/** Constructor for setting up the graph G, the queue, and some other initialization requirements.
	 * We maintain for each edge a back edge that has capacity 0 and flow 0 to begin with, and is parts
	 * of the residual out edges for a given vertex
	 * It is recommended to read through Vertex.java and Edge.java before reading the rest of this code.
	 * 
	 */

	public PreFlowPushImproved(String pathandfilename)
	{
		G = new SimpleGraph();
		vertices = GraphInput.LoadSimpleGraph(G, pathandfilename);
		graphModified = false;
		resultFlow = 0;
		source = vertices.get("s");
		sink = vertices.get("t");
		active = new HashMap<Vertex, Boolean>();
		workList = new LinkedList<Vertex>();
		/**
		 * set the size of label count to be maximum 2V+1 since we are guaranteed that
		 * the heights wont increase beyond 2V
		 */
		labelCount = new int[2*vertices.size()+1];
		N = vertices.size();
		/**
		 * Set all vertices to inactive at the beginning
		 * label count is the number of vertices that are at a specified height
		 * used for gap relabelling heurstic
		 * 
		 */
		for(Vertex v: vertices.values())
		{
			active.put(v, false);
			v.labelCount = labelCount;
		}
	}
	/**
	 * Add the vertex to the work queue if it hasn't been 
	 * activated in the past and has remaining excess that needs to be pushed out.
	 * 
	 */
	public void addToWorkList(Vertex v)
	{
		if(!active.get(v))
			if(v.excess >0 )
			{
				active.put(v, true);
				workList.add(v);
			}
	}
	/**
	 * Push delta flow on edge specified. Do appropriate operations on the back edge as well.
	 * if the to vertex is active and has positive excess then add it to the back of queue to be discharged FIFOly. 
	 * 
	 */
	public void Push(Edge e)
	{
		Vertex from = e.getFirstEndpoint();
		Vertex to = e.getSecondEndpoint();
		int delta = Math.min(from.excess,e.remaining());  
		e.flow += delta;
		e.getBackEdge().flow -= delta;
		to.excess += delta;    
		from.excess -= delta;
		addToWorkList(to);	
	}
	/**
	 * After relabeling a vertex, if the number of vertices at height k prior to relabeling turns to 0
	 * then we invoke the gap relabeling heuristic that sets the height of such vertices that have height > k and belong
	 * to the source side of the minimum cut to max(h[v], |V| +1); since we are declaring that 
	 * these are on the setA side of the minimum cut and the other vertices are on the setB side of the cut.
	 * With each gap relabel we get closer and closer to defining the minimum cut.
	 * 
	 */
	public void gapRelabel(int GAP)
	{
		for(Vertex v: vertices.values())
			if(v.getHeight() > GAP)
			{
				v.setHeight(Math.max(v.getHeight(), vertices.size() + 1));
				addToWorkList(v);
			}
	}
	/**
	 * relabel this vertex because there are no more admissible residual out edges
	 * that the excess at this vertex could be pushed out on
	 * essentially, increase the height of this vertex by the minimum height + 1 of
	 * the residual out edges that still have remaining capacity to push flow on.
	 * 
	 */
	public void relabel(Vertex v) {
		int minHeight = Integer.MAX_VALUE;
		for (Edge e: v.getResidualOutEdges()) 
			if(e.remaining() >0)
				minHeight = Math.min(minHeight, e.getSecondEndpoint().getHeight() + 1);
		//System.out.println("minheight " + minHeight);
		v.setHeight(minHeight);
		addToWorkList(v);
	}

	/**
	 * 
	 * @see deliverables.FlowAlgorithm#findMaxFlow()
	 * Main method that computes the maximum flow
	 * 
	 */
	public double findMaxFlow()
	{	
		if(graphModified)
			return resultFlow;
		int answer = 0;
		labelCount[0] = N;
		source.setHeight(N);
		active.put(source, true);
		active.put(sink, true);
		for (int i = 0; i < source.getResidualOutEdges().size(); i++) {
			source.excess += (double)source.getResidualOutEdges().get(i).getcapacity();
			Push(source.getResidualOutEdges().get(i));
		}
		Vertex v;

		while (!workList.isEmpty()) {
			active.put((v = workList.poll()), false);
			discharge(v);
		}

		answer = -source.computeExcess();
		graphModified = true;
		return (resultFlow = answer);
	}

/**
 * Runs as long as the vertex has excess
 * We are trying to push out as much flow as possible from the vertex
 * so that each vertex that is not the source and not the sink can 
 * have 0 excess.
 * Pseudocode follows from the CLRS textbook pg.683 under RELABEL_TO_FRONT algorithm
 * if after the relabelling, we no. vertices at orginialHeight turns to
 * then invoke the gap relabelling heuristic
 * 
 */
	public void discharge(Vertex vertex)
	{
		Edge edge = null;		
		while(vertex.excess >0)
		{
			if(vertex.getCurrentOutEdgeCounter() == vertex.getResidualOutEdges().size())
			{
				int originalHeight = vertex.getHeight();
				relabel(vertex);	
				if(labelCount[originalHeight] == 0)
					gapRelabel(originalHeight);
				vertex.resetCurrentOutEdgeCounter();
			}
			edge = vertex.getResidualOutEdges().get(vertex.getCurrentOutEdgeCounter());
			if(admissible(edge))
					Push(edge);

			else
				vertex.incrementCurrentOutEdgeCounter();
		}
		
		
	}
	/**
	 * Edge is admissible if the for edge= (u,v)
	 * u = v + 1, (i.e it is at a higher level than the destination reservoir by a height of 1)
	 * and the edge is in the residual graph.
	 * @return boolean
	 */
	public boolean admissible(Edge edge)
	{
		return edge.remaining() >0 && edge.getFirstEndpoint().getHeight() == edge.getSecondEndpoint().getHeight() + 1;
	}
	

	/**
	 * Check that apart from the source and the sink, no other node has excess
	 * and that the excess at source is the negation of the excess at the sink
	 * the absolute value of this excess at source is the maximum flow at the end 
	 * of the algorithm
	 * @return boolean
	 */
	public boolean validateExcessforNodes()
	{
		Vertex source = vertices.get("s");
		Vertex sink = vertices.get("t");
		for(Vertex v: vertices.values())
			if(v != sink && v != source)
				if(v.computeExcess() > 0.0 || v.computeExcess() < 0.0)
					return false;
		
		System.out.println(" Excess at source was " + source.computeExcess() + " Excess at sink was " + sink.computeExcess());
		return true & (-source.computeExcess()) == sink.computeExcess();
	}

	/**
	 * validates that the maxFlow is correct by checking height conditions,
	 * checking no more augmenting paths exist and checking that the source excess = -(sink excess)
	 * along with no other vertices have excess> 0. And finally, checking if the maxflow = mincut.
	 * @return boolean
	 */
	public boolean validateMaxFlow() {
		return validateHeights() & validateAugmentPathDoesntExist() &  validateExcessforNodes() & (findMaxFlow() == findMinCut());
	}

	/**
	 * Check that the preflow satisfies height constraints
	 * @return boolean
	 */
	public boolean validateHeights()
	{
		 for (Vertex src: vertices.values()) {
		      for (Edge e : src.getResidualOutEdges()) {
		        Vertex dest = G.opposite(src, e);
		        if (e.remaining() > 0 && src.getHeight() > dest.getHeight() + 1) 
		          return false;
		      }
		    }
		return true;
	}
	/**
	 * Once max flow is computed check that no augmenting path exists from the
	 * source to the sink, just as in the ford - fulkerson algorithm
	 * @return boolean
	 */
	public boolean validateAugmentPathDoesntExist()
	{
		   	HashMap<Vertex, Boolean> visited = new HashMap<Vertex, Boolean>();
		   	for(Vertex v: vertices.values())
		   		visited.put(v, false);
		   	
		    Queue<Vertex> queue = new ArrayDeque<Vertex>();
		    queue.add(source);
		    visited.put(source, true);
		    while (!queue.isEmpty()) {
		      Vertex vertex = queue.poll();
		      for (Edge e: vertex.getResidualOutEdges()) {
		        Vertex dest = G.opposite(vertex, e);
		        if (!visited.get(dest) && e.remaining() >0) {
		        	visited.put(dest, true);
		        	queue.add(dest);
		        }
		      }
		    }
		    if (visited.get(sink)) 
		      return false;
		    return true;
	}
	
	/**
	 * find the minimumCut after having found the maximum flow
	 * find an integer k, 0< k< |V|-1 that defines the setA as vertices with heights >k 
	 * and set B as vertices in V that are not is setA
	 * find the minimum cut by adding the capacities of outgoing edges in the original graph 
	 * that start in SetA and end in SetB
	 * returns the min cut found after identifying the gap.
	 * @return double
	 */
	public double findMinCut()
	{
		List<Vertex> setA  = new ArrayList<Vertex>();
		double answer = 0;
		int GAP = 0;
		for (int a = 1 ; a < vertices.size() ; a ++)
			if(labelCount[a] == 0)
			{
				GAP = a;
				break;
			}
		for(Vertex v: vertices.values())
			if(v.getHeight() > GAP)
				setA.add(v);
		for(Vertex v: vertices.values())
			if(v.getHeight() > GAP)
				for(Edge edge: v.getOutGoingEdges())
					if(!setA.contains(edge.getSecondEndpoint()))
						answer += (double) edge.getcapacity();

		return answer;
	}
	/**
	 * Main method used for initial testing.
	 * @param args
	 */
	public static void main(String args[])
	{
		double maxFlow = 0.0;
		PreFlowPushImproved preFlow2 = new PreFlowPushImproved(BIPARTITE);
		long startTime = System.currentTimeMillis();
		maxFlow = preFlow2.findMaxFlow();
		preFlow2.validateExcessforNodes();
		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
		System.out.println(maxFlow);
	}
}
