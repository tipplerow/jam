
package jam.stoch;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import jam.lang.JamException;

public final class FixedEventQueue<P extends StochProc> {
    //
    // Number of events held in the queue.
    //
    private final int size;

    // Elements 1 through N of the array "queue" contain the nodes of
    // the complete binary heap; element 0 is unused.
    //
    private final StochEvent<P>[] queue;

    // Element "k" of the array "point" is a pointer to the location
    // of stochastic process with ordinal index "k" in the queue (the
    // index of the element in the array "queue" containing the event
    // for process "k").
    //
    private final int[] point;

    // Special node indexes for the root of the heap and the "null"
    // unused queue element...
    private static final int NULL_NODE = 0;
    private static final int ROOT_NODE = 1;

    @SuppressWarnings("unchecked")
    private FixedEventQueue(Collection<StochEvent<P>> events) {
        this.size = events.size();
        this.point = new int[size];
        this.queue = new StochEvent[size + 1]; // The first element is unused...

        fillQueue(events);

        initPoints();
        assignPoints();
        validatePoints();
    }

    // -------------------
    // Constructor helpers
    // -------------------

    private void fillQueue(Collection<StochEvent<P>> events) {
        int node = ROOT_NODE;
        Iterator<StochEvent<P>> iterator = events.iterator();

        while (iterator.hasNext()) {
            queue[node] = iterator.next();
            ++node;
        }

        Arrays.sort(queue, ROOT_NODE, queue.length);
    }

    private void initPoints() {
        //
        // Set all to pointers to the "null" node...
        //
        Arrays.fill(point, NULL_NODE);
    }

    private void assignPoints() {
        for (int node = ROOT_NODE; node <= size; ++node)
            assignPoint(node);
    }

    private void assignPoint(int node) {
        int index = queue[node].getProcIndex();
        validateEventIndex(index);
        point[index] = node;
    }

    private void validateEventIndex(int index) {
        if (index < 0 || index >= size)
            throw JamException.runtime("Invalid event index: [%d].", index);
    }

    private void validatePoints() {
        for (int index = 0; index < point.length; ++index)
            if (point[index] == NULL_NODE)
                throw JamException.runtime("Processes must be numbered from 0 to N - 1.");
    }

    // ---------------
    // Heap management
    // ---------------

    private int compare(int node1, int node2) {
        return queue[node1].compareTo(queue[node2]);
    }

    private boolean isOrdered(int parent, int child) {
        return compare(parent, child) <= 0;
    }

    private int parent(int child) {
        return child / 2;
    }

    private int firstChild(int parent) {
        return 2 * parent;
    }

    private int secondChild(int parent) {
        return 2 * parent + 1;
    }

    private int nextChild(int parent) {
        int child1 = firstChild(parent);
        int child2 = secondChild(parent);

        if (!isNode(child1))
            return -1;

        if (!isNode(child2))
            return child1;

        if (compare(child1, child2) <= 0)
            return child1;
        else
            return child2;
    }

    private boolean isNode(int node) {
        return ROOT_NODE <= node && node <= size;
    }

    private boolean isRoot(int node) {
        return node == ROOT_NODE;
    }

    private boolean isParent(int node) {
        return isNode(firstChild(node));
    }

    private boolean isLeaf(int node) {
        return !isParent(node);
    }

    private int find(StochEvent<P> event) {
        return point[event.getProcIndex()];
    }

    private void sink(int node) {
        while (!isLeaf(node)) {
            int parent = node;
            int child  = nextChild(parent);

            if (isOrdered(parent, child))
                break;

            swap(parent, child);
            node = child;
        }
    }

    private void swim(int node) {
        int child  = node;
        int parent = parent(child);

        while (!isRoot(child) && !isOrdered(parent, child)) {
            swap(parent, child);

            child  = parent;
            parent = parent(child);
        }
    }

    private void swap(int i, int j) {
        StochEvent<P> previ = queue[i];
        StochEvent<P> prevj = queue[j];

        queue[i] = prevj;
        queue[j] = previ;

        assignPoint(i);
        assignPoint(j);
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
    public static <P extends StochProc> FixedEventQueue<P> create(Collection<StochEvent<P>> events) {
        return new FixedEventQueue<P>(events);
    }

    /**
     * Returns the next event for a given process in this queue (not
     * necessarily the next event to occur in the system) but does not
     * remove the event or update the queue.
     *
     * @param proc the process of interest.
     *
     * @return the next event for the specified process.
     */
    public StochEvent<P> findEvent(P proc) {
        return queue[point[proc.getProcIndex()]];
    }

    /**
     * Returns the next event to occur in the stochastic system (the
     * event at the top of this queue) but does not remove the event
     * or update the queue.
     *
     * @return the next event to occur in the stochastic system.
     */
    public StochEvent<P> nextEvent() {
        return queue[ROOT_NODE];
    }

    /**
     * Returns the number of events in this queue.
     *
     * @return the number of events in this queue.
     */
    public int size() {
        return size;
    }

    /**
     * Updates an event in this queue after the time of its next
     * occurrence has changed.
     *
     * @param event the event to update.
     */
    public void updateEvent(StochEvent<P> event) {
        int node = find(event);
        queue[node] = event;

        swim(node);
        sink(node);
    }

    @Override public String toString() {
        StringBuilder builder = new StringBuilder();

        for (int node = 1; node <= size; ++node) {
            builder.append(node);
            builder.append(": ");
            builder.append(queue[node]);
            builder.append("\n");
        }

        builder.append(Arrays.toString(point));
        return builder.toString();
    }
}
