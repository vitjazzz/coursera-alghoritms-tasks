import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.io.FileInputStream;
import java.util.LinkedList;

public class MoveToFront {
    public static void encode() {
        LinkedList<Character> alphabet = createAlphabet();
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            int index = findLetter(c, alphabet);

            alphabet.remove(index);
            alphabet.add(0, c);

            BinaryStdOut.write((char) index);
        }
    }

    public static void decode() {
        LinkedList<Character> alphabet = createAlphabet();
        while (!BinaryStdIn.isEmpty()) {
            int index = BinaryStdIn.readChar();
            Character c = alphabet.get(index);

            alphabet.remove(index);
            alphabet.add(0, c);

            BinaryStdOut.write(c);
        }

    }

    private static LinkedList<Character> createAlphabet() {
        LinkedList<Character> alphabet = new LinkedList<>();
        for (int i = 0; i < 255; i++) {
            alphabet.add((char) i);
        }
        return alphabet;
    }

    private static int findLetter(char c, LinkedList<Character> alphabet) {
        for (int i = 0; i < alphabet.size(); i++) {
            if (alphabet.get(i) == c) {
                return i;
            }
        }
        return -1;
    }


    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            throw new IllegalArgumentException();
        }
        if (args.length >= 2) {
            FileInputStream stream = new FileInputStream(args[1]);
            System.setIn(stream);
        }
        switch (args[0]) {
            case "-":
                encode();
                break;
            case "+":
                decode();
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

}
