import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;


public class BurrowsWheeler {
    public static void transform() {
        String string = StdIn.readString();
        CircularSuffixArray circularSuffixArray = new CircularSuffixArray(string);

        int first = -1;
        byte[] bytes = string.getBytes(StandardCharsets.US_ASCII);
        byte[] transformedBytes = new byte[string.length()];
        for (int i = 0; i < string.length(); i++) {
            int circularIndex = circularSuffixArray.index(i);
            if (circularIndex == 0) {
                first = i;
                transformedBytes[i] = bytes[string.length() - 1];
            } else {
                transformedBytes[i] = bytes[circularIndex - 1];
            }
        }

        StdOut.println(first);
        StdOut.println(new String(transformedBytes));
    }

    public static void inverseTransform() {
        int firstSuffixIndex = StdIn.readInt();
        String codedString = StdIn.readString();
        byte[] lastBytes = codedString.getBytes();

        Map<Byte, Queue<Integer>> lastLettersDictionary = new HashMap<>(255);
        for (int i = 0; i < lastBytes.length; i++) {
            lastLettersDictionary.computeIfAbsent(lastBytes[i], b -> new LinkedList<>()).add(i);
        }

        int nextIndex = 0;
        int[] next = new int[lastBytes.length];
        byte[] firstBytes = new byte[lastBytes.length];

        for (int letter = 0; letter < 256; letter++) {
            Queue<Integer> lastLetterIndexes = lastLettersDictionary.get((byte)letter);
            if (lastLetterIndexes == null) {
                continue;
            }
            while (!lastLetterIndexes.isEmpty()) {
                Integer index = lastLetterIndexes.poll();
                next[nextIndex] = index;
                firstBytes[nextIndex] = (byte)letter;
                nextIndex++;
            }
        }

        int nextLetterIndex = firstSuffixIndex;
        byte[] result = new byte[lastBytes.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = firstBytes[nextLetterIndex];
            nextLetterIndex = next[nextLetterIndex];
        }

        StdOut.println(new String(result));
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
                transform();
                break;
            case "+":
                inverseTransform();
                break;
            default:
                throw new IllegalArgumentException();
        }
    }
}
