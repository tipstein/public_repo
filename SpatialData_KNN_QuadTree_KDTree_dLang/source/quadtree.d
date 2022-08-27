module quadtree;

import common;
import std.container : SList;

struct QuadTree {
//threshold = most number of points in a leaf node
    
    alias PT = Point!2; 
    PT[] points;
    int threshold;
    Node root;
    

    this(PT[]points, int threshold){
        //copy the incoming array.  Without dup we're referencing the same array that was passed in.
        this.threshold = threshold;
        this.points = points.dup;
        
        AABB!2 root_box;
        root_box.min[] = [-100, -100];
        root_box.max[] = [100, 100];
        this.root = new Node( points, root_box);

    }

    PT[] rangeQuery( PT p, float r ){
        PT[] ret;
        auto minR = p - r;
        auto maxR = p + r;

        void recurse(Node n){
            if(!(minR[0] >= n.aabb.max[0] || maxR[1] >= n.aabb.min[1] || maxR[0] <= n.aabb.min[0] || minR[1] <= n.aabb.max[1]))
            {
                if(n.leaf){
                    foreach(const ref q; n.data){
                        if(distance(p, q) < r){
                            ret ~= q;
                        }
                    }
                }
                else{
                    recurse(n.nw);
                    recurse(n.ne);
                    recurse(n.sw);
                    recurse(n.se);
                }
            }
        }
        recurse( root );
        return ret;
    }

    class Node { 
        AABB!2 aabb;
        alias PT = Point!2;
        PT[] data;
        
        Node nw;
        Node ne;
        Node sw;
        Node se;
        
        bool leaf;

        this( PT[]data, AABB!2 box) {
            //writeln( "constructing node");
            int thresh = 4;
            //writeln( "data: ", data);
            if( data.length > thresh) {
                this.data = data;
                this.leaf = false;                
                node_constructor(data, box);
            }

            else {
                this.data = data;
                this.leaf = true;
            }
            

        }

        
        void node_constructor (PT[]data, AABB!2 node_box) {

           //split the box and send in as a param

            float[] max_corner = node_box.max;
            float[] min_corner = node_box.min;

            float x = min_corner[ 0 ];
            float y = min_corner[ 1 ];
            float a = max_corner[ 0 ];
            float b = max_corner[ 1 ];

            float[2] mid_left = [ x, (y+b)/2  ];
            float[2] mid_right = [ a, (b+y)/2 ];
            float[2] midpoint = [ (x+a)/2, (x+b)/2 ];
            float[2] top_mid = [ (x+a)/2, b ];
            float[2] bot_mid = [ (x+a)/2, y  ];
            float[2] small = min_corner;
            float[2] large = max_corner;

            auto ml = Point!2( mid_left );
            auto mr = Point!2( mid_right );
            auto mp = Point!2( midpoint );
            auto tm = Point!2 ( top_mid );
            auto bm = Point!2 ( bot_mid );
            auto tiny = Point!2 ( small );
            auto big = Point!2 ( large );
           
           
            auto NE_points = [ big, mp];
            auto NW_points = [ ml, tm ];
            auto SE_points = [ mr, bm ];
            auto SW_points = [ tiny, mp ];

            AABB!2 NE = boundingBox( NE_points );
            AABB!2 SE = boundingBox( SE_points );
            AABB!2 NW = boundingBox( NW_points );
            AABB!2 SW = boundingBox( SW_points );        
            
            // writeln (NE);
            // writeln (SE);
            // writeln (SW);
            // writeln (SE);
            
            float x_split = (x+a)/2;
            float y_split = (y+b)/2;
            PT[] rightsideX = partitionByDimension!(0, 2)(data, x_split);
            PT[] leftsideX = data[0 .. $ - rightsideX.length];

            //writeln (rightsideX);

            PT[] pNE = partitionByDimension!(1, 2)( rightsideX, y_split);
            
            
            this.ne = new Node (pNE, NE); 
            //writeln( "got to here");
            // writeln("northeast data: ", pNE );
            
            PT[] pSE = rightsideX[ 0 ..$ - pNE.length ];
            this.se = new Node ( pSE, SE);
            // writeln("southeast: ", pSE );
            // writeln("length: ", pSE.length );
            
            
            PT[] pNW = partitionByDimension!(1, 2)( leftsideX, y_split);
            this.nw = new Node ( pNW, NW);
            // writeln("northwest: ", pNW );
            // writeln("length: ", pNW.length );
            
            
            PT[] pSW = leftsideX[ 0 ..$ - pNW.length ];
            this.sw = new Node ( pSW, SW);
            // writeln("southwest: ", pSW );
            // writeln("length: ", pSW.length );
    
        }
    
}


    

}

