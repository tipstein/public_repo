package assignment07.MazeBuilder;

import assignment07.NodeBuilder.Node;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MazeReader {

    /***
     * Takes in a file and turns each character into a node
     * Step 1: read in the maze and make sure each thing is stored in an array of objects
     *
     */
    private String mapInCharacters_;
    String dimensionString_;
    String mapString_;
    String mapInEntirety_;
    int xAxisLength_;
    int yAxisLength_;
    int[]dimensionArray_;
    private ArrayList mapArray_;
    private static int dimensionCount_;
    ArrayList<Node>nodeArrayList_;

    /***
     * Constructor: reads in maze file and creates two arrays:
     * a dimension array for maze size/dimensions
     * a map array of the actual maze
     * @param text
     * @throws IOException
     */
    public MazeReader(File text) throws IOException {

        //Reading each line of the file using Scanner class
        dimensionString_ = "";
        mapString_="";

        try (FileInputStream dimensionStream = new FileInputStream(text)) {

            //51 = 3
            // 55 = 7
            //88 = X
            //32 = space
            //10 = line break

            int i;
            //reads in file character by character
            while ((i = dimensionStream.read()) != 10) {
                dimensionString_ += i;
            }
            //initializes dimensionCount_; after parseDimensions, count should be 4;
            dimensionCount_ = 0;
            //fills member variable dimensionArray_
            dimensionArray_ = parseDimensions(dimensionString_);

            //fills in member variable mapString (map minus dimensions)
            int addCount = 0;
            while ((i = dimensionStream.read()) != -1) {
                addCount++;
                if (i != 10) {
                    mapString_ += i;
                }
            }
        }
    }


    /***
     * Takes the dimension portion of the file and creates the numerical equivalent
     * @param dimensionString
     * @return
     */
    private int[] parseDimensions(String dimensionString) {
       dimensionArray_ = new int[20];
        int x = 0;
        int dimensionValue;

        while ( x != dimensionString.length() ) {

            //get the substring, i.e. 55, which is the ascii val of 7
            String subString = dimensionString.substring(x, x + 2);

            //does not add the ' ' to the arrayList
            if (subString.contains("32")) {
                dimensionValue = 0;
            }

            else {

                //turn the String into a number, i.e. "55" is now 55
                int subStringToInt = Integer.parseInt(subString);

                //find the ascii val at the number, i.e. 55 becomes char 7
                char backToChar = (char) subStringToInt;

                //turn the 7 into a "7"
                String nowToString = "" + backToChar;

                dimensionValue= Integer.parseInt(nowToString);
                dimensionArray_[dimensionCount_] = dimensionValue;
                dimensionCount_++;
            }
            x += 2;
        }
        return dimensionArray_;
    }


    /***
     * transforms a string like "123" to 100 + 20 + 3 == final int value
     */
    public void transformDimensions() {
        xAxisLength_ = 0;
        yAxisLength_ = 0;
        int initialValue = 0;

        //y axis
        for (int i=dimensionCount_/2 -1; i > -1; i--) {
           int power = (int) Math.pow(10, initialValue);
            int valToAdd = (int) (dimensionArray_[i] * power);
            yAxisLength_ += valToAdd;
            initialValue++;
        }

        //x axis
        initialValue = 0;
        for (int i = dimensionCount_-1; i > dimensionCount_/2-1; i--) {
            xAxisLength_ += (int) (dimensionArray_[i]) * Math.pow(10, initialValue);
            initialValue++;
        }

    }

    public String getMap() {
        return mapString_;
    }

    public int getXAxisLength() {
        return xAxisLength_;
    }

    public int getYAxisLength() {
        return yAxisLength_;
    }

    public void printDimensions() {
        for (int i = 0; i < dimensionCount_; i++) {
            System.out.println(dimensionArray_[i]);
        }
    }

    public int getDimensionCount() {
        return dimensionCount_;
    }

    public int getGridTotal(){
        return xAxisLength_ * yAxisLength_;
    }


}

