import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import GraphStruct.Edge;
import GraphStruct.GraphInput;
import GraphStruct.SimpleGraph;
import GraphStruct.Vertex;

public class FordFulkerson{

	private static SimpleGraph G;
	private static Hashtable<String, Vertex> vertices;
	private static boolean graphModified;
	private static double resultFlow;
	/**
	 * Constructor for FordFulkerson that takes a filename and constructs Graph G
	 * @param pathandfilename
	 */
	public FordFulkerson(String pathandfilename)
	{
		G = new SimpleGraph();
        vertices = GraphInput.LoadSimpleGraph(G, pathandfilename);
        graphModified = false;
        resultFlow = 0.0d;        	
	}
	/**
	 * Command line alternative to FordFulkerson that takes filename in prompt
	 */
	public FordFulkerson()
	{
		G = new SimpleGraph();
		vertices = GraphInput.LoadSimpleGraph(G);
		graphModified = false;
		resultFlow = 0.0d;
	}
	
	/**
	 * Do a BFS search to find the augmenting path
	 * This is the Edmond-Karps flavor of the algorithm that set the upper bound of run time as O((V^2)E)
	 * Returns the set of eeges in the augmenting path that was found.
	 * @return List<Edege> 
	 */
	private List<Edge> foundAugmentingPath()
	{
		Vertex source = vertices.get("s");
		Vertex sink = vertices.get("t");
		HashMap<Vertex, Boolean> visited = new HashMap<Vertex, Boolean>();
		HashMap<Vertex,Edge> path = new HashMap<Vertex, Edge>();
		Queue<Vertex> queue = new LinkedList<Vertex>();
		queue.add(source);
		visited.put(source, true);
		Vertex temp; 
		Vertex adjacent;
		while(!queue.isEmpty() && !visited.containsKey(sink))
		{
			
			temp = queue.poll();
			
			Iterator<Edge> j;
			Edge e;
	        for (j = G.incidentEdges(temp); j.hasNext();) 
	        {
	        	e = (Edge) j.next();
	        	if((adjacent = G.opposite(temp, e))!= null)
	            	if(!visited.containsKey(adjacent))
	            		if(e.residualCapacityToVertex(adjacent) > 0.0 )
	            			{
	            				queue.add(adjacent);
	            				path.put(adjacent, e);	
	            				visited.put(adjacent, true);
	            			}
	        }
	        
	        
		}
		if(visited.containsKey(sink))
		{
			  List<Edge> edgePath = new ArrayList<Edge>();
			  temp = sink;
			  while(temp != source )
		      { 
		    	  edgePath.add(path.get(temp));  
		    	  temp = G.opposite(temp, path.get(temp));
		      }
			  return edgePath;
		}
		return null;
	}
	/**
	 * Do one more BFS to find the minimum cut 
	 * All vertices reachable from s are in the SetA
	 * All vertices not in Set A are in SetB
	 * Then the minimum cut is the sum of capacities of edges exiting SetA and entering Set B
	 * Returns the minimum cut capcity after having executed the maxFlow algorithm executed on the residual graph.
	 * @return double
	 */
	public double findMinCutCapacity()
	{	
		if(!graphModified)
			findMaxFlow();
		Vertex source = vertices.get("s");
		Vertex adjacent;
		Vertex temp;
		HashMap<Vertex, Boolean> visited = new HashMap<Vertex, Boolean>();
		Queue<Vertex> queue = new LinkedList<Vertex>();
		queue.add(source);
		visited.put(source, true);
		double minCutCapacity = 0.0d;
		List<Vertex> setA = new ArrayList<Vertex>();
		setA.add(source);
		while(!queue.isEmpty())
		{
			temp = queue.poll();
			Iterator<Edge> j;
			Edge e;
	        for (j = G.incidentEdges(temp); j.hasNext();) 
	        {
	        	e = (Edge) j.next();
	        	if((adjacent = G.opposite(temp, e))!= null)
	            	if(!visited.containsKey(adjacent))
	            		if(e.residualCapacityToVertex(adjacent) > 0.0 )
	            			{
	            				setA.add(adjacent);
	            				visited.put(adjacent, true);
	            				queue.add(adjacent);
	            			}

	        }
		
		}
		List<Edge> minCutOutEdges = new ArrayList<Edge>();
		for(Vertex a: setA)
		{
			List<Edge> outEdges = a.getOutGoingEdges();
			for(Edge e : outEdges)
				if(!setA.contains(e.getSecondEndpoint()))
					minCutOutEdges.add(e);
		}
		for(Edge e : minCutOutEdges)
		{
			minCutCapacity+= (double)e.getcapacity();
		}
		return minCutCapacity;
	}
	/**
	 * Method that runs the max flow routine.
	 * Returns the maximum flow of this network.
	 * @return double.
	 */
	public double findMaxFlow()
	{	
		if(graphModified)
			return resultFlow;
		double maxflow = 0.0d ;
		if(!vertices.containsKey("s") || !vertices.containsKey("t"))
		{
			System.out.println("Either sink or source is not defined in vertex set");
			System.exit(-1);
		}
		Vertex sink = vertices.get("t");
		List<Edge> path;
		while((path = foundAugmentingPath())!=null)
		{
			Iterator<Edge> j;
			// Find bottleneck
			Double bottleneck = Double.MAX_VALUE;
			Vertex toVertex = sink;
			for(j = path.iterator(); j.hasNext();)
			{
				Edge e = j.next();
				bottleneck = Math.min(bottleneck, e.residualCapacityToVertex(toVertex));
				toVertex = G.opposite(toVertex,e );
			}
			
			// Augment the path
			toVertex = sink;
			for(j = path.iterator(); j.hasNext();)
			{
				Edge e = j.next();
				e.updateEdgeFlow(toVertex, bottleneck);
				toVertex = G.opposite(toVertex, e);
			}
			maxflow += bottleneck;
		}
		graphModified = true;
		resultFlow = maxflow;
		return maxflow;
		//
	}
	
}

