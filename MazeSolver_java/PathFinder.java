package assignment07;

import assignment07.MazeBuilder.MazeReader;
import assignment07.MazeRunner.MazeRunner;
import assignment07.NodeBuilder.MazeNodeBuilder;
import assignment07.NodeBuilder.Node;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class PathFinder {

    public static void solveMaze(String inputFile, String outputFile) throws IOException, NoSuchFieldException {

        MazeReader mazeReader = new MazeReader(new File(inputFile));
        //set the xAxisLength and yAxisLength values as ints
        mazeReader.transformDimensions();
        int xAxis = mazeReader.getXAxisLength();
        int yAxis = mazeReader.getYAxisLength();
        MazeNodeBuilder mazeNodeBuilder = new MazeNodeBuilder(xAxis, yAxis);
        String map = mazeReader.getMap();
        mazeNodeBuilder.setNodeValues(map);
        mazeNodeBuilder.setExes();
        MazeRunner mazeRunner = new MazeRunner(mazeNodeBuilder);
        Node node = mazeRunner.breadthFirstSearch();
        Node goal = mazeNodeBuilder.findGoal();
        LinkedList<Node> list = mazeRunner.getSolutionList(goal);
        System.out.println("List of index numbers indicating solutions: ");
        for (Node n: list) {
            System.out.println(n.getBucketNumber());
            int bucketIndex = n.getBucketNumber();
            Node xNode = mazeNodeBuilder.getNodeByBucket(bucketIndex);
            xNode.setValue();
        }
//        mazeNodeBuilder.printNodeValues();
        String solutionString = mazeNodeBuilder.stringNodeValues();
        System.out.println(solutionString);
        mazeNodeBuilder.breakSolutionString();

    }

    public static void main(String[] args) throws IOException, NoSuchFieldException {
        solveMaze("src/assignment07/Mazes/mazes/bigMaze.txt", "src/assignment07/Mazes/mazes/bigMaze.txt");
    }

}
