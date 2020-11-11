package board;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.Comparator;
import java.util.function.Function;

public class Solver {
    private static final Function<Board, Integer> PRIORITY_FUNCTION = Board::manhattan;
    private boolean isSolvable;
    private final Stack<Board> solution = new Stack<>();

    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException("Cannot be null.");
        }
        solve(initial);
    }

    private void solve(Board initial) {
        MinPQ<WeightedBoard> minPQ = new MinPQ<>(10, Comparator.comparingInt(weightedBoard -> weightedBoard.weight));
        MinPQ<WeightedBoard> twinMinPq = new MinPQ<>(10, Comparator.comparingInt(weightedBoard -> weightedBoard.weight));

        minPQ.insert(new WeightedBoard(initial, null, PRIORITY_FUNCTION.apply(initial), 0));
        Board twinInitialBoard = initial.twin();
        twinMinPq.insert(new WeightedBoard(twinInitialBoard, null, PRIORITY_FUNCTION.apply(twinInitialBoard), 0));

        while (!minPQ.min().board.isGoal() && !twinMinPq.min().board.isGoal()) {
            WeightedBoard original = minPQ.delMin();
            WeightedBoard twin = twinMinPq.delMin();
            insertNeighbors(minPQ, original);
            insertNeighbors(twinMinPq, twin);
        }
        this.isSolvable = minPQ.min().board.isGoal();
        if (isSolvable) {
            WeightedBoard goalBoard = minPQ.min();
            recreateSteps(goalBoard);
        }
    }

    private void insertNeighbors(MinPQ<WeightedBoard> minPQ, WeightedBoard weightedBoard) {
        int moves = weightedBoard.moves + 1;
        Board previousBoard = weightedBoard.previous != null ?
                weightedBoard.previous.board : null;
        for (Board neighbor : weightedBoard.board.neighbors()) {
            if (neighbor.equals(previousBoard)) continue;
            WeightedBoard weightedNeighbor = new WeightedBoard(neighbor, weightedBoard, PRIORITY_FUNCTION.apply(neighbor) + moves, moves);
            minPQ.insert(weightedNeighbor);
        }
    }

    private void recreateSteps(WeightedBoard goalBoard) {
        WeightedBoard currentBoard = goalBoard;
        do {
            this.solution.push(currentBoard.board);
            currentBoard = currentBoard.previous;
        } while (currentBoard != null);
    }

    public boolean isSolvable() {
        return isSolvable;
    }

    public int moves() {
        return solution.isEmpty() ?
                0 : solution.size() - 1;
    }

    public Iterable<Board> solution() {
        return solution;
    }

    private static class WeightedBoard {
        private final Board board;
        private final WeightedBoard previous;
        private final int weight;
        private final int moves;

        public WeightedBoard(Board board, WeightedBoard previous, int weight, int moves) {
            this.board = board;
            this.previous = previous;
            this.weight = weight;
            this.moves = moves;
        }
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tiles[i][j] = in.readInt();
            }
        }

        Board initial = new Board(tiles);

        Solver solver = new Solver(initial);

        if (!solver.isSolvable()) {
            StdOut.println("No solution possible");
        } else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
