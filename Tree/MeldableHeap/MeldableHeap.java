import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Random;

public class MeldableHeap<AnyType extends Comparable<? super AnyType>> implements Queue<AnyType>  {
	
	protected Random rand;
	
	protected int n;

	protected Node<AnyType> r;	
	protected static class Node<AnyType> {
		AnyType x;
		public Node left;
		public Node right;
		public Node parent;	
	}

	public MeldableHeap() {
		rand = new Random();
		r = null;
	}

	public Node nextNode(Node w) {
		if (w.right != null) {
			w = w.right;
			while (w.left != null)
				w = w.left;
		} else {
			while (w.parent != null && w.parent.left != w)
				w = w.parent;
			w = w.parent;
		}
		return w;
	}
		
	@SuppressWarnings({"unchecked"})
	protected Node newNode() {
		try {
			Node u = new Node<AnyType>();
			u.parent = u.left = u.right = null;
			return u;
		} catch (Exception e) {
			return null;
		}
	}


	public boolean add(AnyType x) {
		Node<AnyType> u = newNode();
		u.x = x;
		r = merge(u, r);
		r.parent = null;
		n++;
		return true;
	}
	
	public AnyType findMin() {
		return r.x;
	}
	
	public AnyType remove() {
		AnyType x = r.x;
		r = merge(r.left, r.right);
		if (r != null) r.parent = null;
		n--;
		return x;
	}
	
	protected void remove(Node<AnyType> u) {
		if (u == r) {
			remove();
		} else {
			if (u == u.parent.left) {
				u.parent.left = null;
			} else {
				u.parent.right = null;
			}
			u.parent = null;
			r = merge(r, u.left);
			r = merge(r, u.right);
			r.parent = null;
			n--;
		}
	}
	
	public Node<AnyType> merge(Node<AnyType> h1, Node<AnyType> h2) {
		if (h1 == null) return h2;
		if (h2 == null) return h1;
		if (h2.x.compareTo(h1.x) < 0) return merge(h2, h1);
		// now we know h1.x <= h2.x
		if (rand.nextBoolean()) {
			h1.left = merge(h1.left, h2);
			h1.left.parent = h1;
		} else {
			h1.right = merge(h1.right, h2);
			h1.right.parent = h1;
		}
		return h1;
	}
	
	public AnyType element() {
		if (r == null) throw new NoSuchElementException();
		return r.x;
	}

	public boolean offer(AnyType x) {
		return add(x);
	}

	public AnyType peek() {
		return r == null ? null : r.x;
	}

	public AnyType poll() {
		return r == null ? null : remove();
	}

	public boolean addAll(Collection<? extends AnyType> c) {
		for (AnyType x : c) add(x);
		return !c.isEmpty();
	}

	public boolean contains(Object x) {
		for (AnyType y : this) {
			if (y.equals(x)) return true;
		}
		return false;
	}

	public boolean containsAll(Collection<?> c) {
		for (Object x : c) {
			if (!contains(x)) return false;
		}
		return true;
	}

	public Iterator<AnyType> iterator() {
		class MHI implements Iterator<AnyType> {
			protected Node<AnyType> w, prev;
			public MHI(Node<AnyType> iw) {
				w = iw;
			}
			public boolean hasNext() {
				return w != null;
			}
			public AnyType next() {
				AnyType x = w.x;
				prev = w;
				w = nextNode(w);
				return x;
			}
			public void remove() {
				MeldableHeap.this.remove(prev);
			}
		}
		Node<AnyType> w = r;
		while (w.left != null) w = w.left;
		return new MHI(w);
	}

	public boolean remove(Object x) {
		Iterator<AnyType> it = iterator();
		while (it.hasNext()) {
			if (it.next().equals(x)) {
				remove();
				return true;
			}
		}
		return false;
	}

	public boolean removeAll(Collection<?> c) {
		boolean modified = false;
		for (Object x : c) {
			modified = modified || remove(x);
		}
		return modified;
	}

	public boolean retainAll(Collection<?> c) {
		boolean modified = false;
		Iterator<AnyType> it = iterator();
		while (it.hasNext()) {
			if (!c.contains(it.next())) {
				it.remove();
				modified = true;
			}
		}
		return modified;
	}

	public int size() {
		return n;
	}
	
	public void clear() {
		r = null;
		n = 0;
	}
	
	public Object[] toArray() {
		Object[] a = new Object[n];
		int i = 0;
		for (AnyType x : this) {
			a[i++] = x;
		}
		return a;
	}

	public static void main(String[] args) {
		System.out.println("==== MeldableHeap ====");
		speedTests(new MeldableHeap<Integer>());
	}

	protected static void speedTests(Queue<Integer> q) {
		Random r = new Random();
		q.clear();
		int n = 1000000;
		long start, stop;
		double elapsed;
		System.out.print("performing " + n + " adds...");
		start = System.nanoTime();
		for (int i = 0; i < n; i++) {
			q.add(r.nextInt());
		}
		stop = System.nanoTime();
		elapsed = 1e-9*(stop-start);
		System.out.println("(" + elapsed + "s [" 
				+ (int)(((double)n)/elapsed) + "ops/sec])");
		n *= 10;
		System.out.print("performing " + n + " add/removes...");
		start = System.nanoTime();
		for (int i = 0; i < n; i++) {
			if (r.nextBoolean()) {
				q.add(r.nextInt());
			} else {
				q.remove();
			}
		}
		stop = System.nanoTime();
		elapsed = 1e-9*(stop-start);
		System.out.println("(" + elapsed + "s [" 
				+ (int)(((double)n)/elapsed) + "ops/sec])");
		
	}

	public boolean isEmpty() {
		return r == null;
	}
	

	@SuppressWarnings("unchecked")
	public <T2> T2[] toArray(T2[] a) {
		if (a.length < n) {
			a = (T2[])Array.newInstance(a.getClass().getComponentType(), n);
		}
		int i = 0;
		for (AnyType x : this) {
			a[i++] = (T2)x;
		}
		return a;
	}
}
