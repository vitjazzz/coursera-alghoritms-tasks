package board;

import java.util.Iterator;

public class Board {
    private final int n;
    private final int[][] tiles;
    private final int manhattan;
    private final int hamming;

    public Board(int[][] tiles) {
        this.n = tiles.length;
        this.tiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(tiles[i], 0, this.tiles[i], 0, n);
        }
        this.manhattan = calculateManhattan();
        this.hamming = calculateHamming();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(n + "\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                builder.append(tiles[i][j]);
                if (j != n - 1) {
                    builder.append(" ");
                }
            }
            if (i != n - 1) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    public int dimension() {
        return n;
    }

    public int hamming() {
        return hamming;
    }

    public int manhattan() {
        return manhattan;
    }

    public boolean isGoal() {
        if (tiles[n - 1][n - 1] != 0) {
            return false;
        }
        for (int i = 1; i < n * n; i++) {
            int row = (i - 1) / n;
            int col = (i - 1) % n;
            if (tiles[row][col] != i) {
                return false;
            }
        }

        return true;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Board that = (Board) o;

        if (n != that.n) return false;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (this.tiles[i][j] != that.tiles[i][j])
                    return false;
            }
        }
        return true;
    }

    public Iterable<Board> neighbors() {
        return BoardNeighborsIterator::new;
    }

    private class BoardNeighborsIterator implements Iterator<Board> {
        private int neighborsCount;
        private int currentIndex = 0;
        private final Board[] neighbors = new Board[4];

        private BoardNeighborsIterator() {
            TileIndex emptyTile = findEmptyTile();
            if (emptyTile.row > 0) {
                neighbors[neighborsCount++] = twin(emptyTile.row - 1, emptyTile.col, emptyTile.row, emptyTile.col);
            }
            if (emptyTile.row < n - 1) {
                neighbors[neighborsCount++] = twin(emptyTile.row + 1, emptyTile.col, emptyTile.row, emptyTile.col);
            }
            if (emptyTile.col > 0) {
                neighbors[neighborsCount++] = twin(emptyTile.row, emptyTile.col - 1, emptyTile.row, emptyTile.col);
            }
            if (emptyTile.col < n - 1) {
                neighbors[neighborsCount++] = twin(emptyTile.row, emptyTile.col + 1, emptyTile.row, emptyTile.col);
            }
        }

        public boolean hasNext() {
            return currentIndex < neighborsCount;
        }

        public Board next() {
            return neighbors[currentIndex++];
        }

        public void remove() {
            throw new IllegalStateException("not implemented");
        }

        private TileIndex findEmptyTile(){
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (tiles[i][j] == 0) {
                        return new TileIndex(i, j);
                    }
                }
            }
            throw new RuntimeException("Should not be here.");
        }

        private class TileIndex {
            final int row;
            final int col;

            public TileIndex(int row, int col) {
                this.row = row;
                this.col = col;
            }
        }
    }

    public Board twin() {
        int swapI = -1;
        int swapJ = -1;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] != 0) {
                    if (swapI == -1) {
                        swapI = i;
                        swapJ = j;
                    } else {
                        return twin(swapI, swapJ, i, j);
                    }
                }
            }
        }
        return null;
    }

    private Board twin(int xRow, int xCol, int yRow, int yCol) {
        swapTiles(xRow, xCol, yRow, yCol);
        Board twin = new Board(tiles);
        swapTiles(xRow, xCol, yRow, yCol);
        return twin;
    }

    private void swapTiles(int xRow, int xCol, int yRow, int yCol) {
        int temp = tiles[xRow][xCol];
        tiles[xRow][xCol] = tiles[yRow][yCol];
        tiles[yRow][yCol] = temp;
    }

    private int calculateHamming() {
        int hamming = 0;
        for (int i = 1; i < n * n; i++) {
            int row = (i - 1) / n;
            int col = (i - 1) % n;
            if (tiles[row][col] != i)
                hamming++;
        }
        if (tiles[n - 1][n - 1] != 0)
            hamming++;
        return hamming;
    }

    private int calculateManhattan() {
        int manhattan = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int tile = tiles[i][j];
                int goalRow = tile != 0 ?
                        (tile - 1) / n : n - 1;
                int goalCol = tile != 0 ?
                        (tile - 1) % n : n - 1;
                manhattan += Math.abs(goalRow - i) + Math.abs(goalCol - j);
            }
        }
        return manhattan;
    }


    public static void main(String[] args) {
        int[][] tiles = new int[3][3];
        tiles[0] = new int[]{1, 0, 3}; tiles[1] = new int[]{4, 2, 5}; tiles[2] = new int[]{7, 8, 6};
        Board board = new Board(tiles);
        System.out.println(board);

        for (Board neighbor : board.neighbors()) {
            System.out.println(neighbor);
        }
    }
}
