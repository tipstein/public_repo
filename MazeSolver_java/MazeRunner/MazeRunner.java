package assignment07.MazeRunner;

import assignment07.NodeBuilder.MazeNodeBuilder;
import assignment07.NodeBuilder.Node;
import java.util.LinkedList;

public class MazeRunner {

    static MazeNodeBuilder mazeNodes_;
    LinkedList<Node>solveList_;
    LinkedList<Node>tempList_;
    Node startNode_;

    public MazeRunner(MazeNodeBuilder mazeNodeBuilder) throws NoSuchFieldException {
       mazeNodes_ = mazeNodeBuilder;
       startNode_ = mazeNodes_.findStart();
       solveList_ = new LinkedList<>();
       tempList_ = new LinkedList<>();
    }

    public Node breadthFirstSearch() {
        if (startNode_.getValue() == 71) {
            return startNode_;
        }
        Node returnNode =  MarkNodeDirections(startNode_);
        return returnNode;
    }

    public void replaceNodeValues() {
        for (Node node: solveList_) {
            int index = node.getBucketNumber();
            Node newValue = mazeNodes_.getNodeByBucket(index);
            newValue.setValue();
        }
    }

    public void printpreviousNodeList (Node goalNode) {

        if ( goalNode.getPrevious() != null ) {
            Node previousNode = goalNode.getPrevious();
            System.out.println(goalNode.getPrevious().getBucketNumber());
            Node node = goalNode.getPrevious();
            printpreviousNodeList(node);
        }
    }

    public LinkedList getSolutionList (Node goalNode) {
        if ( goalNode.getPrevious() != null ) {
            Node previousNode = goalNode.getPrevious();
            Node node = goalNode.getPrevious();
            solveList_.add(node);
            getSolutionList(node);
        }
        solveList_.addFirst(goalNode);
        return solveList_;
    }

    private Node MarkNodeDirections(Node node) {

        Node nodeToMark = node;
        nodeToMark.markAsVisited();

        Node north = mazeNodes_.getNorth(node);
        if (north != null && north.getVisited() != true) {
            if (north.getValue() == 71) {
                north.setPreviousNode(node);
                return north;
            } else {
                north.setPreviousNode(node);
                MarkNodeDirections(north);
            }
        }

        Node east = mazeNodes_.getEast(node);
        if (east != null && east.getVisited() != true) {
            if (east.getValue() == 71) {
                east.setPreviousNode(node);
                return east;
            } else {
                east.setPreviousNode(node);
                MarkNodeDirections(east);
            }
        }

        Node south = mazeNodes_.getSouth(node);
        if (south != null && south.getVisited() != true) {
            if (south.getValue() == 71) {
                south.setPreviousNode(node);
                return south;
            } else {
                south.setPreviousNode(node);
                MarkNodeDirections(south);
            }
        }

        Node west = mazeNodes_.getWest(node);
        if (west != null && west.getVisited() != true) {
            MarkNodeDirections(west);
            if (west.getValue() == 71) {
                west.setPreviousNode(node);
                return west;
            } else {
                west.setPreviousNode(node);
                MarkNodeDirections(west);
            }

        }
        return nodeToMark;
    }

    public LinkedList getTempList() {
        return tempList_;
    }


}
