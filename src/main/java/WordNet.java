import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WordNet {

    private final Map<String, Set<Integer>> nounIndexes = new HashMap<>();
    private final List<String> wordNetSynsets = new ArrayList<>();
    private Digraph synsetsDigraph;
    private int root;

    private SAP sap;


    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null){
            throw new IllegalArgumentException();
        }
        fillSynsets(synsets);
        fillHypernyms(hypernyms);
        if (new DirectedCycle(synsetsDigraph).hasCycle()) {
            throw new IllegalArgumentException("No cicles allowed.");
        }
        List<Integer> zeroOutDegreeVertexes = IntStream.range(0, wordNetSynsets.size())
                .filter(index -> synsetsDigraph.outdegree(index) == 0)
                .boxed()
                .collect(Collectors.toList());
        if (zeroOutDegreeVertexes.size() != 1) {
            throw new IllegalArgumentException("Digraph should be with root.");
        }
        this.root = zeroOutDegreeVertexes.get(0);

        this.sap = new SAP(synsetsDigraph);
    }

    private void fillSynsets(String synsets) {
        In in = null;
        try {
            in = new In(synsets);
            while (in.hasNextLine()) {
                String synsetLine = in.readLine();
                Integer index = extractIndex(synsetLine);
                String synset = extractSynset(synsetLine);
                wordNetSynsets.add(index, synset);
                for (String synsetNoun : parseSynsets(synset)) {
                    nounIndexes.computeIfAbsent(synsetNoun, s -> new HashSet<>())
                            .add(index);
                }
            }
        } finally {
            if (in != null) in.close();
        }
    }

    private void fillHypernyms(String hypernyms) {
        this.synsetsDigraph = new Digraph(wordNetSynsets.size());

        In in = null;
        try {
            in = new In(hypernyms);
            while (in.hasNextLine()) {
                String hypernymsLine = in.readLine();
                int index = extractIndex(hypernymsLine);
                String hypernym = extractHypernym(hypernymsLine);
                for (String relationshipIndexStr : parseHypernyms(hypernym)) {
                    synsetsDigraph.addEdge(index, Integer.parseInt(relationshipIndexStr));
                }
            }
        } finally {
            if (in != null) in.close();
        }
    }

    private Integer extractIndex(String synsetStr){
        int firstComa = synsetStr.indexOf(",");
        String indexStr = synsetStr.substring(0, firstComa);
        return Integer.parseInt(indexStr);
    }

    private String extractSynset(String synsetStr){
        int firstComa = synsetStr.indexOf(",");
        int secondComa = synsetStr.indexOf(",", firstComa);
        return synsetStr.substring(firstComa+1, secondComa);
    }

    private String extractHypernym(String hypernymsStr){
        int firstComa = hypernymsStr.indexOf(",");
        return hypernymsStr.substring(firstComa+1);
    }

    private String[] parseSynsets(String synset){
        return synset.split(" ");
    }

    private String[] parseHypernyms(String hypernym){
        return hypernym.split(",");
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounIndexes.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null){
            throw new IllegalArgumentException();
        }
        return nounIndexes.containsKey(word);
    }

    public int distance(String nounA, String nounB){
        if (nounA == null || nounB == null){
            throw new IllegalArgumentException();
        }
        if (!isNoun(nounA) || !isNoun(nounB)){
            throw new IllegalArgumentException();
        }
        if (nounA.equals(nounB)) return 0;
        return sap.length(nounIndexes.get(nounA), nounIndexes.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null){
            throw new IllegalArgumentException();
        }
        if (!isNoun(nounA) || !isNoun(nounB)){
            throw new IllegalArgumentException();
        }
        if (nounA.equals(nounB)) return nounA;

        int ancestorIndex = sap.ancestor(nounIndexes.get(nounA), nounIndexes.get(nounB));
        return wordNetSynsets.get(ancestorIndex);
    }

    public static void main(String[] args) {

    }
}
