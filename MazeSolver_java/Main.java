package assignment07;

import assignment07.MazeBuilder.MazeReader;
import assignment07.MazeRunner.MazeRunner;
import assignment07.NodeBuilder.MazeNodeBuilder;
import assignment07.NodeBuilder.Node;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class Main {

    public static void main(String[] args) throws IOException, NoSuchFieldException {
//        MazeReader mazeReader = new MazeReader(new File("src/assignment07/Mazes/mazes/demoMaze.txt"));
//        //set the xAxisLength and yAxisLength values as ints
//        mazeReader.transformDimensions();
//        int xAxis = mazeReader.getXAxisLength();
//        int yAxis = mazeReader.getYAxisLength();
//        MazeNodeBuilder mazeNodeBuilder = new MazeNodeBuilder(xAxis, yAxis);
//        String map = mazeReader.getMap();
//        mazeNodeBuilder.setNodeValues(map);
//        mazeNodeBuilder.setExes();
//        MazeRunner mazeRunner = new MazeRunner(mazeNodeBuilder);
//       Node node = mazeRunner.breadthFirstSearch();
//       Node goal = mazeNodeBuilder.findGoal();
//        LinkedList<Node>list = mazeRunner.getSolutionList(goal);
//        for (Node n: list) {
////            System.out.println(n.getBucketNumber());
//            int bucketIndex = n.getBucketNumber();
//            Node xNode = mazeNodeBuilder.getNodeByBucket(bucketIndex);
//            xNode.setValue();
//        }
////        mazeNodeBuilder.printNodeValues();
//        String solutionString = mazeNodeBuilder.stringNodeValues();
//        System.out.println(solutionString);
//        mazeNodeBuilder.breakSolutionString();

















//        Node node = mazeNodeBuilder.getNodeByBucket(27);
//        System.out.println("start node value test: " + node.getValue());
//
//        Node north = mazeNodeBuilder.getNorth(node);
//        Node east = mazeNodeBuilder.getEast(node);
//        Node south = mazeNodeBuilder.getSouth(node);
//        Node west = mazeNodeBuilder.getWest(node);
        //        System.out.println("north bucket number is: " + north.getBucketNumber());
//        System.out.println("east bucket number is: " + east.getBucketNumber());
//        System.out.println("south number is: " + south.getBucketNumber());
//        System.out.println("west bucket number is: " + west.getBucketNumber());











    }
}
