
package jam.stoch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jam.app.JamLogger;
import jam.lang.JamException;

public final class EventQueue<P extends StochProc> {

    // The number of events held in the queue (its logical size,
    // regardless of the physical size of the underlying array).
    private int size;

    // Elements 1 through "size" of the list "queue" contain the
    // nodes of the complete binary heap; element 0 is unused.
    private final ArrayList<StochEvent<P>> queue;

    // A mapping from each stochastic process to its node: the index
    // of the event for that process in the "queue" list...
    private final Map<Integer, Integer> locator;

    // Special node indexes for the root of the heap and the "null"
    // unused queue element...
    private static final int NULL_NODE = 0;
    private static final int ROOT_NODE = 1;

    private static final int DEFAULT_CAPACITY = 10;

    private EventQueue(int capacity, Collection<StochEvent<P>> events) {
        this.size = 0;
        this.queue = new ArrayList<StochEvent<P>>(capacity);
        this.locator = new HashMap<Integer, Integer>(capacity);

        for (StochEvent<P> event : events)
            addEvent(event);
    }

    // ---------------
    // List management
    // ---------------

    private void ensureCapacity(int node) {
        //
        // To assign an object to the queue element indexed by "node",
        // the physical queue size must be greater than "node"...
        //
        while (queue.size() <= node)
            queue.add(null);
    }

    private StochEvent<P> getNode(int node) {
        return queue.get(node);
    }        

    private int findNode(StochEvent<P> event) {
        return findNode(event.getProcess());
    }

    private int findNode(P proc) {
        Integer pkey = proc.getProcIndex();
        Integer node = locator.get(pkey);

        if (node != null)
            return node.intValue();
        else
            throw JamException.runtime("Queue does not contain process [%d].", pkey.intValue());
    }

    private void setNode(int node, StochEvent<P> event) {
        ensureCapacity(node);
        queue.set(node, event);
        locator.put(event.getProcIndex(), node);
    }

    // ---------------
    // Heap management
    // ---------------

    private int compare(int node1, int node2) {
        return getNode(node1).compareTo(getNode(node2));
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

    private void swap(int j, int k) {
        StochEvent<P> prevj = getNode(j);
        StochEvent<P> prevk = getNode(k);

        setNode(j, prevk);
        setNode(k, prevj);
    }

    /**
     * Creates an empty queue with the default capacity.
     *
     * @return a new empty queue with the default capacity.
     */
    public static <P extends StochProc> EventQueue<P> create() {
        return create(DEFAULT_CAPACITY);
    }

    /**
     * Creates an empty queue with a given initial capacity.
     *
     * @param capacity the initial capacity of the queue (which will
     * adjust as necessary as events are added and/or removed).
     *
     * @return a new empty queue with the specified capacity.
     */
    public static <P extends StochProc> EventQueue<P> create(int capacity) {
        return new EventQueue<P>(capacity, List.of());
    }

    /**
     * Creates a new queue and populates it with events.
     *
     * @param events the initial collection of possible events.
     *
     * @return a new event queue containing the specified events.
     */
    public static <P extends StochProc> EventQueue<P> create(Collection<StochEvent<P>> events) {
        return new EventQueue<P>(events.size(), events);
    }

    /**
     * Adds a new process and its next event to this queue.
     *
     * @param event the event to add.
     *
     * @throws RuntimeException if this queue already contains an
     * event for the process in the input event.
     */
    public void addEvent(StochEvent<P> event) {
        P proc = event.getProcess();

        if (containsProc(proc))
            throw JamException.runtime("Event queue already contains process [%d].", proc.getProcIndex());

        // Increment the logical queue size, add the event at the end
        // of the queue, and percolate upward to restore heap order...
        size++;
        setNode(size, event);
        swim(size);

        assert isOrdered();
        assert containsProc(event.getProcess());
    }

    /**
     * Identifies processes contained in this queue.
     *
     * @param proc the process of interest.
     *
     * @return {@code true} iff this queue contains an event for the
     * specified process.
     */
    public boolean containsProc(P proc) {
        return locator.containsKey(proc.getProcIndex());
    }

    /**
     * Returns the next event to occur for a given process in this
     * queue (not necessarily the next event to occur within the
     * entire system) but does not remove the event or update the
     * queue.
     *
     * @param proc the process of interest.
     *
     * @return the next event to occur for the specified process.
     *
     * @throws RuntimeException unless this queue contains an event
     * for the specified process.
     */
    public StochEvent<P> findEvent(P proc) {
        return getNode(findNode(proc));
    }

    /**
     * Determines whether the underlying heap is properly ordered.  It
     * always should be, of course, and this method is provided to aid
     * with unit testing and internal consistency checks.
     *
     * @return {@code true} iff the underlying heap is properly ordered.
     */
    public boolean isOrdered() {
        JamLogger.info("Validating heap order...");

        for (int parent = ROOT_NODE; isParent(parent); parent++) {
            int child1 = firstChild(parent);
            int child2 = secondChild(parent);

            if (!isOrdered(parent, child1))
                return false;

            if (isNode(child2) && !isOrdered(parent, child2))
                return false;
        }

        return true;
    }

    /**
     * Returns the next event to occur in the stochastic system (the
     * event at the top of this queue) but does not remove the event
     * or update the queue.
     *
     * @return the next event to occur in the stochastic system.
     */
    public StochEvent<P> nextEvent() {
        return getNode(ROOT_NODE);
    }

    /**
     * Removes a process (and its corresponding event) from this queue.
     *
     * @param proc the process to remove.
     *
     * @throws RuntimeException unless this queue contains an event
     * for the specified process.
     */
    public void removeProcess(P proc) {
        //
        // Swap the corresponding event with the event in the last
        // node (at node index "size"), delete the event and the
        // process index, decrement the logical queue size, and
        // restore heap order.
        //
        int node = findNode(proc);
        swap(node, size);

        queue.set(size, null); // Help GC...
        locator.remove(proc.getProcIndex());

        --size;
        sink(node);
        swim(node);

        if (queue.size() > 2 * size) {
            JamLogger.info("Trimming heap size...");
            queue.trimToSize();
        }

        assert isOrdered();
        assert !containsProc(proc);
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
     * @param event the updated event.
     *
     * @throws RuntimeException unless this queue contains an older
     * event for the process in the input event.
     */
    public void updateEvent(StochEvent<P> event) {
        int node = findNode(event);
        setNode(node, event);

        swim(node);
        sink(node);

        assert isOrdered();
        assert containsProc(event.getProcess());
    }

    /**
     * Ensures that the underlying heap is properly ordered. It
     * always should be, of course, and this method is provided
     * to aid with unit testing and internal consistency checks.
     *
     * @throws RuntimeException unless the underlying heap is
     * properly ordered.
     */
    public void validateOrder() {
        if (!isOrdered())
            throw JamException.runtime("Heap order is violated.");
    }

    @Override public String toString() {
        StringBuilder builder = new StringBuilder();

        for (int node = 1; node <= size; node++) {
            builder.append(node);
            builder.append(": ");
            builder.append(getNode(node));
            builder.append("\n");
        }

        return builder.toString();
    }
}
