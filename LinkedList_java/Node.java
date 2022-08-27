package LinkedList;

public class Node<E> {
    E element_;
    Node before_;
    Node after_;

    public Node(Object nodeValue) {
        element_ = (E) nodeValue;

    }
}
