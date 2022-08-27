package assignment07.NodeBuilder;

import assignment07.MazeRunner.MazeRunner;

import java.util.ArrayList;
import java.util.LinkedList;

public class MazeNodeBuilder {

    public ArrayList<Node>nodeList_;
    int xAxisLength_;
    int yAxisLength_;
    String solutionString;


    public MazeNodeBuilder(int xAxisLength, int yAxisLength) {
        nodeList_ = new ArrayList<>();
        yAxisLength_ = yAxisLength;
        xAxisLength_ = xAxisLength;
        int bucketNumber = 0;
        for (int i = 0; i < yAxisLength; i++) {
            for (int q = 0; q < xAxisLength; q++) {
                Node node = new Node(q, i, bucketNumber);
                nodeList_.add(node);
                node.setPreviousNode(null);
                bucketNumber++;
            }
        }
    }


    public void setNodeValues(String mapString){
        int length = (mapString.length());
        int i = 0;
        int nodeCount = 0;
        for (Node node: nodeList_) {
            if (mapString.substring(i, i + 2).equals("88")) {
                nodeList_.get(nodeCount).value_ = 88;
                nodeList_.get(nodeCount).south = false;
                nodeList_.get(nodeCount).west = false;
                nodeList_.get(nodeCount).north = false;
                nodeList_.get(nodeCount).east = false;
            }

            if (mapString.substring(i, i + 2).equals("32")) {
                nodeList_.get(nodeCount).value_ = 32; // " "
            }

            if (mapString.substring(i, i + 2).equals("83")) {
                nodeList_.get(nodeCount).value_ = 83; // "S"
            }

            if (mapString.substring(i, i + 2).equals("71")) {
                nodeList_.get(nodeCount).value_ = 71; // "G"
            }
            i+=2;
            nodeCount+=1;
        }
    }

    public void setExes() {
        for (Node node: nodeList_) {
            if (node.value_ == 88) {
                node.north = false;
                node.east = false;
                node.south = false;
                node.west = false;
                node.visited_ = true;
            }
        }
    }

    public void printNodeValues() {
        int index = 0;
        for (Node node: nodeList_) {
            System.out.println("index " + index + " is " + node.value_);
            index++;
        }
    }


    public String stringNodeValues () {
        solutionString = "";
        for (Node n : nodeList_) {
            char mapChar = (char) n.getValue();
            solutionString += mapChar;
        }
        return solutionString;
    }

    public void breakSolutionString() {
        int x = solutionString.length()/yAxisLength_;
        for (int i = 0; i < x; i++) {

        }
    }

    public void printEdgeValues() {
        int index = 0;
        for (Node node: nodeList_) {
            System.out.println("node " + index + ": ");
            System.out.println("east " + node.east);
            System.out.println("south " + node.south);
            System.out.println("west " + node.west);
            System.out.println("north " + node.north);
            index++;

        }

    }

    public void getNodeXY(int index) {
        System.out.println("Node at " + index + " is " + nodeList_.get(index).x_);
        System.out.println("Node at " + index + " is " + nodeList_.get(index).y_);

    }

    public Node getNodeByIndex(int x, int y) {
        int index = ((xAxisLength_) * y) + x;
        return nodeList_.get(index);
    }

    public int getNodeIndex(int x, int y) {
        int indexValue = ((xAxisLength_) * y) + x;
        return indexValue;
    }

    public ArrayList<Node> getNodeList_() {
        return nodeList_;
    }

    public Node findStart() throws NoSuchFieldException {
        for (Node node: nodeList_) {
            if (node.value_ == 83) {
                return node;
            }
        }
        throw new NoSuchFieldException();
    }

    public Node findGoal() throws NoSuchFieldException {
        for (Node node: nodeList_) {
            if (node.value_ == 71) {
                return node;
            }
        }
        throw new NoSuchFieldException();
    }

    public int getxAxisLength() {
        return xAxisLength_;
    }

    public int getyAxisLength() {
        return yAxisLength_;
    }

    public void printCoordinates() {
        for (Node node: nodeList_) {
            System.out.println(node.getX() + " " + node.getY());
            System.out.println("value is: " + node.getValue());
        }
    }

    public Node getNodeByBucket(int bucketNumber) {
        Node node = nodeList_.get(bucketNumber);
        return node;
    }

    public int getBucketNumber(Node node) {
        return node.bucketNumber_;
    }

    public Node getNorth(Node node) {
        int bucketNumber = node.getBucketNumber();
        int bucketNorth = bucketNumber - xAxisLength_;
        if (bucketNorth < 0) {
            return null;
        }
        Node north = getNodeByBucket(bucketNorth);
        return north;
    }

    public Node getEast(Node node) {
        int bucketNumber = node.getBucketNumber();
        int bucketEast = bucketNumber + 1;
        if (bucketEast > xAxisLength_ * yAxisLength_) {
            return null;
        }
        if (getNodeByBucket(bucketEast).getValue() == 88) {
            return null;
        }
        Node east = getNodeByBucket(bucketEast);
        return east;
    }

    public Node getSouth(Node node) {
        int bucketNumber = node.getBucketNumber();
        int bucketSouth = bucketNumber + xAxisLength_;
        if (bucketSouth > yAxisLength_ * xAxisLength_) {
            return null;
        }
        Node south = getNodeByBucket(bucketSouth);
        return south;
    }

    public Node getWest(Node node) {
        int bucketNumber = node.getBucketNumber();
        int bucketWest = bucketNumber - 1;
        if (bucketWest < 0) {
            return null;
        }
        if (getNodeByBucket(bucketWest).getValue() == 88) {
            return null;
        }
        Node west = getNodeByBucket(bucketWest);
        return west;
    }
}
