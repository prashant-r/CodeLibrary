package Graph;

public interface TypedGraphGenerator {
	abstract public GraphType getGraphType();
	abstract public void generateGraph(
			int n, int m, float maxProbability, int minCapacity, int maxCapacity, String fileName);
}
