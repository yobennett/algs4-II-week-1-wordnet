import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordNet {

    private static final int ROOT_INDEX = 38003;

    private final Map<String, List<Integer>> nouns;
    private final List<String> synsets;
    private final Digraph digraph;
    private final String root;
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new NullPointerException();
        }

        this.nouns = new HashMap<String, List<Integer>>();
        this.synsets = new ArrayList<String>();

        createSynsets(synsets);
        this.digraph = new Digraph(this.synsets.size());
        this.root = this.synsets.get(ROOT_INDEX);
        addEdges(hypernyms);
        this.sap = new SAP(digraph);
    }

    private void createSynsets(String inSynsets) {
        In in = new In(inSynsets);
        while (in.hasNextLine()) {
            String line = in.readLine();

            if (line == null || line.isEmpty()) {
                break;
            }

            String[] parts = line.split(",");

            if (parts.length >= 2) {

                int id = Integer.parseInt(parts[0]);
                String nounsPart = parts[1];

                this.synsets.add(nounsPart);

                for (String noun : nounsPart.split("\\s")) {
                    List<Integer> bag = nouns.get(noun);
                    if (bag == null) {
                        bag = new ArrayList<Integer>();
                        nouns.put(noun, bag);
                    }
                    bag.add(id);
                }

            }
        }
    }

    private void addEdges(String hypernyms) {
        In in = new In(hypernyms);
        while (in.hasNextLine()) {
            String line = in.readLine();

            if (line == null || line.isEmpty()) {
                break;
            }

            String[] parts = line.split(",");

            int synsetId = Integer.parseInt(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                int hypernymId = Integer.parseInt(parts[i]);
                addEdge(hypernymId, synsetId);
            }
        }
    }

    private void addEdge(int v, int w) {
        digraph.addEdge(v, w);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return Collections.unmodifiableSet(nouns.keySet());
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new NullPointerException();
        }
        return nouns.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new NullPointerException();
        }
        if (!isNoun(nounA) && !isNoun(nounB)) {
            throw new IllegalArgumentException();
        }
        List<Integer> nounsA = nouns.get(nounA);
        List<Integer> nounsB = nouns.get(nounB);
        return sap.length(nounsA, nounsB);
    }

    // a synset (second field of synsets.txt) that is
    // the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new NullPointerException();
        }
        List<Integer> nounsA = nouns.get(nounA);
        List<Integer> nounsB = nouns.get(nounB);
        int ancestorIndex = sap.ancestor(nounsA, nounsB);
        return synsets.get(ancestorIndex);
    }

    public static void main(String[] args) {
        WordNet wordNet = new WordNet(args[0], args[1]);

        String word1 = "unadaptability";
        String word2 = "slow-wittedness";
        int distance = wordNet.distance(word1, word2);
        assert distance == 9 : "incorrect distance (expected="
                + 9 + " was=" + distance + ")";
        System.out.println("distance between "
                + word1 + " and " + word2 + ": "
                + distance);
    }

}
