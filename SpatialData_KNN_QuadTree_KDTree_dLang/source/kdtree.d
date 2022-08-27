module kdtree;

import common;
import std.container : SList;

struct KDtree(size_t dim) {
    //threshold = most number of points in a leaf node

    alias PT = Point!dim;
    PT[] points;
    int threshold;
    Node!0 root;


    this(PT[]points, int threshold){
        //copy the incoming array.  Without dup we're referencing the same array that was passed in.
        this.threshold = threshold;
        this.points = points.dup;

        AABB!dim root_box = boundingBox!dim(points);
        this.root = new Node!0( points, root_box);

    }

    PT[] rangeQuery( PT p, float r ){
        PT[] ret;
        PT minR = p - r;
        PT maxR = p + r;

        void recurse( NodeType )( NodeType n ){
            if(!(minR[n.thisLevel] >= n.aabb.max[n.thisLevel] || maxR[n.thisLevel] <= n.aabb.min[n.thisLevel]))
            {
                if(n.leaf){
                    foreach(const ref q; n.data){
                        if(distance(p, q) < r){
                            ret ~= q;
                        }
                    }
                }
                else{
                    recurse(n.left);
                    recurse(n.right);
                }
            }
        }
        recurse( root );
        return ret;
    }

    class Node( size_t splitdimension ) {
        AABB!dim aabb;
        alias PT = Point!dim;
        PT[] data;

        enum thisLevel = splitdimension;
        enum nextLevel = (splitdimension + 1) % dim;
        Node!nextLevel left, right;

        bool leaf;

        this(PT[] data, AABB!dim box) {
            //writeln( "constructing node");
            int thresh = 4;
          
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


        void node_constructor (PT[]data, AABB!dim node_box) {

            //split the box and send in as a param

            //writeln("this box ", nextLevel);

            float[dim] max_corner = node_box.max;
            float[dim] min_corner = node_box.min;

            //writeln("this is  ", node_box.max);

            float min = min_corner[ thisLevel ];
            float max = max_corner[ thisLevel ];

            float mid = (max+min)/2;

            auto box1Min = Point!dim( min_corner );

            auto box2Max = Point!dim( max_corner );

            max_corner[thisLevel] = mid;
            auto box1Max = Point!dim( max_corner );

            min_corner[thisLevel] = mid;
            auto box2Min = Point!dim( min_corner );

            auto left_p = [ box1Min, box1Max];
            auto right_p = [ box2Min, box2Max ];

            AABB!dim left_box = boundingBox( left_p );
            AABB!dim right_box = boundingBox( right_p );
            //writeln("left box it : ", left_box);
            //writeln("right box it : ", right_box);

            PT[] rightside_dat = partitionByDimension!(thisLevel, dim)(data, mid);
            //writeln("right ", rightside_dat);
            this.right = new Node!nextLevel(rightside_dat, right_box);
            //writeln("dat ", right_box);

            PT[] leftside_dat = data[0 .. $ - rightside_dat.length];
            this.left = new Node!nextLevel(leftside_dat, left_box);
        }

    }
}


unittest{
    auto bknn = KDtree!2([Point!2([.5, .5]), Point!2([1, 1]),
    Point!2([0.75, 0.4]), Point!2([0.4, 0.74])],
    2);

    writeln("bucket range query");
    foreach(p; bknn.rangeQuery(Point!2([1,1]), .7)){
        writeln(p);
    }
}
