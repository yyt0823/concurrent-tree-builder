import java.awt.image.*;
import java.io.*;
import javax.imageio.*;


import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.geom.*;
import java.util.concurrent.*;                                                                                                                                        


public class q1 {
    // parameters and their default values
    public static int threads = 1; // number of threads to use
    public static int n = 100; // number of nodes
    public static int b = 3; // branching factor
    public static double r = 0.05; // max dist
    public static int w = 2048; // output image height
    public static int h = 2048; // output image width
    public static long seed = 42; // random seed for reproducibility

    public static final int RADIUS = 3; // circle radius in drawing
    public static final List<Obstacle> obstacles = new ArrayList<>(); // list of obstacles (we'll fill this in later)

    // output image
    public static BufferedImage imgout;

    // print out command-line parameter help and exit
    public static void help(String s) {
        System.out.println("Could not parse argument \""+s+"\".  Please use only the following arguments:");
        System.out.println(" -w output image width (integer; current=\""+w+"\")");
        System.out.println(" -h output image height (integer; current=\""+h+"\")");
        System.out.println(" -t threads (integer value >=1; current=\""+threads+"\")");
        System.out.println(" -n nodes (integer value >1; current=\""+n+"\")");
        System.out.println(" -b branching factor (integer value >1; current=\""+b+"\")");
        System.out.println(" -r max segment distance (double value in (0.0,1.0); current=\""+r+"\")");
        System.exit(1);
    }

    // process command-line options
    public static void opts(String[] args) {
        int i = 0;

        try {
            for (;i<args.length;i++) {
                if (i==args.length-1)
                    help(args[i]);
                if (args[i].equals("-h")) {
                    w = Integer.parseInt(args[i+1]);
                } else if (args[i].equals("-w")) {
                    h = Integer.parseInt(args[i+1]);
                } else if (args[i].equals("-t")) {
                    threads = Integer.parseInt(args[i+1]);
                } else if (args[i].equals("-n")) {
                    n = Integer.parseInt(args[i+1]);
                } else if (args[i].equals("-b")) {
                    b = Integer.parseInt(args[i+1]);
                } else if (args[i].equals("-r")) {
                    r = Double.parseDouble(args[i+1]);
                } else if (args[i].equals("-s")) {
                    seed = Long.parseLong(args[i+1]);
                } else {
                    help(args[i]);
                }
                // an extra increment since our options consist of 2 pieces
                i++;
            }
        } catch (Exception e) {
            System.err.println(e);
            help(args[i]);
        }
    }

    // main.  we allow an IOException in case the image loading/storing fails.
    public static void main(String[] args) throws IOException, InterruptedException {
        /* 
        starter code and api examples

        // process options
        opts(args);

        // create an output image
        BufferedImage outputimage = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);

        // example of drawing---substitute with your own code if you want
        Graphics2D g = (Graphics2D) outputimage.getGraphics();
        // draw a node
        g.setColor(Color.BLACK);
        g.fillOval(w/2-RADIUS,h/2-RADIUS,RADIUS*2,RADIUS*2);

        // draw an edge
        g.setColor(Color.RED);
        g.drawLine(0,0,w/2-5,h/2-5);

        // Write out the image
        File outputfile = new File("outputimage.png");
        ImageIO.write(outputimage, "png", outputfile);
        */

        //parse
        opts(args);
        Random rng = new Random(seed);
        //make image
        BufferedImage outputimage = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        // init "brush tools"
        Graphics2D g = (Graphics2D) outputimage.getGraphics();
        // choose a color for drawing
        g.setColor(Color.BLUE);
        // draw the outer polygon
        g.drawRect(0, 0, w-1, h-1);
        // draw the obstacles: each is 0.05 x 0.05 in unit coords, strictly inside boundary
        g.setColor(Color.GREEN);
        double obsUnitSize = 0.05; // obstacle size in unit coords
        for (int i = 0; i < 20; i++) {
            // generate top-left corner in unit coords, strictly within (0,1)
            double ux = rng.nextDouble() * (1.0 - obsUnitSize);
            double uy = rng.nextDouble() * (1.0 - obsUnitSize);
            obstacles.add(new Obstacle(ux, uy, obsUnitSize));
            // convert to pixels for drawing
            int px = (int)(ux * w);
            int py = (int)(uy * h);
            int pSize = (int)(obsUnitSize * w);
            g.fillRect(px, py, pSize, pSize);
        }

        // pick root node and and init the tree
        double rootX, rootY;
        do {
            rootX = rng.nextDouble();
            rootY = rng.nextDouble();
        } while (inAnyObstacle(rootX, rootY));
        Tree tree = new Tree(rootX, rootY);

        // make the thread pool
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        pool.submit(new TreeTask(tree, tree.root, pool, rng));
        pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        // draw tree edges
        g.setColor(Color.RED);
        for (Tree.Node nd : tree.allNodes) {
            int nx = (int)(nd.x * w);
            int ny = (int)(nd.y * h);
            for (Tree.Node neighbor : nd.neighbors) {
                int mx = (int)(neighbor.x * w);
                int my = (int)(neighbor.y * h);
                g.drawLine(nx, ny, mx, my);
            }
        }
        // draw tree nodes
        g.setColor(Color.BLACK);
        for (Tree.Node nd : tree.allNodes) {
            int nx = (int)(nd.x * w);
            int ny = (int)(nd.y * h);
            g.fillOval(nx - RADIUS, ny - RADIUS, RADIUS * 2, RADIUS * 2);
        }

























        // Write out the image
        File outputfile = new File("outputimage.png");
        ImageIO.write(outputimage, "png", outputfile);




        

        
    }

    // check if not collide with any obstacles
    static boolean inAnyObstacle(double x, double y) {
        for (Obstacle obs : obstacles) {
            if (obs.contains(x, y)) return true;
        }
        return false;
    }

    // check if in outer polygon
    static boolean inBounds(double x, double y) {
        return x > 0 && x < 1 && y > 0 && y < 1;
    }

    // check for distance
    static double distance(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx*dx + dy*dy);
    }

    // basically the same line intersection check as in comp521 modern game design
    static boolean edgeIntersectsAny(double x1, double y1, double x2, double y2) {
        // check against each obstacle's 4 edges
        for (Obstacle obs : obstacles) {
            double ox = obs.x, oy = obs.y, os = obs.size;
            if (Line2D.linesIntersect(x1,y1,x2,y2, ox,oy, ox+os,oy))       return true; // top
            if (Line2D.linesIntersect(x1,y1,x2,y2, ox+os,oy, ox+os,oy+os)) return true; // right
            if (Line2D.linesIntersect(x1,y1,x2,y2, ox,oy+os, ox+os,oy+os)) return true; // bottom
            if (Line2D.linesIntersect(x1,y1,x2,y2, ox,oy, ox,oy+os))       return true; // left
        }
        return false;
    }

    // runnable for tree construction task
    static class TreeTask implements Runnable {
        Tree tree;
        Tree.Node node;
        ExecutorService pool;
        Random rng;
        TreeTask(Tree tree, Tree.Node node, ExecutorService pool, Random rng) {
            this.tree = tree;
            this.node = node;
            this.pool = pool;
            this.rng = new Random(rng.nextLong()); // each task gets its own Random
        }

        public void run() {
            //todo: implement tree construction and drawing here
            // check if tree is complete already
            synchronized (tree) {
                if (tree.allNodes.size() >= n) {
                    return; 
                }
            }
            // otherwise, try to add a new node
            Tree.Node newNode = null; 
            while (true) {
                double newX = rng.nextDouble();
                double newY = rng.nextDouble();
                double dist = distance(node.x, node.y, newX, newY);
                // check for validity
                if (!inBounds(newX, newY) || inAnyObstacle(newX, newY) || (dist <= 0 || dist >= r)) {
                    continue; 
                }
                // check if can draw an edge
                if (edgeIntersectsAny(node.x, node.y, newX, newY)) {
                    continue; 
                }
                // if valid, add the new node and edge to the tree and draw them
                newNode = tree.new Node(newX, newY);
                synchronized (tree) {
                    if (tree.allNodes.size() >= n) {
                        pool.shutdown();
                        return;
                    }
                    if (node.neighbors.size() >= b) {
                        return;
                    }
                    tree.allNodes.add(newNode);
                    node.neighbors.add(newNode);
                    newNode.neighbors.add(node);
                    if (tree.allNodes.size() >= n) {
                        pool.shutdown();
                    }
                }
                break;
            }

            if (newNode != null && !pool.isShutdown()) {
                pool.submit(new TreeTask(tree, newNode, pool, rng));                                                                                                              
            }                                                                                                                 
            synchronized (tree) {                                                                                                                                                 
                if (node.neighbors.size() < b && !pool.isShutdown()) {                                                                                                            
                    pool.submit(new TreeTask(tree, node, pool, rng));                                                                                                             
                }                                                                                                                                                                 
            } 



            



        }
    }

    // tree data structure
    static class Tree {
        Node root;
        List<Node> allNodes;
        Tree(double x, double y) {
            this.root = new Node(x, y);
            this.allNodes = new ArrayList<>();
            this.allNodes.add(this.root);
        }

        class Node {
            double x;
            double y;
            List<Node> neighbors;
            Node(double x, double y) {
                this.x = x;
                this.y = y;
                this.neighbors = new ArrayList<>();
            }
        }
    }

    static class Obstacle {
        double x, y, size; // top-left corner and size, all in unit coords (0 to 1)

        Obstacle(double x, double y, double size) {
            this.x = x;
            this.y = y;
            this.size = size;
        }

        // returns true if point (px, py) is inside or on the boundary of this obstacle
        boolean contains(double px, double py) {
            return px >= x && px <= x + size && py >= y && py <= y + size;
        }
    }




   
}
