package seam;

import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.io.File;

public class SeamCarver {
    private static final double BORDER_ENERGY = 1000;
    private final Picture picture;

    private int width;
    private int height;

    private double[][] energyMatrix;
    private Color[][] pixelsMatrix;

    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException();
        }
        this.picture = picture;
        this.width = picture.width();
        this.height = picture.height();
        this.energyMatrix = new double[height][width];
        this.pixelsMatrix = new Color[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color color = picture.get(j, i);
                pixelsMatrix[i][j] = color;
            }
        }
        recalculateEnergyMatrix();
    }

    private double calculateEnergy(int i, int j) {
        double xGradient = calculateGradient(this.pixelsMatrix[i][j - 1], this.pixelsMatrix[i][j + 1]);
        double yGradient = calculateGradient(this.pixelsMatrix[i - 1][j], this.pixelsMatrix[i + 1][j]);
        return Math.sqrt(xGradient + yGradient);
    }

    private double calculateGradient(Color prev, Color next) {
        return Math.pow(prev.getRed() - next.getRed(), 2) +
                Math.pow(prev.getGreen() - next.getGreen(), 2) +
                Math.pow(prev.getBlue() - next.getBlue(), 2);
    }

    private boolean isBorder(int i, int j) {
        return i == 0 || j == 0 || i == height - 1 || j == width - 1;
    }

    public Picture picture() {
        Picture picture = new Picture(width, height);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                picture.set(j, i, pixelsMatrix[i][j]);
            }
        }
        return picture;

    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public double energy(int x, int y) {
        validateXCoordinate(x);
        validateYCoordinate(y);
        return energyMatrix[y][x];
    }

    public int[] findHorizontalSeam() {
        double[][] weights = new double[height][width];
        int[][] prevIndex = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                weights[i][j] = Double.MAX_VALUE;
            }
        }
        for (int i = 0; i < height; i++) {
            weights[i][width - 1] = BORDER_ENERGY;
        }
        for (int j = width - 2; j >= 0; j--) {
            for (int i = 0; i < height; i++) {
                double minWeight = weights[i][j + 1];
                int prevI = i;
                if (i > 0 && weights[i - 1][j + 1] < minWeight) {
                    minWeight = weights[i - 1][j + 1];
                    prevI = i - 1;
                }
                if (i < height - 1 && weights[i + 1][j + 1] < minWeight) {
                    minWeight = weights[i + 1][j + 1];
                    prevI = i + 1;
                }
                weights[i][j] = energyMatrix[i][j] + minWeight;
                prevIndex[i][j] = prevI;
            }
        }
        double minWeight = weights[0][0];
        int minWeightIndex = 0;
        for (int i = 1; i < height; i++) {
            if (weights[i][0] < minWeight) {
                minWeight = weights[i][0];
                minWeightIndex = i;
            }
        }
        int[] seam = new int[width];

        for (int j = 0; j < width; j++) {
            seam[j] = minWeightIndex;
            minWeightIndex = prevIndex[minWeightIndex][j];
        }
        return seam;
    }

    public int[] findVerticalSeam() {
        double[][] weights = new double[height][width];
        int[][] prevIndex = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                weights[i][j] = Double.MAX_VALUE;
            }
        }
        for (int j = 0; j < width; j++) {
            weights[height - 1][j] = BORDER_ENERGY;
        }
        for (int i = height - 2; i >= 0; i--) {
            for (int j = 0; j < width; j++) {
                double minWeight = weights[i + 1][j];
                int prevJ = j;
                if (j > 0 && weights[i + 1][j - 1] < minWeight) {
                    minWeight = weights[i + 1][j - 1];
                    prevJ = j - 1;
                }
                if (j < width - 1 && weights[i + 1][j + 1] < minWeight) {
                    minWeight = weights[i + 1][j + 1];
                    prevJ = j + 1;
                }
                weights[i][j] = energyMatrix[i][j] + minWeight;
                prevIndex[i][j] = prevJ;
            }
        }
        double minWeight = weights[0][0];
        int minWeightIndex = 0;
        for (int j = 1; j < width; j++) {
            if (weights[0][j] < minWeight) {
                minWeight = weights[0][j];
                minWeightIndex = j;
            }
        }
        int[] seam = new int[height];

        for (int i = 0; i < height; i++) {
            seam[i] = minWeightIndex;
            minWeightIndex = prevIndex[i][minWeightIndex];
        }
        return seam;
    }

    public void removeHorizontalSeam(int[] seam) {
        if (height <= 1) {
            throw new IllegalArgumentException();
        }
        validateHorizontalSeam(seam);
        height -= 1;
        Color[][] newPixelsMatrix = new Color[height][width];
        for (int j = 0; j < width; j++) {
            for (int i = 0; i < height; i++) {
                if (i < seam[j]) {
                    newPixelsMatrix[i][j] = pixelsMatrix[i][j];
                } else {
                    newPixelsMatrix[i][j] = pixelsMatrix[i + 1][j];
                }
            }
        }
        this.pixelsMatrix = newPixelsMatrix;
        recalculateEnergyMatrix();
    }

    public void removeVerticalSeam(int[] seam) {
        if (width <= 1) {
            throw new IllegalArgumentException();
        }
        validateVerticalSeam(seam);
        width -= 1;
        Color[][] newPixelsMatrix = new Color[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (j < seam[i]) {
                    newPixelsMatrix[i][j] = pixelsMatrix[i][j];
                } else {
                    newPixelsMatrix[i][j] = pixelsMatrix[i][j + 1];
                }
            }
        }
        this.pixelsMatrix = newPixelsMatrix;
        recalculateEnergyMatrix();
    }

    private void validateHorizontalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException();
        }
        if (seam.length != width) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < seam.length; i++) {
            int yCoordinate = seam[i];
            validateYCoordinate(yCoordinate);
            if (i > 0 && Math.abs(yCoordinate - seam[i - 1]) > 1) {
                throw new IllegalArgumentException();
            }
        }
    }

    private void validateVerticalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException();
        }
        if (seam.length != height) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < seam.length; i++) {
            int xCoordinate = seam[i];
            validateXCoordinate(xCoordinate);
            if (i > 0 && Math.abs(xCoordinate - seam[i - 1]) > 1) {
                throw new IllegalArgumentException();
            }
        }
    }

    private void recalculateEnergyMatrix() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                energyMatrix[i][j] = isBorder(i, j) ? BORDER_ENERGY : calculateEnergy(i, j);
            }
        }
    }

    private void validateXCoordinate(int x) {
        if (x < 0 || x >= width) {
            throw new IllegalArgumentException();
        }
    }

    private void validateYCoordinate(int y) {
        if (y < 0 || y >= height) {
            throw new IllegalArgumentException();
        }
    }

    public static void main(String[] args) {
        Picture picture = new Picture(new File("/home/viktor/Local/coursera/HJoceanSmall.png"));
        SeamCarver seamCarver = new SeamCarver(picture);
        for (int i = 0; i < 400; i++) {
            seamCarver.removeVerticalSeam(seamCarver.findVerticalSeam());
        }
        for (int i = 0; i < 200; i++) {
            seamCarver.removeHorizontalSeam(seamCarver.findHorizontalSeam());
        }
        seamCarver.picture().save("/home/viktor/Local/coursera/HJoceanSmall-400v-200h.png");
        System.out.println("success");
    }
}
