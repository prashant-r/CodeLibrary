import java.io.*;
import java.util.*;
import java.math.*;

public class Treap<AnyType extends Comparable<? super AnyType>>
{
    TreapNode<AnyType> root;

    public Treap()
    {
        root = null;
    }

    public void insert(AnyType x)
    {
        root = insert(x, root);
    }

    public void remove(AnyType x)
    {
        root = remove(x, root);
    }

    public AnyType findMin() throws Exception
    {
        if(isEmpty())
            throw new Exception(" Trea Heap is Empty!! ");
        TreapNode<AnyType> ptr = root;
        while(ptr.left != null)
        {
            ptr = ptr.left;
        }
        return ptr.element;
    }

    public AnyType findMax() throws Exception
    {
        if(isEmpty())
            throw new Exception("Tree Heap is Empty!! ");
        TreapNode<AnyType> ptr = root;
        while(ptr.right != null)
        {
            ptr = ptr.right;
        }
        return ptr.element;
    }

    public boolean contains(AnyType x)
    {
        return contains(x, root);
    }

    public boolean contains(AnyType x, TreapNode<AnyType> t)
    {
        if(t == null)
            return false;
        int compareResult = x.compareTo(t.element);
        if(compareResult < 0 )
        {
            return contains(x, t.left);
        }
        if(compareResult == 0)
        {
            return true;
        }
        else
            return contains(x, t.right);
    }

    public boolean isEmpty()
    {
        return root == null;
    }

    public void makeEmpty()
    {
        root = null;
    }

    public void printTree()
    {
        if(isEmpty())
            System.out.println("Empty Tree");
        else
            printTree(root);
    }


    private TreapNode<AnyType> insert(AnyType x, TreapNode<AnyType> t )
    {
        if(t == null)
            return new TreapNode<AnyType>(x, null, null);
        int compareResult = x.compareTo(t.element);
        if(compareResult < 0 )
        {
            t.left = insert(x, t.left);
            if( t.left.priority < t.priority)
                t = rotateWithLeftChild(t);
        }
        else if(compareResult > 0)
        {
            t.right = insert(x, t.right);
            if(t.right.priority < t.priority)
                t = rotateWithRightChild(t);
        }
        return t;
    }

    private TreapNode<AnyType> remove(AnyType x,  TreapNode<AnyType> t)
    {
        if(t!=null)
        {
            int compareResult = x.compareTo(t.element);
            if(compareResult <0)
            {
                t.left = remove(x, t.left);
            }
            else if(compareResult >0)
            {
                t.right = remove(x, t.right);
            }
            else
            {
                if(t.left == null) return t.right;
                if(t.right == null) return t.left;

                if(t.left.priority < t.right.priority)
                {
                    t = rotateWithLeftChild(t);
                    t.right = remove(x, t.right);
                }
                else
                {
                    t = rotateWithRightChild(t);
                    t.left = remove(x, t.left);
                }
            }
        }
        return t;
    }

    private void printTree(TreapNode<AnyType> t)
    {
        if(t != null)
        {
            printTree(t.left);
            System.out.println(t.element.toString());
            printTree(t.right);
        }
    }

    private TreapNode<AnyType> rotateWithRightChild(TreapNode<AnyType> k1)
    {
        TreapNode<AnyType> k2 = k1.right;
        k1.right = k2.left;
        k2.left = k1;
        return k2;
    }

    private TreapNode<AnyType> rotateWithLeftChild(TreapNode<AnyType> k2)
    {
        TreapNode<AnyType> k1 = k2.left;
        k2.left = k1.right;
        k1.right = k2;
        return k1;
    }

    private static class TreapNode<AnyType>
    {
        private static Random random = new Random();

        TreapNode(AnyType theElement)
        {
            this(theElement, null, null);
        }
        TreapNode(AnyType theElement, TreapNode<AnyType> lt, TreapNode<AnyType> rt)
        {
            element = theElement;
            left = lt;
            right = rt;
            priority = random.nextInt();
        }

        AnyType element;
        TreapNode<AnyType> left;
        TreapNode<AnyType> right;
        int priority;
    }

    public static void main(String args[]) throws Exception
    {
        Treap<Integer> t = new Treap<Integer>( );
        final int NUMS = 400;
        final int GAP  =   307;  // 307

        System.out.println( "Checking... (no bad output means success)" );

        for( int i = GAP; i != 0; i = ( i + GAP ) % NUMS )
        {
            t.insert( i );
        }
        System.out.println( "Inserts complete" );
        for( int i = 1; i < NUMS; i+= 2 )
            t.remove( i );
        System.out.println( "Removes complete" );

        if( NUMS < 40 )
            t.printTree( );

        if( t.findMin( ) != 2 || t.findMax( ) != NUMS - 2 )
            System.out.println( "FindMin or FindMax error!" );

        for( int i = 2; i < NUMS; i+=2 )
            if( !t.contains( i ) )
                System.out.println( "Error: find fails for " + i );

        for( int i = 1; i < NUMS; i+=2 )
            if( t.contains( i ) )
                System.out.println( "Error: Found deleted item " + i );
    }

}