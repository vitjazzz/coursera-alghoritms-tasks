package percolation;

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

    private final double mean;
    private final double stddev;
    private final double confidenceLo;
    private final double confidenceHi;

    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0){
            throw new IllegalArgumentException("Grid's side size and 'trials' must be greater then 0.");
        }
        double[] openedSitesToN = new double[trials];

        for (int i = 0; i < trials; i++) {
            Percolation percolation = new Percolation(n);
            do {
                int row = StdRandom.uniform(1, n + 1);
                int col = StdRandom.uniform(1, n + 1);
                percolation.open(row, col);
            } while (!percolation.percolates());
            openedSitesToN[i] = (double) percolation.numberOfOpenSites() / (n * n);
        }

        this.mean = StdStats.mean(openedSitesToN);
        this.stddev = StdStats.stddev(openedSitesToN);
        double confidenceDelta = 1.96 * stddev / Math.sqrt(trials);
        this.confidenceLo = mean - confidenceDelta;
        this.confidenceHi = mean + confidenceDelta;
    }

    public double mean() {
        return mean;
    }

    public double stddev() {
        return stddev;
    }

    public double confidenceLo() {
        return confidenceLo;
    }

    public double confidenceHi() {
        return confidenceHi;
    }

    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);
        PercolationStats percolationStats = new PercolationStats(n, trials);
        System.out.println("mean\t\t\t\t\t= " + percolationStats.mean());
        System.out.println("stddev\t\t\t\t\t= " + percolationStats.stddev());
        System.out.println("95% confidence interval = [" + percolationStats.confidenceLo() + ", " +
                percolationStats.confidenceHi() + "]");
    }
}
