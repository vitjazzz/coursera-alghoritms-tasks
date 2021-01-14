package queue;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class Permutation {
    public static void main(String[] args) {
        int k = Integer.parseInt(args[0]);
        RandomizedQueue<String> queue = new RandomizedQueue<>();
        int i = 0;
        while (!StdIn.isEmpty()) {
            String str = StdIn.readString();
            if (i++ < k) {
                queue.enqueue(str);
            } else {
                boolean needEnqueue = StdRandom.uniform(i) < k;
                if (needEnqueue) {
                    queue.dequeue();
                    queue.enqueue(str);
                }
            }
        }
        for (i = 0; i < k; i++) {
            StdOut.println(queue.dequeue());
        }
    }
}
