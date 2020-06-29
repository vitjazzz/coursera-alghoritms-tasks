import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public class SAP {
    private final Digraph digraph;
    private Info[] infos;
    private Queue<Integer> processingQueue = new LinkedList<>();


    public SAP(Digraph G) {
        this.digraph = G;

    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (isOutOfRange(v) || isOutOfRange(w)) {
            throw new IllegalArgumentException();
        }
        return length(Collections.singleton(v), Collections.singleton(w));
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if (isOutOfRange(v) || isOutOfRange(w)) {
            throw new IllegalArgumentException();
        }
        return ancestor(Collections.singleton(v), Collections.singleton(w));
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException();
        }
        return bfs(v, w).length;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException();
        }
        return bfs(v, w).ancestor;
    }

    private AncestralPath bfs(Iterable<Integer> v, Iterable<Integer> w) {
        // TODO keep 2 types of info array (for v and w)
        infos = new Info[digraph.V()];
        processingQueue.clear();
        for (Integer vVertex : v) {
            addSource(vVertex, SOURCE.V);
        }
        for (Integer wVertex : w) {
            boolean containsSameV = addSource(wVertex, SOURCE.W);
            if (containsSameV) return new AncestralPath(0, wVertex);
        }
        AncestralPath shortestPath = new AncestralPath(Integer.MAX_VALUE, -1);
        while (!processingQueue.isEmpty()) {
            Integer currentVertex = processingQueue.poll();
            Info currentInfo = infos[currentVertex];
            if (currentInfo.distance + 1 >= shortestPath.length) {
                continue;
            }
            for (Integer vertex : digraph.adj(currentVertex)) {
                Info vertexInfo = infos[vertex];
                if (vertexInfo != null) {
                    shortestPath = calculateShortestPath(shortestPath, currentInfo, vertex, vertexInfo);
                    if (vertexInfo.source != currentInfo.source){
                        addVertexToQueue(currentVertex, vertex);
                    }
                } else {
                    addVertexToQueue(currentVertex, vertex);
                }
            }
        }
        return new AncestralPath(shortestPath.length != Integer.MAX_VALUE ?
                shortestPath.length : -1,
                shortestPath.ancestor
        );
    }

    private void addVertexToQueue(Integer currentVertex, Integer nextVertex) {
        Info currentInfo = infos[currentVertex];
        infos[nextVertex] = new Info(
                currentInfo.distance + 1,
                currentVertex,
                currentInfo.source
        );
        processingQueue.add(nextVertex);
    }

    private AncestralPath calculateShortestPath(AncestralPath shortestPath, Info currentInfo, Integer vertex, Info vertexInfo) {
        if (vertexInfo.source != currentInfo.source) {
            int pathLength = vertexInfo.distance + currentInfo.distance + 1;
            if (pathLength < shortestPath.length) {
                shortestPath = new AncestralPath(pathLength, vertex);
            }
        }
        return shortestPath;
    }

    private boolean addSource(Integer sourceVertex, SOURCE source) {
        if (sourceVertex == null || isOutOfRange(sourceVertex)) {
            throw new IllegalArgumentException();
        }
        if (infos[sourceVertex] != null) {
            if (infos[sourceVertex].source != source) {
                return true;
            }
        } else {
            infos[sourceVertex] = new Info(0, sourceVertex, source);
            processingQueue.add(sourceVertex);
        }
        return false;
    }

    private static class Info {
        private final int distance;
        private final int fromVertex;
        private final SOURCE source;

        private Info(int distance, int fromVertex, SOURCE source) {
            this.distance = distance;
            this.fromVertex = fromVertex;
            this.source = source;
        }
    }

    private static class AncestralPath {
        private final int length;
        private final int ancestor;

        public AncestralPath(int length, int ancestor) {
            this.length = length;
            this.ancestor = ancestor;
        }
    }

    private enum SOURCE {
        V, W;
    }

    private boolean isOutOfRange(int i) {
        return i < 0 || i >= digraph.V();
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
