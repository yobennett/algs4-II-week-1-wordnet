import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WordNet {

    private static final int ROOT_INDEX = 38003;

    private final Synset[] synsets;
    private final Digraph digraph;
    private final Synset root;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new NullPointerException();
        }

        this.synsets = createSynsets(synsets);
        this.digraph = new Digraph(this.synsets.length);
        this.root = this.synsets[ROOT_INDEX];
        addEdges(hypernyms);
    }

    private Synset[] createSynsets(String synsets) {
        In in = new In(synsets);
        List<Synset> result = new ArrayList<Synset>();
        while (in.hasNextLine()) {
            String line = in.readLine();

            if (line == null || line.isEmpty()) {
                break;
            }

            String[] parts = line.split(",");

            if (parts.length >= 2) {
                int id = Integer.parseInt(parts[0]); // id
                String[] nouns = parts[1].split(" "); // nouns

                Synset synset = new Synset(id, nouns);
                result.add(synset);
            }
        }
        return result.toArray(new Synset[result.size()]);
    }

    private class Synset {

        final int id;
        final String[] nouns;

        Synset(int id, String[] nouns){
            this.id = id;
            this.nouns = nouns;
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
        return Collections.emptySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new NullPointerException();
        }
        return false;
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new NullPointerException();
        }
        return -1;
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new NullPointerException();
        }
        return "";
    }

    public static void main(String[] args) {
        new WordNet(args[0], args[1]);
    }

}
