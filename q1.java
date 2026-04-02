import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.util.*;
import java.util.List;
import java.awt.*;

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
    public static void main(String[] args) throws IOException {
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
        int obsSize = (int)(0.05 * w); // obstacle size in pixels
        for (int i = 0; i < 20; i++) {
            // keep obstacle strictly within boundary (at least 1px from edge)
            int x = 1 + (int)(rng.nextDouble() * (w - obsSize - 2));
            int y = 1 + (int)(rng.nextDouble() * (h - obsSize - 2));
            obstacles.add(new Obstacle(x, y));

            g.fillRect(x, y, obsSize, obsSize);
        }

        //todo: run the tree construction and drawing here
        Tree tree;
        while (true){
            double x = rng.nextDouble();
            double y = rng.nextDouble();
            
        }
        























        // Write out the image
        File outputfile = new File("outputimage.png");
        ImageIO.write(outputimage, "png", outputfile);




        

        
    }

    // runnable for tree construction task
    static class TreeTask implements Runnable {

        public void run() {
            //todo: implement tree construction and drawing here
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
        double t,l,b,r;
        double size = 0.05 * w; // obstacle size in pixels
        Obstacle(double x, double y) {
            this.t = x + size;
            this.l = y + size;
            this.b = y - size;
            this.r = x - size; 
        }

        boolean contains(double x, double y) {
            return x >= l && x <= r && y >= b && y <= t;
        }
        
    }




   
}
