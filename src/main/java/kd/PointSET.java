package kd;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class PointSET {
    private final Set<Point2D> points = new TreeSet<Point2D>();

    public PointSET() {
    }

    public boolean isEmpty() {
        return points.isEmpty();
    }

    public int size() {
        return points.size();
    }

    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        points.add(p);
    }

    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        return points.contains(p);
    }

    public void draw() {
        for (Point2D point : points) {
            StdDraw.point(point.x(), point.y());
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();
        return points.stream()
                .filter(rect::contains)
                .collect(Collectors.toList());
    }

    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        double minDistance = Double.MAX_VALUE;
        Point2D nearestPoint = null;
        for (Point2D point : points) {
            double distance = p.distanceTo(point);
            if (distance < minDistance) {
                minDistance = distance;
                nearestPoint = point;
            }
        }
        return nearestPoint;
    }
}
