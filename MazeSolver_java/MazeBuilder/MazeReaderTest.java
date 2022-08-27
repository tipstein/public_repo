package assignment07.MazeBuilder;

import assignment07.MazeBuilder.MazeReader;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class MazeReaderTest {

    @Test
    public void mazeReadTest() throws IOException {
        File file = new File("src/assignment07/Mazes/mazes/bigMaze.txt");
        MazeReader mazeReader = new MazeReader(new File("src/assignment07/Mazes/mazes/bigMaze.txt"));

    }

    @Test
    public void mazeDimensionTest() throws IOException {
//        MazeReader mazeReader1 = new MazeReader(new File("src/assignment07/Mazes/mazes/bigMaze.txt"));
//        assertEquals(51, mazeReader1.xDimension());
//        assertEquals(55, mazeReader1.yDimension());

//
//        MazeReader mazeReader2 = new MazeReader(new File("src/assignment07/Mazes/mazes/classic.txt"));
//        assertEquals(11, mazeReader2.xDimension());
//        assertEquals(20, mazeReader2.yDimension());
////
//
//        MazeReader mazeReader3 = new MazeReader(new File("src/assignment07/Mazes/mazes/demoMaze.txt"));
//        assertEquals(10, mazeReader3.xDimension());
//        assertEquals(19, mazeReader3.yDimension());
//
//
//        MazeReader mazeReader4 = new MazeReader(new File("src/assignment07/Mazes/mazes/mediumMaze.txt"));
//        assertEquals(18, mazeReader4.xDimension());
//        assertEquals(36, mazeReader4.yDimension());
//
//
//        MazeReader mazeReader5 = new MazeReader(new File("src/assignment07/Mazes/mazes/randomMaze.txt"));
//        assertEquals(100, mazeReader5.xDimension());
//        assertEquals(100, mazeReader5.yDimension());
//
//
//
//        MazeReader mazeReader6 = new MazeReader(new File("src/assignment07/Mazes/mazes/straight.txt"));
//        assertEquals(3, mazeReader6.xDimension());
//        assertEquals(10, mazeReader6.yDimension());
//
//
//
//        MazeReader mazeReader7 = new MazeReader(new File("src/assignment07/Mazes/mazes/tinyMaze.txt"));
//        assertEquals(7, mazeReader7.xDimension());
//        assertEquals(9, mazeReader7.yDimension());
//
//
//
//        MazeReader mazeReader8 = new MazeReader(new File("src/assignment07/Mazes/mazes/tinyOpen.txt"));
//        assertEquals(5, mazeReader8.xDimension());
//        assertEquals(5, mazeReader8.yDimension());
//
//
//
//        MazeReader mazeReader9 = new MazeReader(new File("src/assignment07/Mazes/mazes/turn.txt"));
//        assertEquals(30, mazeReader9.xDimension());
//        assertEquals(30, mazeReader9.yDimension());
//
//
//        MazeReader mazeReader10 = new MazeReader(new File("src/assignment07/Mazes/mazes/unsolvable.txt"));
//        assertEquals(18, mazeReader10.xDimension());
//        assertEquals(36, mazeReader10.yDimension());


    }



}