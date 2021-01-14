package percolation;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;


public class Percolation {
    private WeightedQuickUnionUF quickUnionUF;

    // 0 - closed, 1 - opened
    // array numeration starts from 1
    private boolean[][] percolationMatrix;
    private final int size;
    private final int sideSize;
    private int openedSites;

    private final int imaginaryTopSiteIndex;
    private final int imaginaryBottomSiteIndex;

    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Number cannot be <= 0.");
        }
        // add 2 for "imaginary" sites at the "top" and "bottom"
        int arraySize = n * n;
        this.sideSize = n;
        this.quickUnionUF = new WeightedQuickUnionUF(arraySize + 2);
        this.percolationMatrix = new boolean[n][n];
        this.size = n;
        this.imaginaryTopSiteIndex = arraySize;
        this.imaginaryBottomSiteIndex = arraySize + 1;
    }

    public void open(int row, int col) {
        if (isOpen(row, col))
            return;

        int rowIndex = row - 1;
        int colIndex = col - 1;

        percolationMatrix[rowIndex][colIndex] = true;

        int selectedIndex = rowIndex * sideSize + colIndex;

        if (rowIndex == 0) {
            quickUnionUF.union(selectedIndex, imaginaryTopSiteIndex);
        }
        if (rowIndex == sideSize - 1) {
            quickUnionUF.union(selectedIndex, imaginaryBottomSiteIndex);
        }

        if (rowIndex != 0) {
            unionOpenedSites(selectedIndex, rowIndex - 1, colIndex);
        }
        if (rowIndex != sideSize - 1) {
            unionOpenedSites(selectedIndex, rowIndex + 1, colIndex);
        }
        if (colIndex != 0) {
            unionOpenedSites(selectedIndex, rowIndex, colIndex - 1);
        }
        if (colIndex != sideSize - 1) {
            unionOpenedSites(selectedIndex, rowIndex, colIndex + 1);
        }

        openedSites++;
    }

    private void unionOpenedSites(int selectedSiteIndex, int rowIndex, int colIndex) {
        if (percolationMatrix[rowIndex][colIndex]) {
            int openedSiteIndex = rowIndex * sideSize + colIndex;
            quickUnionUF.union(selectedSiteIndex, openedSiteIndex);
        }
    }

    public boolean isOpen(int row, int col) {
        if (isOutsideOfRange(row) || isOutsideOfRange(col))
            throw new IllegalArgumentException("Row or col is outside of valid range.");

        return percolationMatrix[row - 1][col - 1];
    }

    public boolean isFull(int row, int col) {
        if (!isOpen(row, col))
            return false;

        int selectedSiteIndex = (row - 1) * sideSize + (col - 1);

        return quickUnionUF.find(selectedSiteIndex) == quickUnionUF.find(imaginaryTopSiteIndex);
    }

    public int numberOfOpenSites() {
        return openedSites;
    }

    public boolean percolates() {
        return quickUnionUF.find(imaginaryBottomSiteIndex) == quickUnionUF.find(imaginaryTopSiteIndex);
    }

    private boolean isOutsideOfRange(int n) {
        return n < 1 || n > size;
    }
}
