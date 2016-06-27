package GraphStruct;


public class Edge {
    /** the first endpoint of the edge */
    private Vertex v1;
    
    /** the second endpoint of the edge */
    private Vertex v2;
	private Object capacity;  // an object associated with this edge
    private Object name;  // a name associated with this edge 
    public double flow;
    private Edge back;
    

    /**
     * Constructor that allows capacity and a name to be associated
     * with the edge. Also, construct a back edge that has capacity 0, and points back edge of back edge
     * to this edge;
     * @param v     the first endpoint of this edge
     * @param w     the second endpoint of this edge
     * @param capacity  capacity to be associated with this edge
     * @param isBack identifies this as a forward edge or not
     * @param name  a name to be associated with this edge
     */
    public Edge (Vertex v, Vertex w, Object capacity, Object name, boolean isBack) {
        this.capacity = capacity;
        this.name = name;
        this.v1 = v;
        this.v2 = w;
        flow = 0;
        v.residualEdges.add(this);
        if(!isBack){
        	back = new Edge(w,v, (double) 0.0 , null,true);
        	back.setBackEdge(this);
        }
    }

    /**
     * Return the first endpoint of this edge.
     * @return  the first endpoint of this edge
     */
    public Vertex getFirstEndpoint() {
        return this.v1;
    }
    /**
     * set back edge for this edge to j
     * @param j
     */
    public void setBackEdge(Edge j)
    {
    	back = j;
    }
    /**
     * returns the back edge for this edge. Note, this could return a forward edge or backward edge
     * in the residual graph based on the edge being invoked on.
     * @return Edge
     */
    public Edge getBackEdge()
    {
    	return back;
    }
    /**
     * updates the edges flow, accoring to the bottleneck passed in by the FF, SFF algorithms that have identifieed
     * it on an augmenting path. Increase flow by bottleneck on forward edge, do the reverse on a backward edge.
     * @param toVertex
     * @param bottleneck
     */
    public void updateEdgeFlow(Vertex toVertex,double bottleneck)
    {
    	if(bottleneck >= 0)
    	{
    		if(toVertex == v1)// Backward Edge
    			flow -= bottleneck;
    		else if(toVertex == v2) // Forward Edge
    			flow += bottleneck;
    		else
    			throw new IllegalArgumentException("During flow update: No endpoint "+ toVertex.getName() + " exists for edge " + v1.getName() +"->" + v2.getName() );
    		if(!(flow >= 0))
    			throw new IllegalArgumentException("During flow update: Flow is negative!");
    		if(!(flow <= (double)capacity))
    			throw new IllegalArgumentException("During flow update: Flow is larger than capacity!");
    	}
    }
    /**
     * Given a vertex v that the edge is pointing to return the residual capacity, be it a forward edge
     * or a backward edge;
     * @param v
     * @return double
     */
    public double residualCapacityToVertex(Vertex v)
    {
    	if(v == v1)  // Backward Edge 
    		return flow;
    	else if(v == v2) // Forward Edge
    		return (double)capacity - flow;
    	else
    		throw new IllegalArgumentException("During Query residual cap : No endpoint "+ v.getName() + " exists for edge " + v1.getName() +"->" + v2.getName() );
    }
    
    /**
     * Return the second endpoint of this edge.
     * @return  the second endpoint of this edge
     */
    public Vertex getSecondEndpoint() {
        return this.v2;
    }

    /**
     * Return the capacity associated with this edge.
     * @return  the capacity of this edge
     */
    public Object getcapacity() {
        return this.capacity;
    }
        
    /**
     * Set the capacity associated with this edge.
     * @param capacity  the capacity of this edge
     */
    public void setcapacity(Object capacity) {
        this.capacity = capacity;
    }
    
    /**
     * Return the name associated with this edge.
     * @return  the name of this edge
     */
    public Object getName() {
        return this.name;
    }
    /**
     * return the flow on this edge
     * @return double
     */
    public double getFlow() {
		return flow;
	}
/**
 * set the flow on this edge to the flow in parameter
 * @param flow
 */
	public void setFlow(double flow) {
		this.flow = flow;
	}
	/**
	 * Similar to the residualCapacityToVertex 
	 * returns the residual capacity on an edge in residual graph.
	 * Duplicate function, used sometimes.
	 * @return capacity - flow
	 */
	public int remaining()
	{
		return (int)((double) capacity - (double ) flow );
	}
}
