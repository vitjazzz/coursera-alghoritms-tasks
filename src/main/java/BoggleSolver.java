import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;
import java.util.Set;

public class BoggleSolver {
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)

    private final CustomTrieSET dictionaryTrie;

    public BoggleSolver(String[] dictionary) {
        if (dictionary == null) {
            throw new IllegalArgumentException();
        }

        this.dictionaryTrie = new CustomTrieSET();
        for (String word : dictionary) {
            dictionaryTrie.add(word);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        Set<String> validWords = new HashSet<>();
        boolean[][] visited = new boolean[board.rows()][board.cols()];
        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                findAllWords(board, i, j, dictionaryTrie.root, "", visited, validWords);
            }
        }
        return validWords;
    }

    private void findAllWords(BoggleBoard board, int i, int j, CustomTrieSET.Node node, String word, boolean[][] visited, Set<String> words) {
        char letter = board.getLetter(i, j);
        node = node.getNext(letter);
        if (node == null) {
            return;
        }
        word += letter;
        if (letter == 'Q') {
            node = node.getNext('U');
            word += 'U';
            if (node == null) {
                return;
            }
        }

        visited = copyArray(visited, board.rows(), board.cols());
        visited[i][j] = true;

        if (node.isString && word.length() >= 3) {
            words.add(word);
        }
        for (int subI = i - 1; subI <= i + 1; subI++) {
            for (int subJ = j - 1; subJ <= j + 1; subJ++) {
                if (subI == i && subJ == j
                        || subI < 0
                        || subJ < 0
                        || subI >= board.rows()
                        || subJ >= board.cols()
                        || visited[subI][subJ]
                ) {
                    continue;
                }
                findAllWords(board, subI, subJ, node, word, visited, words);
            }
        }
    }

    private boolean[][] copyArray(boolean[][] arr, int rows, int cols) {
        boolean[][] newArr = new boolean[rows][cols];
        for (int i = 0; i < arr.length; i++) {
            System.arraycopy(arr[i], 0, newArr[i], 0, arr[i].length);
        }
        return newArr;
    }

    public int scoreOf(String word) {
        if (word == null) {
            throw new IllegalArgumentException();
        }
        int length = word.length();
        if (!dictionaryTrie.contains(word) || length < 3) {
            return 0;
        }
        if (length == 3 || length == 4) {
            return 1;
        } else if (length == 5) {
            return 2;
        } else if (length == 6) {
            return 3;
        } else if (length == 7) {
            return 5;
        } else {
            return 11;
        }
    }

    private static class CustomTrieSET {
        private final static int R = 26;

        private static class Node {
            private Node[] next = new Node[R];
            private boolean isString;

            private Node getNext(char c){
                return next[c - 65];
            }
            private void setNext(char c, Node n){
                next[c - 65] = n;
            }
        }

        private Node root;
        private int size;

        private CustomTrieSET() {
        }

        private void add(String key) {
            if (key == null) {
                throw new IllegalArgumentException();
            }
            this.root = add(root, key, 0);
        }

        private Node add(Node node, String key, int d) {
            if (node == null) node = new Node();

            if (d == key.length()) {
                if (!node.isString) {
                    node.isString = true;
                    size++;
                }
                return node;
            }
            char charAt = key.charAt(d);
            node.setNext(charAt, add(node.getNext(charAt), key, d + 1));
            return node;
        }

        private boolean contains(String key) {
            if (key == null) throw new IllegalArgumentException();
            Node x = get(root, key, 0);
            if (x == null) return false;
            return x.isString;
        }

        private Node get(Node node, String key, int d) {
            if (node == null) return null;
            if (d == key.length()) return node;
            char charAt = key.charAt(d);
            return get(node.getNext(charAt), key, d+1);
        }
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
