import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class TimingChart {
    // timing data: threads -> {avg, min, max}
    static int[] threads = {1, 2, 4, 8, 16};
    static double[] avg   = {77, 94, 119, 131, 238};
    static double[] min   = {72, 88, 100, 120, 192};
    static double[] max   = {84, 99, 132, 183, 277};

    static int W = 900, H = 600;
    static int padL = 80, padR = 40, padT = 60, padB = 60;

    public static void main(String[] args) throws IOException {
        drawTimeChart();
        drawSpeedupChart();
    }

    static void drawTimeChart() throws IOException {
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, W, H);

        // title
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        g.drawString("Total Time vs Number of Threads", W/2 - 160, 35);

        // axis labels
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.drawString("Number of Threads", W/2 - 70, H - 10);
        // y axis label (rotated)
        Graphics2D g2 = (Graphics2D) g.create();
        g2.rotate(-Math.PI/2, 20, H/2);
        g2.drawString("Total Time (ms)", -30, H/2 + 5);
        g2.dispose();

        double maxVal = 280;
        double minVal = 0;

        // grid lines
        g.setColor(new Color(220, 220, 220));
        for (int v = 0; v <= 280; v += 40) {
            int y = toY(v, minVal, maxVal);
            g.drawLine(padL, y, W - padR, y);
            g.setColor(Color.DARK_GRAY);
            g.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g.drawString("" + v, padL - 35, y + 4);
            g.setColor(new Color(220, 220, 220));
        }

        // axes
        g.setColor(Color.BLACK);
        g.drawLine(padL, padT, padL, H - padB);
        g.drawLine(padL, H - padB, W - padR, H - padB);

        // x axis labels
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        for (int i = 0; i < threads.length; i++) {
            int x = toX(i);
            g.drawString("" + threads[i], x - 5, H - padB + 20);
        }

        // error bars + line
        g.setColor(new Color(31, 119, 180));
        g.setStroke(new BasicStroke(2.5f));
        int[] xs = new int[threads.length];
        int[] ys = new int[threads.length];
        for (int i = 0; i < threads.length; i++) {
            xs[i] = toX(i);
            ys[i] = toY(avg[i], minVal, maxVal);
        }
        g.drawPolyline(xs, ys, threads.length);

        // error bars
        g.setStroke(new BasicStroke(1.5f));
        for (int i = 0; i < threads.length; i++) {
            int x = toX(i);
            int yMin = toY(min[i], minVal, maxVal);
            int yMax = toY(max[i], minVal, maxVal);
            int yAvg = toY(avg[i], minVal, maxVal);
            g.drawLine(x, yMin, x, yMax);
            g.drawLine(x - 5, yMin, x + 5, yMin);
            g.drawLine(x - 5, yMax, x + 5, yMax);
            // dot
            g.fillOval(x - 5, yAvg - 5, 10, 10);
        }

        ImageIO.write(img, "png", new File("timing_chart.png"));
        System.out.println("Saved timing_chart.png");
    }

    static void drawSpeedupChart() throws IOException {
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, W, H);

        // title
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        g.drawString("Relative Speedup vs Number of Threads", W/2 - 190, 35);

        // axis labels
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.drawString("Number of Threads", W/2 - 70, H - 10);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.rotate(-Math.PI/2, 20, H/2);
        g2.drawString("Relative Speedup (T1/Tn)", -60, H/2 + 5);
        g2.dispose();

        double maxVal = threads.length + 0.5;
        double minVal = 0;

        // grid lines
        g.setColor(new Color(220, 220, 220));
        for (int v = 0; v <= threads.length; v++) {
            int y = toY(v, minVal, maxVal);
            g.drawLine(padL, y, W - padR, y);
            g.setColor(Color.DARK_GRAY);
            g.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g.drawString("" + v, padL - 20, y + 4);
            g.setColor(new Color(220, 220, 220));
        }

        // axes
        g.setColor(Color.BLACK);
        g.drawLine(padL, padT, padL, H - padB);
        g.drawLine(padL, H - padB, W - padR, H - padB);

        // x labels
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        for (int i = 0; i < threads.length; i++) {
            int x = toX(i);
            g.drawString("" + threads[i], x - 5, H - padB + 20);
        }

        // ideal speedup (dashed red)
        g.setColor(Color.RED);
        float[] dash = {8f, 6f};
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dash, 0f));
        int[] idealXs = new int[threads.length];
        int[] idealYs = new int[threads.length];
        for (int i = 0; i < threads.length; i++) {
            idealXs[i] = toX(i);
            idealYs[i] = toY(threads[i], minVal, maxVal);
        }
        g.drawPolyline(idealXs, idealYs, threads.length);

        // actual speedup (blue)
        g.setColor(new Color(31, 119, 180));
        g.setStroke(new BasicStroke(2.5f));
        int[] xs = new int[threads.length];
        int[] ys = new int[threads.length];
        for (int i = 0; i < threads.length; i++) {
            xs[i] = toX(i);
            double speedup = avg[0] / avg[i];
            ys[i] = toY(speedup, minVal, maxVal);
        }
        g.drawPolyline(xs, ys, threads.length);
        for (int i = 0; i < threads.length; i++) {
            g.fillOval(xs[i] - 5, ys[i] - 5, 10, 10);
        }

        // legend
        int lx = W - padR - 160, ly = H - padB - 60;
        g.setColor(new Color(31, 119, 180));
        g.setStroke(new BasicStroke(2.5f));
        g.drawLine(lx, ly, lx + 30, ly);
        g.fillOval(lx + 10, ly - 5, 10, 10);
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString("Actual Speedup", lx + 35, ly + 4);

        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dash, 0f));
        g.drawLine(lx, ly + 20, lx + 30, ly + 20);
        g.setColor(Color.BLACK);
        g.drawString("Ideal Speedup", lx + 35, ly + 24);

        ImageIO.write(img, "png", new File("speedup_chart.png"));
        System.out.println("Saved speedup_chart.png");
    }

    static int toX(int i) {
        int plotW = W - padL - padR;
        return padL + (i * plotW) / (threads.length - 1);
    }

    static int toY(double val, double minVal, double maxVal) {
        int plotH = H - padT - padB;
        return H - padB - (int)((val - minVal) / (maxVal - minVal) * plotH);
    }
}
