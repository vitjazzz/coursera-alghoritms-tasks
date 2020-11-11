package wordnet;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wordNet;

    public Outcast(WordNet wordnet) {
        if (wordnet == null) {
            throw new IllegalArgumentException();
        }
        this.wordNet = wordnet;
    }

    public String outcast(String[] nouns) {
        if (nouns == null) {
            throw new IllegalArgumentException();
        }
        String currentOutcast = null;
        int currentMaxDistance = Integer.MIN_VALUE;
        for (String noun : nouns) {
            if (noun == null) {
                throw new IllegalArgumentException();
            }
            int currentDistance = 0;
            for (String otherNoun : nouns) {
                currentDistance += wordNet.distance(noun, otherNoun);
            }
            if (currentDistance > currentMaxDistance){
                currentMaxDistance = currentDistance;
                currentOutcast = noun;
            }
        }
        return currentOutcast;
    }



    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
