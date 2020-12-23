
package jam.stoch;

import jam.lang.JamException;

/**
 * Represents an edge in a directed dependency graph: a link between a
 * predecessor process and a successor process whose rate changes when
 * the predecessor process occurs.
 */
public final class RateLink<P extends StochProc> {
    private final P predecessor;
    private final P successor;

    private RateLink(P predecessor, P successor) {
        validate(predecessor, successor);
        this.predecessor = predecessor;
        this.successor = successor;
    }

    /**
     * Creates a new link between processs: when the predecessor
     * occurs, it triggers a change in the rate of the successor
     * process.
     *
     * @param predecessor the predecessor process.
     *
     * @param successor the successor process.
     *
     * @return the new process link for the specified processs.
     *
     * @throws RuntimeException if the predecessor and successor are
     * the same process.
     */
    public static <P extends StochProc> RateLink<P> link(P predecessor, P successor) {
        return new RateLink<P>(predecessor, successor);
    }

    /**
     * Ensures that the predecessor and successor are separate and
     * distinct processes.
     *
     * @param predecessor the predecessor process.
     *
     * @param successor the successor process.
     *
     * @throws RuntimeException if the predecessor and successor are
     * the same process.
     */
    public static <P extends StochProc> void validate(P predecessor, P successor) {
        if (predecessor.equals(successor))
            throw JamException.runtime("Linked processes must be distinct.");
    }

    /**
     * Returns the predecessor process: the process, when it occurs,
     * that triggers a change in the rate of the successor process.
     *
     * @return the predecessor process.
     */
    public P getPredecessor() {
        return predecessor;
    }

    /**
     * Returns the successor process: the process whose rate changes
     * in response to the occurrence of the predecessor process.
     *
     * @return the successor process.
     */
    public P getSuccessor() {
        return successor;
    }

    @Override public boolean equals(Object obj) {
        return (obj instanceof RateLink) && equalsLink((RateLink) obj);
    }

    private boolean equalsLink(RateLink that) {
        return this.predecessor.equals(that.predecessor) && this.successor.equals(that.successor);
    }

    @Override public int hashCode() {
        return predecessor.hashCode() + 31 * successor.hashCode();
    }

    @Override public String toString() {
        return String.format("RateLink(%s => %s)", predecessor, successor);
    }
}
