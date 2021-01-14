package colinearpoints;

public class BruteCollinearPoints {
    private LineSegment[] lineSegments = new LineSegment[0];

    public BruteCollinearPoints(Point[] points) {
        if (points == null) throw new IllegalArgumentException("Points array cannot be null");
        for (Point point : points) {
            if (point == null) throw new IllegalArgumentException("No point in the array cannot be null");
        }
        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                double ijSlope = points[i].slopeTo(points[j]);
                if (ijSlope == Double.NEGATIVE_INFINITY)
                    throw new IllegalArgumentException("Points cannot be equal.");
                for (int k = j + 1; k < points.length; k++) {
                    double jkSlope = points[j].slopeTo(points[k]);
                    if (jkSlope == Double.NEGATIVE_INFINITY)
                        throw new IllegalArgumentException("Points cannot be equal.");
                    for (int l = k + 1; l < points.length; l++) {
                        double klSlope = points[k].slopeTo(points[l]);
                        if (klSlope == Double.NEGATIVE_INFINITY)
                            throw new IllegalArgumentException("Points cannot be equal.");
                        if (ijSlope == jkSlope && jkSlope == klSlope) {
                            LineSegment[] oldSegment = lineSegments;
                            lineSegments = new LineSegment[oldSegment.length + 1];
                            System.arraycopy(oldSegment, 0, lineSegments, 0, oldSegment.length);
                            Point min = points[i];
                            Point max = points[j];
                            if (points[i].compareTo(points[j]) > 0
                                    && points[i].compareTo(points[k]) > 0
                                    && points[i].compareTo(points[l]) > 0) {
                                max = points[i];
                            } else if(points[j].compareTo(points[k]) > 0
                                    && points[j].compareTo(points[l]) > 0) {
                                max = points[j];
                            } else if (points[k].compareTo(points[l]) > 0) {
                                max = points[k];
                            } else {
                                max = points[l];
                            }

                            if (points[i].compareTo(points[j]) < 0
                                    && points[i].compareTo(points[k]) < 0
                                    && points[i].compareTo(points[l]) < 0) {
                                min = points[i];
                            } else if(points[j].compareTo(points[k]) < 0
                                    && points[j].compareTo(points[l]) < 0) {
                                min = points[j];
                            } else if (points[k].compareTo(points[l]) < 0) {
                                min = points[k];
                            } else {
                                min = points[l];
                            }

                            lineSegments[oldSegment.length] = new LineSegment(min, max);

                        }
                    }
                }
            }
        }
    }


    public int numberOfSegments() {
        return lineSegments.length;
    }

    public LineSegment[] segments() {
        return lineSegments;
    }
}
