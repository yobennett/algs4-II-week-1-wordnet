import java.util.Arrays;

public class SAP {

    private static final int NOT_FOUND = -1;

    private final Digraph G;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        this.G = G;
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        validateVertices(Arrays.asList(v, w));
        return length(Arrays.asList(v), Arrays.asList(w));
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        return ancestor(Arrays.asList(v), Arrays.asList(w));
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        validateVertices(v);
        validateVertices(w);

        BreadthFirstDirectedPaths vbfs = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths wbfs = new BreadthFirstDirectedPaths(G, w);

        int length = Integer.MAX_VALUE;

        for (int vertex = 0; vertex < G.V(); vertex++) {
            if (vbfs.hasPathTo(vertex) && wbfs.hasPathTo(vertex)) {
                if (length > (vbfs.distTo(vertex) + wbfs.distTo(vertex))) {
                    length = vbfs.distTo(vertex) + wbfs.distTo(vertex);
                }
            }
        }

        if (length == Integer.MAX_VALUE) {
            return NOT_FOUND;
        }
        return length;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        validateVertices(v);
        validateVertices(w);

        BreadthFirstDirectedPaths vbfs = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths wbfs = new BreadthFirstDirectedPaths(G, w);

        int sap = Integer.MAX_VALUE;
        int ancestor = NOT_FOUND;

        for (int vertex = 0; vertex < G.V(); vertex++) {
            if (vbfs.hasPathTo(vertex) && wbfs.hasPathTo(vertex)) {
                if (sap > (vbfs.distTo(vertex) + wbfs.distTo(vertex))) {
                    sap = vbfs.distTo(vertex) + wbfs.distTo(vertex);
                    ancestor = vertex;
                }
            }
        }

        return ancestor;
    }

    // throw an IndexOutOfBoundsException unless 0 <= v < V
    private void validateVertex(int v) {
        if (v < 0 || v >= G.V())
            throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (G.V()-1));
    }

    private void validateVertices(Iterable<Integer> i) {
        for (int v : i) {
            validateVertex(v);
        }
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
