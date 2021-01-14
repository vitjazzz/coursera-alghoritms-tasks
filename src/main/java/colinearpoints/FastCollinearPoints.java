package colinearpoints;

import java.util.Arrays;

public class FastCollinearPoints {
    private LineSegment[] segments = new LineSegment[0];

    public FastCollinearPoints(Point[] points) {
        if (points == null) throw new IllegalArgumentException("Points array cannot be null");
        for (Point point : points) {
            if (point == null) throw new IllegalArgumentException("No point in the array cannot be null");
        }
        if (points.length < 4) return;
        Point[] pointsCopy = new Point[points.length];
        System.arraycopy(points, 0, pointsCopy, 0, points.length);
        for (Point selectedPoint : pointsCopy) {
            Arrays.sort(points, selectedPoint.slopeOrder());
            double currentSlope = selectedPoint.slopeTo(points[0]);
            int currentCollinearCount = 1;
            for (int j = 1; j < points.length; j++) {
                double jSlope = selectedPoint.slopeTo(points[j]);
                if (jSlope == currentSlope) {
                    currentCollinearCount++;
                    if (j != points.length - 1) {
                        continue;
                    }
                }
                if (currentCollinearCount >= 3) {
                    Point[] collinearPoints = new Point[currentCollinearCount + 1];
                    System.arraycopy(points, j - currentCollinearCount,
                            collinearPoints, 0, currentCollinearCount);
                    collinearPoints[currentCollinearCount] = selectedPoint;
                    Arrays.sort(collinearPoints);
                    if (collinearPoints[0] == selectedPoint) {
                        LineSegment[] oldSegments = segments;
                        segments = new LineSegment[oldSegments.length + 1];
                        System.arraycopy(oldSegments, 0, segments, 0, oldSegments.length);
                        segments[oldSegments.length] = new LineSegment(collinearPoints[0],
                                collinearPoints[collinearPoints.length - 1]);
                    }
                }
                currentCollinearCount = 1;
                currentSlope = jSlope;

            }
        }
    }

    public int numberOfSegments() {
        return segments.length;
    }

    public LineSegment[] segments() {
        return segments;
    }
}
