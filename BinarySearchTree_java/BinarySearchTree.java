package assignment05;

import assignment05.Visualizer.ExpressionTree;

import java.util.*;

//BinarySearchSet

//************MEMBER VARIABLES**************************************************
// root_ -> can be null or an object
// count_ -> number of objects in tree

//**********PRIVATE OPERATIONS**************************************************
// private void recursiveInsert -> takes node as parameter and recursively searches for target
// private void traverse -- takes node as parameter and recursively searches entire bst for printing
// private Node remove1 -- allows the remove method to accept a node for recursive searching
// private Object inorderSuccessor (Node root)

//**********IMPLEMENTED METHODS**************************************************
/*
public Comparator<? super E> comparator();
public E first() throws NoSuchElementException;
public E last() throws NoSuchElementException;
public void add(E element) throws myException;
public boolean addAll(Collection<? extends E> elements);
public void clear();
public boolean contains(Object element);
public boolean containsAll(Collection<Object> elements);
public boolean isEmpty();
public Iterator<E> iterator();
public boolean remove(Object element);
public boolean removeAll(Collection<?> elements);
public int size();
public Object[] toArray();
*/

public class BinarySearchTree<T extends Comparable<? super T>> implements SortedSet<T> {

    Node root_;
    static int count_;

    /**********CONSTRUCTOR****************************************/
    /***
     * @param //root_ root node of tree
     * @param //value sets value to null
     */
    public BinarySearchTree() {
        root_ = new Node(null);
    }


    /**********OVERRIDDEN METHODS****************************************/
    /***
     *
     * @param item
     *          - the item whose presence is ensured in this set
     * @return
     */
    @Override
    public boolean add(T item) {

        Node begin_ = root_;

        if (begin_.value_ == null) {
            Node newNode = new Node(item);
            root_ = newNode;
            count_++;
            return true;
        }

        //recursive call
        else {
            recursiveInsert(item, begin_);
        }
        return true;
    }


    /***
     *
     * @param items
     *          - the collection of items whose presence is ensured in this set
     * @return
     */
    @Override
    public boolean addAll(Collection<? extends T> items) {
        for (T t : items) {
            add(t);
            count_++;
        }
        return true;
    }

    @Override
    public void clear() {
        root_ = null;
    }

    /***
     *
     * @param item
     *          - the item sought in this set
     * @return
     */
    @Override
    public boolean contains(T item) {
        Node anchorNode = root_;
        while (anchorNode != null) {
            if (item.compareTo((T) anchorNode.value_) > 0) {
                anchorNode = anchorNode.right_;
            } else if (item.compareTo((T) anchorNode.value_) < 0) {
                anchorNode = anchorNode.left_;
            } else return true;
        }
        return false;
    }

    /***
     *
     * @param items
     *          - the collection of items sought in this set
     * @return
     */
    @Override
    public boolean containsAll(Collection items) {
        for (Object item : items) {
            Boolean contains = contains((T) item);
            if (contains == false) {
                return false;
            }
        }
        return true;
    }

    /***
     *
     * @return
     * @throws NoSuchElementException
     */
    @Override
    public T first() throws NoSuchElementException {
        Node begin = root_;
        T smallestYet = (T) begin.value_;
        while (begin.left_ != null) {
            smallestYet = (T) begin.left_.value_;
            begin = begin.left_;
        }
        return smallestYet;
    }


    @Override
    public boolean isEmpty() {
        if (root_.value_ == null) {
            return true;
        }
        return false;
    }

    /***
     *
     * @return ensures largest item in set
     * @throws NoSuchElementException
     */
    @Override
    public T last() throws NoSuchElementException {
        Node begin = root_;
        T largestYet = (T) begin.value_;
        while (begin.right_ != null) {
            largestYet = (T) begin.right_.value_;
            begin = begin.right_;
        }
        return largestYet;
    }

    /***
     *
     * @param item
     *          - the item whose absence is ensured in this set
     * @return
     */
    @Override
    public boolean remove(Comparable item) {
        remove1(root_, (T) item);
        return true;
    }

    @Override
    public boolean removeAll(Collection items) {
        root_ = null;
        return true;
    }

    @Override
    public int size() {
        return count_;
    }

    @Override
    public ArrayList<T> toArrayList() {
        ArrayList<T> values = new ArrayList<>();
        traverse(root_, values);
        return values;
    }




/**********PRIVATE METHODS****************************************/


private void traverse(Node node, ArrayList values) {
    if(node != null){
        values.add(node.value_);
        traverse(node.left_,values);
        traverse(node.right_,values);
    }
}
    /***
     *
     * @param newValue value to be inserted
     * @param begin node to begin search
     */
    private void recursiveInsert(Comparable newValue, Node begin) {
        Node anchor = begin;
        //go right
        if (newValue.compareTo(anchor.value_) > 0) {
            if (begin.right_ == null) {
                Node nodeInWaiting = new Node(newValue);
                begin.right_ = nodeInWaiting;
            } else {
                begin =  begin.right_;
                recursiveInsert(newValue, begin);
            }
        } else if(newValue.compareTo(anchor.value_) < 0) {
            if (begin.left_ == null) {
                Node nodeInWaiting = new Node(newValue);
                begin.left_ = nodeInWaiting;
            } else {
                begin = begin.left_;
                recursiveInsert(newValue, begin);
            }
        }

    }
    private Node remove1(Node root, T item) {

        Comparable target = item;


        if (root == null) {
            return root;
        }

        /**********if root does not equal target**********/
        if (target.compareTo(root.value_) > 0) {
            root.right_ = remove1(root.right_, item);
        } else if (target.compareTo(root.value_) < 0) {
            root.left_ = remove1(root.left_, item);
        }
        /**********if root == target**********/
        else {
            //root has no children
            if (root.left_ == null) {
                return root.right_;
            }
            //root.left is null
            if (root.right_ == null) {
                return root.left_;
            }
            root.value_ = inorderSuccessor(root.right_);
            root.right_ = remove1(root.right_, (T) root.value_);
        }
        return root;
    }
    /***
     *
     * @param root
     * @return ensures in-order successor to root
     */
   private Object inorderSuccessor (Node root) {
        Object successor = root.value_;
        while (root.left_ != null) {
            successor = root.left_.value_;
            root = root.left_;
        }
        return successor;
    }
}


