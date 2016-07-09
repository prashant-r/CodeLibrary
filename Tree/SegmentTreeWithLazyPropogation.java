import java.util.*;

class SegmentTreeWithLazyPropogation
{
	private Node[] heap;
	private int[] array;
	private int size;
	public static void main(String args[]) throws Exception
	{
		new Main().run(args);
	}

	public SegmentTreeWithLazyPropogation(int [] array)
	{
		this.array = Arrays.copyOf(array, array.length);
		// The max size of this array is 2*2log(n) + 1
		size = (int) (2 * Math.pow(2.0, Math.floor((Math.log((double) array.length) / Math.log(2.0)) + 1)));
		heap = new Node[size];
		build(1, 0, array.length);
	}

	public int size()
	{
		return array.length;
	}

	// Initialize the nodes of the Segment Tree
	private void build(int v, int from , int size)
	{
		return;
	}

	/*
	*	Range Sum Query
	*/
	public int rsq(int from, int to)
	{
		return rsq(1, from, to);
	}

	private int rsq(int v, int from, int to)
	{
		return 1;


	}

	PrintWriter out;
	public void run(String args[]) throws Exception
	{
		out = new PrintWriter(System.out, true);
		SegmentTreeWithLazyPropogation st = null;

		String cmd = "cmp";
		
		while(true)
		{
			String [] line = scanner.nextLine().split(" ");
			if(line[0].equals("exit")) break;
			int args1 = 0, arg2 = 0, arg3 = 0;
			if(line.length > 1)
				arg1 = Integer.parseInt(line[1]);
			if(line.length > 2)
				args2  = Integer.parseInt(line[2]);
			if(line.length > 3)
				arg3 = Integer.parseInt(line[3]);
			 if ((!line[0].equals("set") && !line[0].equals("init")) && st == null) {
                out.println("Segment Tree not initialized");
                continue;
            }
            int array[];
            if(line[0].equals("set"))
            {
            	array = new int[line.length-1];
            	for(int i = 0; i < line.length -1; i++)
            	{
            		array[i] = Integer.parseInt(line[i+1]);
            	}
            	st = new SegmentTreeWithLazyPropogation(array);
            }
           	else if( line[0].equals("init"))
           	{
           		array = new int[arg1];
           		Arrays.fill(array, arg2);
           		st = new SegmentTreeWithLazyPropogation(array);

           		for(int i =0 ; i < st.size(); i++)
           		{
           			out.print(st.rsq(i,1) + " "  );
           		}
           		out.println();

           	}
           	else if(line[0].equals("up"))
           	{
           		 out.println(" NOT implemented");
           		// st.update(arg1, arg2, arg3);
           		// for(int i = 0; i < st.size(); i++)
           		// {
           		// 	out.print(st.rsq(i, 1) + " ");
           		// }
           		// out.println();
           	}
           	else if( line[0].equals("rsq"))
           	{
           		out.printf("Sum from %d to %d = %d/n", arg1, arg2, st.rsq(arg1, arg2));
           	}
           	else if( line[0].equals("rmq"))
           	{
           		out.printf("Min from %d to %d = %d/n", arg1, arg2, st.rMinQ(arg1, arg2));
           	}
           	else
           	{
           		out.println("Invalid command");
           	}
		}
	}
	public static class Node
	{
		int sum;
		int min;
		Integer pendingVal = null;
		int from;
		int to;
		int size()
		{
			return to-from + 1;
		}

	}

}