
package jam.stoch;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import jam.lang.JamException;

public final class EventQueue<P extends StochProc> {
    //
    // Number of events held in this queue..
    //
    private final int size;

    // Elements 1..N of the array "queue" contain the nodes of the
    // complete binary heap; element 0 is unused.
    //
    private final StochEvent<P>[] queue;

    // Element "k" of the array "point" is a pointer to the location
    // of stochastic process with ordinal index "k" in the queue (the
    // index of the element in the array "queue" containing the event
    // for process "k").
    //
    private final int[] point;

    private static final int ROOT_NODE = 1;

    @SuppressWarnings("unchecked")
    private EventQueue(Collection<StochEvent<P>> events) {
        this.size = events.size();
        this.point = new int[size];
        this.queue = new StochEvent[size + 1]; // The first element is unused...

        fillQueue(events);
        setPoints();
    }

    private void fillQueue(Collection<StochEvent<P>> events) {
        int node = ROOT_NODE;
        Iterator<StochEvent<P>> iterator = events.iterator();

        while (iterator.hasNext()) {
            queue[node] = iterator.next();
            ++node;
        }

        Arrays.sort(queue, ROOT_NODE, queue.length);
        System.out.println(Arrays.toString(queue));
    }

    private void setPoints() {
        for (int node = ROOT_NODE; node <= size; ++node)
            setPoint(node);
        System.out.println(Arrays.toString(point));
    }

    private void setPoint(int node) {
        point[queue[node].getIndex()] = node;
    }

    private int firstChild(int parent) {
        return 2 * parent;
    }

    private int secondChild(int parent) {
        return 2 * parent + 1;
    }

    private int parent(int child) {
        return child / 2;
    }

    private boolean isNode(int node) {
        return ROOT_NODE <= node && node <= size;
    }

    private boolean isRoot(int node) {
        return node == ROOT_NODE;
    }

    private boolean isParent(int node) {
        return firstChild(node) <= size;
    }

    private boolean isLeaf(int node) {
        return firstChild(node) > size;
    }

    private boolean isOrdered(int parent, int child) {
        return queue[parent].compareTo(queue[child]) <= 0;
    }

    private void swap(int i, int j) {
        StochEvent<P> previ = queue[i];
        StochEvent<P> prevj = queue[j];

        queue[i] = prevj;
        queue[j] = previ;

        setPoint(i);
        setPoint(j);
    }

    private void validateOrder(int parent, int child) {
        if (!isOrdered(parent, child))
            throw JamException.runtime("Heap order is violated.");
    }

    void validateOrder() {
        for (int parent = ROOT_NODE; isParent(parent); ++parent) {
            int child1 = firstChild(parent);
            int child2 = secondChild(parent);

            validateOrder(parent, child1);

            if (isNode(child2))
                validateOrder(parent, child2);
        }
    }

    /**
     * Creates a new queue and populates it with events.
     *
     * @param events the initial collection of possible events.
     *
     * @return a new event queue containing the specified events.
     */
    public static <P extends StochProc> EventQueue<P> create(Collection<StochEvent<P>> events) {
        return new EventQueue<P>(events);
    }

    /**
     * Returns the number of events in this queue.
     *
     * @return the number of events in this queue.
     */
    public int size() {
        return size;
    }

    @Override public String toString() {
        return Arrays.toString(queue);
    }
}
