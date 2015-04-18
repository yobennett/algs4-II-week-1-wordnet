public class Outcast {

    private final WordNet wordnet;

    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {

        int maxDistance = 0;
        String outcast = "";

        for (String noun : nouns) {
            int distance = 0;
            for (String n : nouns) {
                distance += wordnet.distance(noun, n);
            }
            if (maxDistance < distance) {
                maxDistance = distance;
                outcast = noun;
            }
        }

        return outcast;
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
