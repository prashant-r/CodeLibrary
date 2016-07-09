import java.io.*;
import java.util.*;
import java.math.*;

abstract class AbstractSortedSet<AnyType> extends AbstractSet<AnyType> implements SortedSet<AnyType> {
	public AnyType first() {
		return null;
	}

	public AnyType last() {
		return null;
	}

	public Iterator<AnyType> iterator() {
		return null;
	}

	public SortedSet<AnyType> headSet(AnyType toElement) {
		return null;
	}

	public SortedSet<AnyType> tailSet(AnyType fromElement) {
		return null;
	}

	public SortedSet<AnyType> subSet(AnyType fromElement, AnyType toElement) {
		return null;
	}

	public Comparator<? super AnyType> comparator() {
		return null;  // uses natural ordering
	}
}

class SkipList<AnyType extends Comparable<AnyType>> extends AbstractSortedSet<AnyType> {
	private SkipListNode<AnyType> head;
	private int maxLevel;
	private int size;

	private static final double PROBABILITY = 0.5;

	public SkipList() {
		size = 0;
		maxLevel = 0;
		head = new SkipListNode<AnyType>(null);
		head.nextNodes.add(null);
	}

	public SkipListNode getHead() {
		return head;
	}

	public int size() {
		return size;
	}

	public boolean add(AnyType e) {
		if (contains(e)) return false;
		size++;

		int level = 0;
		while (Math.random() < PROBABILITY)
			level++;
		while (level > maxLevel) {
			head.nextNodes.add(null);
			maxLevel++;
		}
		SkipListNode newNode = new SkipListNode<AnyType>(e);
		SkipListNode current = head;
		do {
			current = findNext(e, current, level);
			newNode.nextNodes.add(0, current.nextNodes.get(level));
			current.nextNodes.set(level, newNode);
		} while (level-- > 0);
		return true;
	}

	private SkipListNode find(AnyType e) {
		return find(e, head, maxLevel);
	}

	private SkipListNode find(AnyType e, SkipListNode current, int level) {
		do {
			current = findNext(e, current, level);
		} while (level-- > 0);
		return current;
	}
	private SkipListNode findNext(AnyType e, SkipListNode current, int level) {
		SkipListNode next = (SkipListNode) current.nextNodes.get(level);
		while (next != null) {
			AnyType value = (AnyType) next.getValue();
			if (lessThan(e, value)) {
				break;
			}
			current = next;
			next = (SkipListNode) current.nextNodes.get(level);
		}
		return current;
	}
	public boolean contains(Object o) {
		AnyType e = (AnyType) o;
		SkipListNode node = find(e);
		return node != null && node.getValue() != null && equalTo((AnyType) node.getValue(), e);
	}

	public Iterator<AnyType> iterator() {
		return new SkipListIterator(this);
	}

	private boolean lessThan(AnyType a, AnyType b) {
		return a.compareTo(b) < 0;
	}
	private boolean equalTo(AnyType a, AnyType b) {
		return a.compareTo(b) == 0;
	}
	private boolean greaterThan(AnyType a, AnyType b) {
		return a.compareTo(b) > 0;
	}

	public static class SkipListNode<AnyType> {
		private AnyType value;
		public List<SkipListNode<AnyType>> nextNodes;

		public AnyType getValue() {
			return value;
		}
		public SkipListNode(AnyType value) {
			this.value = value;
			nextNodes = new ArrayList<SkipListNode<AnyType>>();
		}
		public int level() {
			return nextNodes.size() - 1;
		}
		public String toString() {
			return "SLN: " + value;
		}
	}

	public static class SkipListIterator<AnyType extends Comparable<AnyType>> implements Iterator<AnyType> {
		SkipList<AnyType> list;
		SkipListNode<AnyType> current;

		public SkipListIterator(SkipList<AnyType> list) {
			this.list = list;
			this.current = list.getHead();
		}
		public boolean hasNext() {
			return current.nextNodes.get(0) != null;
		}

		public AnyType next() {
			current = (SkipListNode<AnyType>) current.nextNodes.get(0);
			return (AnyType) current.getValue();
		}
		public void remove() throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}
	}

	public static void main(String args[]) {
		SkipList testList = new SkipList<Integer>();
		System.out.println(testList);
		testList.add(4);
		System.out.println(testList);
		testList.add(1);
		System.out.println(testList);
		testList.add(2);
		System.out.println(testList);
		testList = new SkipList<String>();
		System.out.println(testList);
		testList.add("hello");
		System.out.println(testList);
		testList.add("beautiful");
		System.out.println(testList);
		testList.add("world");
		System.out.println(testList);

	}
	public String toString() {
		String s = "SkipList: ";
		for (Object o : this)
			s += o + ", ";
		return s.substring(0, s.length() - 2);
	}
}


