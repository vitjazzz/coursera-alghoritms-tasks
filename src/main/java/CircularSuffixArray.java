import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Quick3way;

public class CircularSuffixArray {
    private final int length;
    private final int[] circularSuffixArray;

    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }
        this.length = s.length();
        this.circularSuffixArray = calculateSuffixArray(s);
    }

    private static int[] calculateSuffixArray(String s) {
        ComparableSuffix[] comparableSuffixes = new ComparableSuffix[s.length()];
        byte[] sourceBytes = s.getBytes();
        for (int i = 0; i < s.length(); i++) {
            comparableSuffixes[i] = new ComparableSuffix(sourceBytes, i);
        }

        Quick3way.sort(comparableSuffixes);

        int[] circularSuffixArray = new int[s.length()];
        for (int i = 0; i < s.length(); i++) {
            circularSuffixArray[i] = comparableSuffixes[i].leftShiftSize;
        }
        return circularSuffixArray;
    }

    public int length() {
        return length;
    }

    public int index(int i) {
        if (i < 0 || i >= length) {
            throw new IllegalArgumentException();
        }
        return circularSuffixArray[i];
    }

    private static class ComparableSuffix implements Comparable<ComparableSuffix> {
        private final byte[] source;
        private final int leftShiftSize;

        private ComparableSuffix(byte[] source, int leftShiftSize) {
            this.source = source;
            this.leftShiftSize = leftShiftSize;
        }

        @Override
        public int compareTo(ComparableSuffix second) {
            ComparableSuffix first = this;
            for (int i = 0; i < source.length; i++) {
                int firstIndex = (first.leftShiftSize + i) % source.length;
                int secondIndex = (second.leftShiftSize + i) % source.length;
                if (source[firstIndex] != source[secondIndex]) {
                    return source[firstIndex] - source[secondIndex];
                }
            }
            return 0;
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException();
        }
        In in = new In(args[0]);
        String string = in.readString();
        CircularSuffixArray circularSuffixArray = new CircularSuffixArray(string);
        for (int i = 0; i < string.length(); i++) {
            BinaryStdOut.write(circularSuffixArray.index(i));
        }
    }
}
