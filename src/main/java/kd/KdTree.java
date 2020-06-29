package kd;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class KdTree {
    private int size = 0;
    private Node<Point2D> root = null;

    public KdTree() {
    }

    public boolean isEmpty() {
        return root == null;
    }

    public int size() {
        return size;
    }

    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();

        root = put(root, p, DIRECTION.VERTICAL);
    }

    private Node<Point2D> put(Node<Point2D> node, Point2D p, DIRECTION direction) {
        if (node == null) {
            size++;
            return new Node<>(p, direction);
        }

        double nodeValue = direction.pointValue(node.value);
        double pValue = direction.pointValue(p);
        if (node.value.compareTo(p) == 0) return node;
        if (pValue < nodeValue) {
            node.left = put(node.left, p, direction.opposite());
        }
        if (pValue >= nodeValue) {
            node.right = put(node.right, p, direction.opposite());
        }
        return node;
    }

    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        Node<Point2D> node = root;
        while (node != null) {
            if (node.value.compareTo(p) == 0) {
                return true;
            }
            node = node.direction.pointValue(p) < node.direction.pointValue(node.value) ?
                    node.left : node.right;
        }
        return false;
    }

    public void draw() {
        draw(root, new RectHV(0, 0, 1, 1));
    }

    private void draw(Node<Point2D> node, RectHV rect) {
        if (node == null) return;

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        StdDraw.point(node.value.x(), node.value.y());

        StdDraw.setPenRadius(0.001);
        if (node.direction == DIRECTION.VERTICAL) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(node.value.x(), rect.ymin(), node.value.x(), rect.ymax());
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(rect.xmin(), node.value.y(), rect.xmax(), node.value.y());
        }

        RectHV leftRect = leftRect(node, rect);
        draw(node.left, leftRect);

        RectHV rightRect = rightRect(node, rect);
        draw(node.right, rightRect);
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();
        List<Point2D> result = new ArrayList<>();

        fillRange(root, rect, result);

        return result;
    }

    private void fillRange(Node<Point2D> node, RectHV rect, List<Point2D> pointsInRange) {
        if (node == null) return;
        if (rect.contains(node.value)) {
            pointsInRange.add(node.value);
        }
        DIRECTION dir = node.direction;
        double val = dir.pointValue(node.value);
        double minVal;
        double maxVal;
        if (dir == DIRECTION.HORIZONTAL) {
            minVal = rect.ymin();
            maxVal = rect.ymax();
        } else {
            minVal = rect.xmin();
            maxVal = rect.xmax();
        }

        if (maxVal < val) {
            fillRange(node.left, rect, pointsInRange);
        } else if (minVal > val) {
            fillRange(node.right, rect, pointsInRange);
        } else {
            fillRange(node.left, rect, pointsInRange);
            fillRange(node.right, rect, pointsInRange);
        }
    }

    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (root == null) return null;
        return nearest(root,
                new RectHV(0, 0, 1, 1),
                p,
                new Tuple2(null, Double.MAX_VALUE)
        )
                .point;
    }

    private Tuple2 nearest(Node<Point2D> node, RectHV rect, Point2D p, Tuple2 currentNearest) {
        if (node == null) {
            return currentNearest;
        }

        double distance = node.value.distanceTo(p);

        if (distance < currentNearest.distance) {
            currentNearest = new Tuple2(node.value, distance);
        }

        RectHV leftRect = leftRect(node, rect);
        RectHV rightRect = rightRect(node, rect);

        Node<Point2D> sameSideNode;
        RectHV sameSideRect;
        Node<Point2D> otherSideNode;
        RectHV otherSideRect;
        if (node.direction.pointValue(p) < node.direction.pointValue(node.value)) {
            sameSideNode = node.left;
            sameSideRect = leftRect;
            otherSideNode = node.right;
            otherSideRect = rightRect;
        } else {
            sameSideNode = node.right;
            sameSideRect = rightRect;
            otherSideNode = node.left;
            otherSideRect = leftRect;
        }

        Tuple2 sameSideNearest = nearest(sameSideNode, sameSideRect, p, currentNearest);
        if (sameSideNearest.distance < currentNearest.distance) {
            currentNearest = sameSideNearest;
        }

        if (otherSideRect.distanceTo(p) < currentNearest.distance) {
            Tuple2 otherSideNearest = nearest(otherSideNode, otherSideRect, p, currentNearest);
            if (otherSideNearest.distance < currentNearest.distance) {
                currentNearest = otherSideNearest;
            }
        }

        return currentNearest;
    }

    private RectHV leftRect(Node<Point2D> node, RectHV parentRect) {
        return node.direction == DIRECTION.HORIZONTAL ?
                new RectHV(parentRect.xmin(), parentRect.ymin(), parentRect.xmax(), node.value.y())
                :
                new RectHV(parentRect.xmin(), parentRect.ymin(), node.value.x(), parentRect.ymax());
    }

    private RectHV rightRect(Node<Point2D> node, RectHV parentRect) {
        return node.direction == DIRECTION.HORIZONTAL ?
                new RectHV(parentRect.xmin(), node.value.y(), parentRect.xmax(), parentRect.ymax())
                :
                new RectHV(node.value.x(), parentRect.ymin(), parentRect.xmax(), parentRect.ymax());
    }

    private static class Tuple2 {
        private final Point2D point;
        private final double distance;

        public Tuple2(Point2D point, double distance) {
            this.point = point;
            this.distance = distance;
        }
    }

    private static class Node<T> {
        private final T value;
        private Node<T> left, right;
        private final DIRECTION direction;

        public Node(T value, DIRECTION direction) {
            this.value = value;
            this.direction = direction;
        }


    }

    private enum DIRECTION {
        HORIZONTAL(Point2D::y), VERTICAL(Point2D::x);

        private final Function<Point2D, Double> pointValueExtractor;

        DIRECTION(Function<Point2D, Double> pointValueExtractor) {
            this.pointValueExtractor = pointValueExtractor;
        }

        public DIRECTION opposite() {
            return this == VERTICAL ? HORIZONTAL : VERTICAL;
        }

        public double pointValue(Point2D p) {
            return pointValueExtractor.apply(p);
        }
    }

//    public static void main(String[] args) {
//        // initialize the two data structures with point from file
//        String filename = args[0];
//        In in = new In(filename);
//        PointSET brute = new PointSET();
//        KdTree kdtree = new KdTree();
//        while (!in.isEmpty()) {
//            double x = in.readDouble();
//            double y = in.readDouble();
//            Point2D p = new Point2D(x, y);
//            kdtree.insert(p);
//            brute.insert(p);
//        }
//
//        // process nearest neighbor queries
//        StdDraw.enableDoubleBuffering();
//        while (true) {
//
//            // the location (x, y) of the mouse
//            double x = StdDraw.mouseX();
//            double y = StdDraw.mouseY();
//            Point2D query = new Point2D(x, y);
//
//            // draw all of the points
//            StdDraw.clear();
//            StdDraw.setPenColor(StdDraw.BLACK);
//            StdDraw.setPenRadius(0.01);
//            brute.draw();
//
//            // draw in red the nearest neighbor (using brute-force algorithm)
//            StdDraw.setPenRadius(0.03);
//            StdDraw.setPenColor(StdDraw.RED);
//            brute.nearest(query).draw();
//            StdDraw.setPenRadius(0.02);
//
//            // draw in blue the nearest neighbor (using kd-tree algorithm)
//            StdDraw.setPenColor(StdDraw.BLUE);
//            kdtree.nearest(query).draw();
//            StdDraw.show();
//            StdDraw.pause(40);
//        }
//    }

//    public static void main(String[] args) {
//        // initialize the data structures from file
//        String filename = args[0];
//        In in = new In(filename);
//        PointSET brute = new PointSET();
//        KdTree kdtree = new KdTree();
//        while (!in.isEmpty()) {
//            double x = in.readDouble();
//            double y = in.readDouble();
//            Point2D p = new Point2D(x, y);
//            kdtree.insert(p);
//            brute.insert(p);
//        }
//
//        double x0 = 0.0, y0 = 0.0;      // initial endpoint of rectangle
//        double x1 = 0.0, y1 = 0.0;      // current location of mouse
//        boolean isDragging = false;     // is the user dragging a rectangle
//
//        // draw the points
//        StdDraw.clear();
//        StdDraw.setPenColor(StdDraw.BLACK);
//        StdDraw.setPenRadius(0.01);
//        brute.draw();
//        StdDraw.show();
//
//        // process range search queries
//        StdDraw.enableDoubleBuffering();
//        while (true) {
//
//            // user starts to drag a rectangle
//            if (StdDraw.isMousePressed() && !isDragging) {
//                x0 = x1 = StdDraw.mouseX();
//                y0 = y1 = StdDraw.mouseY();
//                isDragging = true;
//            }
//
//            // user is dragging a rectangle
//            else if (StdDraw.isMousePressed() && isDragging) {
//                x1 = StdDraw.mouseX();
//                y1 = StdDraw.mouseY();
//            }
//
//            // user stops dragging rectangle
//            else if (!StdDraw.isMousePressed() && isDragging) {
//                isDragging = false;
//            }
//
//            // draw the points
//            StdDraw.clear();
//            StdDraw.setPenColor(StdDraw.BLACK);
//            StdDraw.setPenRadius(0.01);
//            brute.draw();
//
//            // draw the rectangle
//            RectHV rect = new RectHV(Math.min(x0, x1), Math.min(y0, y1),
//                    Math.max(x0, x1), Math.max(y0, y1));
//            StdDraw.setPenColor(StdDraw.BLACK);
//            StdDraw.setPenRadius();
//            rect.draw();
//
//            // draw the range search results for brute-force data structure in red
//            StdDraw.setPenRadius(0.03);
//            StdDraw.setPenColor(StdDraw.RED);
//            for (Point2D p : brute.range(rect))
//                p.draw();
//
//            // draw the range search results for kd-tree in blue
//            StdDraw.setPenRadius(0.02);
//            StdDraw.setPenColor(StdDraw.BLUE);
//            for (Point2D p : kdtree.range(rect))
//                p.draw();
//
//            StdDraw.show();
//            StdDraw.pause(20);
//        }
//    }
}
