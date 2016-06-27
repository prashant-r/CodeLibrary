package Graph;
public enum GraphType {
	BIPARTITE,
	FIXED_DEGREE,
	MESH,
	RANDOM;
	
	private String name;
	static {
	 BIPARTITE.name = "Bipartite";
	 FIXED_DEGREE.name = "Fixed Degree";
	 MESH.name = "Mesh";
	 RANDOM.name = "Random";
	}
	
	public String getName() {
		return this.name;
	}
	
	public String toString() {
		return this.name;
	}
}
