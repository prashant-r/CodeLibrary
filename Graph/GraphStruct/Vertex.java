package GraphStruct;
import java.util.*;

public class Vertex {
    /** the edge list for this vertex */
    LinkedList<Edge> incidentEdgeList;
	List<Edge> residualEdges;
    private Object data;              // an object associated with this vertex
    private String name;              // a name associated with this vertex
    private int height;
    public int excess;
    private int currentResidualOutEdgeCounter;
    private boolean activated;
    public int labelCount [];
    
	/**
     * Constructor that allows data and a name to be associated
     * with the vertex.
     * @param data     an object to be associated with this vertex
     * @param name     a name to be associated with this vertex
     */
    public Vertex(Object data, String name) {
        this.data = data;
        this.name = name;
        this.incidentEdgeList = new LinkedList<Edge>();
        this.height = 0;
        this.residualEdges = new ArrayList<Edge>();
        this.excess = 0;
        this.activated = false;
        labelCount = null;
        this.currentResidualOutEdgeCounter = 0;
    }
    
    /**
     * Return the name associated with this vertex.
     * @return  the name of this vertex
     */
    public String getName(){
        return this.name;
    }
    
    /**
     * Return the data associated with this vertex.
     * @return  the data of this vertex
     */
    public Object getData() {
        return this.data;
    }
    
    /**
     * Set the data associated with this vertex.
     * @param data  the data of this vertex
     */
    public void setData(Object data) {
        this.data = data;
    }
    /**
     * return the height variable;
     * @return height
     */
    public int getHeight() {
		return height;
	}
    
    /**
     * decrease label count at original height
     * increase label count at new height
     * @param height
     */
    
	public void setHeight(int height) {
		labelCount[this.height] --;
		this.height = height;
		labelCount[height]++;
	}
	
	/**
	 * Find and return out going edges from this vertex in original graph.
	 * @return List<Edge>
	 */
	public List<Edge> getOutGoingEdges()
	{
		List<Edge> edgeList = new ArrayList<Edge>();
		Iterator<Edge> j;
    	Edge e;
    	for(j = getIncidentEdgeList(); j.hasNext();)
    	{
    		e = j.next();
			if(e.getFirstEndpoint() == this)
				edgeList.add(e);
    	}
		return edgeList;
	}/**
	
	
	/**
	 * Find and return incoming edges from this vertex in original graph
	 * @return List<Edge>
	 */
	public List<Edge> getIncomingEdges()
	{
		List<Edge> edgeList = new ArrayList<Edge>();
		Iterator<Edge> j;
    	Edge e;
    	for(j = getIncidentEdgeList(); j.hasNext();)
    	{
    		e = j.next();
			if(e.getSecondEndpoint() == this)
				edgeList.add(e);
    	}
		return edgeList;
		
	}
	/**
	 *  returns the incidentEdgeList's, (which are incident
	 *  edges on this vertex in original graph) iterator
	 * @return Iterator<Edge> 
	 */
	public Iterator<Edge> getIncidentEdgeList()
	{
		return incidentEdgeList.iterator();
	}
	/**
	 *  returns the currentResidualOutEdgeCounter
	 *  which is the variable that keeps track of the index of which out edge in residual graph
	 *  we are currently trying to check for admissibility in Discharge routine
	 * @return currentResidualOutEdgeCounter
	 */
    public int getCurrentOutEdgeCounter()
    {
    	return currentResidualOutEdgeCounter;
    }
    /**
     * go to the next neighboring vertex in residual graph, by incrementing the edge index.
     */
    public void incrementCurrentOutEdgeCounter()
    {
    	++currentResidualOutEdgeCounter;
    }
    /**
     * reset the residual out edge counter which resets to first edge in residual graph's out edges
     * from this vertex
     */
    public void resetCurrentOutEdgeCounter()
    {
    	currentResidualOutEdgeCounter = 0;
    }
    
    public List<Edge> getResidualOutEdges() {
		return residualEdges;
	}
    
    /**
     * Compute the excess at a vertex by summing up flow on edges outgoing from this vertex in original graph
     * and summing up flow on edges incoming to this vertex in original graph, then returning the difference 
     * these values. Used in the preflow-push algorithm.
     * @return excess at this vertex
     */
    
    public int computeExcess()
    {
    	int outGoingFlow  =0;
    	int incomingFlow = 0;
    	Iterator<Edge> j;
    	Edge e;
    	for(j = getIncidentEdgeList(); j.hasNext();)
    	{
    		e = j.next();
    		if(e.getFirstEndpoint() == this)
    			outGoingFlow += e.getFlow();
    		else
    			incomingFlow += e.getFlow();
    	}
    	return incomingFlow - outGoingFlow;
    }
    
    
}
