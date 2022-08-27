package LinkedList;

import java.util.NoSuchElementException;

public class LinkedList<E> implements List {

    private static int currentNumberOfObjectsInList_;
    private static int currentIndex_;
    Node headerNode_;
    Node endNode_;

    //A void (no arguments) constructor, which creates an
    // empty sorted set sorted according to the natural ordering of its elements.
    public LinkedList(){
        currentNumberOfObjectsInList_ = 0;
        currentIndex_ = 0;

        headerNode_ = new Node(null);
        headerNode_.before_ = null;
        headerNode_.after_ = null;

        endNode_ = new Node(null);
        endNode_.after_ = headerNode_;
        endNode_.before_ = headerNode_;
    }


    @Override
    public void addFirst(Object nodeValue) {
        Node newNode = new Node(nodeValue);
        newNode.element_ = nodeValue;

        /* list is empty */
        if (headerNode_.after_ == null && endNode_.before_ == null) {
            newNode.before_ = headerNode_;
            newNode.after_ = endNode_;
            headerNode_.after_ = endNode_;
            endNode_.before_ = headerNode_;
            currentIndex_++;
            currentNumberOfObjectsInList_++;
        }
        /* list contains at least one element not header/ender */
        else if (headerNode_.after_ != null) {
            Node tempNode = new Node(nodeValue);
            tempNode = headerNode_.after_;
            headerNode_.after_ = newNode;
            newNode.after_ = tempNode;
            //I started to change the endNode val but I don't think it changes once there is an element

        }
    }

    public Boolean hasNext(Node nodeToCheck) {
        if (nodeToCheck.after_ != endNode_) {
            return true;
        }
        return false;
    }

    public Object next(Node nodeToCheck) {
        if (nodeToCheck.after_ != null) {
            return nodeToCheck.after_;
        }
        return null;
    }

    @Override
    public void addLast(Object o) {

    }

    @Override
    public void add(int index, Object element) throws IndexOutOfBoundsException {

    }

    @Override
    public Object getFirst() throws NoSuchElementException {
        return null;
    }

    @Override
    public Object getLast() throws NoSuchElementException {
        return null;
    }

    @Override
    public Object get(int index) throws IndexOutOfBoundsException {
        return null;
    }

    @Override
    public Object removeFirst() throws NoSuchElementException {
        return null;
    }

    @Override
    public Object removeLast() throws NoSuchElementException {
        return null;
    }

    @Override
    public Object remove(int index) throws IndexOutOfBoundsException {
        return null;
    }

    @Override
    public int indexOf(Object element) {
        return 0;
    }

    @Override
    public int lastIndexOf(Object element) {
        return 0;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }
}
