import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WordNet {

    private final Map<String, Set<Integer>> wordToSynsetIds;
    private Map<Integer, String> synsets;
    private final SAP sap;
    private final Digraph digraph;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new NullPointerException();
        }

        this.synsets = new HashMap<Integer, String>();
        this.wordToSynsetIds = buildSynsets(new In(synsets));
        this.digraph = buildDigraph(new In(hypernyms));
        this.sap = new SAP(digraph);

        validateRootedDAG();
    }

    private void validateRootedDAG() {
        if (!isRootedDAG()) {
            throw new IllegalArgumentException("Not rooted DAG");
        }
    }

    private boolean isRootedDAG() {
        DirectedCycle cycle = new DirectedCycle(digraph);

        int numRoots = 0;
        for (int v = 0; v < digraph.V(); v++) {
            if (digraph.outdegree(v) == 0) {
                numRoots += 1;
            }
        }

        return !cycle.hasCycle() && (numRoots == 1);
    }

    private Map<String, Set<Integer>> buildSynsets(In input) {
        Map<String, Set<Integer>> result = new HashMap<String, Set<Integer>>();
        while (input.hasNextLine()) {
            String line = input.readLine();
            String[] parts = line.split(",");

            int id = Integer.parseInt(parts[0]);
            List<String> synonyms = Arrays.asList(parts[1].split(" "));

            for (String synonym : synonyms) {
                if (result.containsKey(synonym)) {
                    result.get(synonym).add(id);
                } else {
                    Set<Integer> ids = new HashSet<Integer>();
                    ids.add(id);
                    result.put(synonym, ids);
                }
            }

            synsets.put(id, parts[1]);
        }
        return result;
    }

    private Digraph buildDigraph(In in) {
        Digraph graph = new Digraph(synsets.size());
        while (in.hasNextLine()) {
            String line = in.readLine();
            String[] parts = line.split(",");

            int synsetId = Integer.parseInt(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                int hypernymId = Integer.parseInt(parts[i]);
                graph.addEdge(synsetId, hypernymId);
            }
        }
        return graph;
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return Collections.unmodifiableSet(wordToSynsetIds.keySet());
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new NullPointerException();
        }
        return wordToSynsetIds.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new NullPointerException();
        }
        validateNouns(Arrays.asList(nounA, nounB));
        Iterable<Integer> nounsA = wordToSynsetIds.get(nounA);
        Iterable<Integer> nounsB = wordToSynsetIds.get(nounB);
        return sap.length(nounsA, nounsB);
    }

    // a synset (second field of synsets.txt) that is
    // the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new NullPointerException();
        }
        validateNouns(Arrays.asList(nounA, nounB));
        Set<Integer> nounsA = wordToSynsetIds.get(nounA);
        Set<Integer> nounsB = wordToSynsetIds.get(nounB);
        int ancestorIndex = sap.ancestor(nounsA, nounsB);
        return synsets.get(ancestorIndex);
    }

    private void validateNouns(Iterable<String> nouns) {
        for (String noun : nouns) {
            if (!isNoun(noun)) {
                throw new IllegalArgumentException();
            }
        }
    }

    public static void main(String[] args) {
        WordNet wordNet = new WordNet(args[0], args[1]);

        String word1 = "b";
        String word2 = "slow-wittedness";
        int distance = wordNet.distance(word1, word2);
        assert distance == 9 : "incorrect distance (expected="
                + 9 + " was=" + distance + ")";
        System.out.println("distance between "
                + word1 + " and " + word2 + ": "
                + distance);
    }

}
