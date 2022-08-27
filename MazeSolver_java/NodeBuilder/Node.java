package assignment07.NodeBuilder;

public class Node {

    int x_;
    int y_;
    Boolean north;
    Boolean south;
    Boolean east;
    Boolean west;
    int value_;
    int bucketNumber_;
    Boolean visited_;
    Node previous_;

    public Node(int x, int y, int b) {
        x_ = x;
        y_ = y;
        north = true;
        south = true;
        east = true;
        west = true;
        bucketNumber_ = b;
        visited_ = false;
    }

    public void setValue() {
        value_ = 42;
    }
    public Boolean getNorth(){
        if (north == true) {
            return true;
        }
        return false;
    }

    public void setPreviousNode(Node node) {
        previous_ = node;
    }

    public Node getPrevious (){
        return previous_;
    }

    public Boolean getEast() {
        if (east == true) {
            return true;
        }
        return false;
    }

    public Boolean getSouth() {
        if (south == true) {
            return true;
        }
        return false;
    }

    public Boolean getWest() {
        if (west == true) {
            return true;
        }
        return false;
    }

    public int getX() {
        return x_;
    }

    public  int getY() {
        return y_;
    }

    public int getValue() {
        return value_;
    }

    public int getBucketNumber() {
        return bucketNumber_;
    }

    public void markAsVisited() {
        visited_ = true;
        return;
    }

    public Boolean getVisited() {
        if (visited_ == true) {
            return true;
        }
        return false;
    }
}
