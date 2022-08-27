import std.stdio;
import std.csv;
import common;
import dumbknn;
import bucketknn;
import quadtree;
import kdtree;
import std.file;
import core.stdc.errno;
import std.exception;

//import your files here
void main()
{
    long[int] dumbKNNdict;
    long[int] bucketKNNdict;
    long[int] quadKNNdict;
    long[int] KDTreedict;
    
    
    auto outputs = File("/Users/adamweinstein/MSD/myGitRepo/CS6017/homework/HW4_spatial_data/source/outputs.csv", "w");
    
    // catch (ErrnoException ex)
    // {
    //     switch(ex.errno)
    //     {
    //         case EPERM:
    //         case EACCES:
    //             // Permission denied
    //             break;
 
    //         case ENOENT:
    //             // File does not exist
    //             break;
 
    //         default:
    //             // Handle other errors
    //             break;
 
    //     }
    // }
    


    //because dim is a "compile time parameter" we have to use "static foreach"
    //to loop through all the dimensions we want to test.
    //the {{ are necessary because this block basically gets copy/pasted with
    //dim filled in with 1, 2, 3, ... 7.  The second set of { lets us reuse
    //variable names.


    //output the time elapsed in microseconds
    //NOTE, I SOMETIMES GOT TOTALLY BOGUS TIMES WHEN TESTING WITH DMD
    //WHEN YOU TEST WITH LDC, YOU SHOULD GET ACCURATE TIMING INFO...

     writeln("dumbKNN results");
     outputs.writeln( "dumb," );
     static foreach(dim; 1..8){{
         //get points of the appropriate dimension
         auto trainingPoints = getGaussianPoints!dim(1000);
         auto testingPoints = getUniformPoints!dim(100);
         auto kd = DumbKNN!dim(trainingPoints);
         //writeln("tree of dimension ", dim, " built");
         auto sw = StopWatch(AutoStart.no);
         sw.start; //start my stopwatch
        
         foreach(const ref qp; testingPoints){
             auto x = kd.knnQuery(qp, 10);   
         }
         sw.stop;
         //writeln(dim, sw.peek.total!"usecs"); //output the time elapsed in microseconds
         dumbKNNdict[dim] = sw.peek.total!"usecs";
         outputs.writeln(dim, sw.peek.total!"usecs", "," );
         //NOTE, I SOMETIMES GOT TOTALLY BOGUS TIMES WHEN TESTING WITH DMD
         //WHEN YOU TEST WITH LDC, YOU SHOULD GET ACCURATE TIMING INFO...
     }
    
     }


     writeln("BucketKNN results");
     outputs.writeln( "bucket," );
     
     //Same tests for the BucketKNN
     static foreach(dim; 1..8){{
         //get points of the appropriate dimension
         enum numTrainingPoints = 1000;
         auto trainingPoints = getGaussianPoints!dim(numTrainingPoints);
         auto testingPoints = getUniformPoints!dim(100);
         auto kd = BucketKNN!dim(trainingPoints, cast(int)pow(numTrainingPoints/64, 1.0/dim)); //rough estimate to get 64 points per cell on average
         //writeln("tree of dimension ", dim, " built");
         File file = File("Bucket.txt", "w");
         auto sw = StopWatch(AutoStart.no);
         sw.start; //start my stopwatch
         foreach(const ref qp; testingPoints){
             auto x = kd.knnQuery(qp, 10);
             
         }
         sw.stop;
         bucketKNNdict[dim] = sw.peek.total!"usecs";
         outputs.writeln(dim, sw.peek.total!"usecs", "," );
         //writeln(dim, sw.peek.total!"usecs");
         
          //output the time elapsed in microseconds
         //NOTE, I SOMETIMES GOT TOTALLY BOGUS TIMES WHEN TESTING WITH DMD
         //WHEN YOU TEST WITH LDC, YOU SHOULD GET ACCURATE TIMING INFO...
     }
     
     }

     writeln("QuadTree results");
     outputs.writeln( "quad," );
     
     static foreach(dim; 1..8){{
         //get points of the appropriate dimension
         auto trainingPoints = getGaussianPoints!2(1000);
         auto testingPoints = getUniformPoints!2(100);
         auto QT = QuadTree(trainingPoints, 4);
         //writeln("tree of dimension ", dim, " built");
         File file = File("Quad.txt", "w");
         auto sw = StopWatch(AutoStart.no);
         sw.start; //start my stopwatch
         foreach(const ref qp; testingPoints){
             auto x = QT.rangeQuery(qp, 10);
         }
         sw.stop;
         quadKNNdict[dim] = sw.peek.total!"usecs";
         outputs.writeln(dim, sw.peek.total!"usecs", "," );
         //writeln(2, sw.peek.total!"usecs"); //output the time elapsed in microseconds
         //NOTE, I SOMETIMES GOT TOTALLY BOGUS TIMES WHEN TESTING WITH DMD
         //WHEN YOU TEST WITH LDC, YOU SHOULD GET ACCURATE TIMING INFO...
     }
     }

     writeln("KDTree results");
     outputs.writeln( "kd," );
     static foreach(dim; 1..8){{
         //get points of the appropriate dimension

         //writeln("start: ", dim);
         auto trainingPoints = getGaussianPoints!dim(1000);
         auto testingPoints = getUniformPoints!dim(100);
         //writeln("dasjhdbjksndfkjwb", trainingPoints);
         auto KD = KDtree!dim(trainingPoints, 4);
         //writeln("tree of dimension ", dim, " built");
         File file = File("KD.txt", "w");
         auto sw = StopWatch(AutoStart.no);
         sw.start; //start my stopwatch
         foreach(const ref qp; testingPoints){
             auto x = KD.rangeQuery(qp, 10);
             
         }
         sw.stop;
         KDTreedict[dim] = sw.peek.total!"usecs";
         outputs.writeln(dim, sw.peek.total!"usecs", "," );
         //writeln(dim, sw.peek.total!"usecs"); //output the time elapsed in microseconds
         //NOTE, I SOMETIMES GOT TOTALLY BOGUS TIMES WHEN TESTING WITH DMD
         //WHEN YOU TEST WITH LDC, YOU SHOULD GET ACCURATE TIMING INFO...
     }
     
     }

}