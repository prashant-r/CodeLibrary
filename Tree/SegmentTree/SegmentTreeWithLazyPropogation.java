import java.util.*;
import java.io.*;
import java.math.*;

class SegmentTreeWithLazyPropogation
{
	private Node[] heap;
	private int[] array;
	private int size;
	public static void main(String args[]) throws Exception
	{
		run(args);
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
		heap[v] = new Node();
        heap[v].from = from;
        heap[v].to = from + size - 1;

        if (size == 1) {
            heap[v].sum = array[from];
            heap[v].min = array[from];
        } else {
            //Build childs
            build(2 * v, from, size / 2);
            build(2 * v + 1, from + size / 2, size - size / 2);

            heap[v].sum = heap[2 * v].sum + heap[2 * v + 1].sum;
            //min = min of the children
            heap[v].min = Math.min(heap[2 * v].min, heap[2 * v + 1].min);
        }
	}

	public void update(int from, int to, int value)
	{
		update(1, from, to , value);
	}

	public void update(int v, int from, int to , int value)
	{
		Node n = heap[v];

		if(contains(from, to, n.from, n.to))
		{
			change(n, value);
		}
		if (n.size() == 1) return;

		if(intersects(from, to, n.from, n.to))
		{
			propagate(v);
			update(2*v, from, to, value);
			update(2*v+1, from, to, value);
			n.sum = heap[2 * v].sum + heap[2 * v + 1].sum;
            n.min = Math.min(heap[2 * v].min, heap[2 * v + 1].min);
		}
	}

	private void propagate(int v) {
        Node n = heap[v];

        if (n.pendingVal != null) {
            change(heap[2 * v], n.pendingVal);
            change(heap[2 * v + 1], n.pendingVal);
            n.pendingVal = null; //unset the pending propagation value
        }
    }

	public void change(Node n, int value)
	{
		n.pendingVal = value;
		n.sum = n.size() * value;
		n.min = value;
		array[n.from] = value;
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
		Node n = heap[v];
		if (contains(from, to, n.from, n.to)) {
            return heap[v].sum;
        }

        if (intersects(from, to, n.from, n.to)) {
        	propagate(v);
            int leftSum = rsq(2 * v, from, to);
            int rightSum = rsq(2 * v + 1, from, to);

            return leftSum + rightSum;
        }

        return 0;
	}

 	private boolean contains(int from1, int to1, int from2, int to2) {
        return from2 >= from1 && to2 <= to1;
    }
	
   	private boolean intersects(int from1, int to1, int from2, int to2) {
        return from1 <= from2 && to1 >= from2   //  (.[..)..] or (.[...]..)
                || from1 >= from2 && from1 <= to2; // [.(..]..) or [..(..)..
    }
	/* 
		Range Minimum Query
	*/
	public int rMinQ(int v, int from, int to)
	{
		Node n = heap[v];
		if(contains(from, to, n.from, n.to ))
		{
			return n.min;
		}
		if (intersects(from, to, n.from, n.to)) {

			propagate(v);

            int leftMin = rMinQ(2 * v, from, to);
            int rightMin = rMinQ(2 * v + 1, from, to);

            return Math.min(leftMin, rightMin);
        }

        return Integer.MAX_VALUE;
	}

	public int rMinQ(int from, int to)
	{
		return rMinQ(1, from, to);
	}

	static PrintWriter out;
	public static void run(String args[]) throws Exception
	{
		out = new PrintWriter(System.out, true);
		SegmentTreeWithLazyPropogation st = null;
		Scanner scanner = new Scanner(new FileInputStream(args[0]));
		String cmd = "cmp";
        while (true) {
            String[] line = scanner.nextLine().split(" ");

            if (line[0].equals("exit")) break;

            int arg1 = 0, arg2 = 0, arg3 = 0;

            if (line.length > 1) {
                arg1 = Integer.parseInt(line[1]);
            }
            if (line.length > 2) {
                arg2 = Integer.parseInt(line[2]);
            }
            if (line.length > 3) {
                arg3 = Integer.parseInt(line[3]);
            }

            if ((!line[0].equals("set") && !line[0].equals("init")) && st == null) {
                out.println("Segment Tree not initialized");
                continue;
            }
            int array[];
            if (line[0].equals("set")) {
                array = new int[line.length - 1];
                for (int i = 0; i < line.length - 1; i++) {
                    array[i] = Integer.parseInt(line[i + 1]);
                }
                st = new SegmentTreeWithLazyPropogation(array);
            }
            else if (line[0].equals("init")) {
                array = new int[arg1];
                Arrays.fill(array, arg2);
                st = new SegmentTreeWithLazyPropogation(array);

                for (int i = 0; i < st.size(); i++) {
                    out.print(st.rsq(i, i) + " ");
                }
               out.println();
            }

            else if (line[0].equals("up")) {
                st.update(arg1-1, arg2-1, arg3);
                for (int i = 0; i < st.size(); i++) {
                    out.print(st.rsq(i, i) + " ");
                }
                out.println();
            }
            else if (line[0].equals("rsq")) {
                out.printf("Sum from %d to %d = %d%n", arg1, arg2, st.rsq(arg1-1, arg2-1));
            }
            else if (line[0].equals("rmq")) {
                out.printf("Min from %d to %d = %d%n", arg1, arg2, st.rMinQ(arg1-1, arg2-1));
            }
            else {
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