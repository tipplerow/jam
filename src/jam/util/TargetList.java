
package jam.util;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Represents an immutable sequence of search targets that allows for
 * the matching of sub-sequences that maintain the target order.
 *
 * <p>This class is optimized for running many searches against a long
 * fixed sequence of targets.  The search methods operate in amortized
 * constant time (after the cost of building the underlying index has
 * been recovered).
 */
public final class TargetList<E> extends AbstractList<E> {
    private final List<E> targets;

    // Index of target positions, created on demand when needed...
    private Multimap<E, Integer> positionMap = null;

    private TargetList(List<E> targets) {
        this.targets = Collections.unmodifiableList(targets);
    }

    /**
     * Creates a new target list.
     *
     * @param <E> the runtime target class.
     *
     * @param targets the targets in the list.
     *
     * @return the new target list.
     */
    @SuppressWarnings("unchecked")
    public static <E> TargetList<E> create(E... targets) {
        return create(List.of(targets));
    }

    /**
     * Creates a new target list.
     *
     * @param <E> the runtime target class.
     *
     * @param targets the targets in the list.
     *
     * @return the new target list.
     */
    public static <E> TargetList<E> create(List<E> targets) {
        return new TargetList<E>(targets);
    }

    /**
     * Determines whether this list contains a specific target.
     *
     * <p>This method executes in <em>amortized constant time</em>.
     *
     * @param target the target to search for.
     *
     * @return {@code true} iff this list contains the specified
     * target.
     */
    public boolean containsTarget(E target) {
        return contains(target);
    }

    /**
     * Determines whether this list contains a sequence of targets (as
     * a sublist of this list).
     *
     * <p>This method executes in <em>amortized</em> {@code O(n)} time,
     * where {@code n} is the length of the input list.
     *
     * @param subList the target sequence to search for.
     *
     * @return {@code true} iff the specified list is a sublist within
     * this list.
     */
    public boolean containsTargets(List<E> subList) {
        if (subList.isEmpty())
            return false;

        if (subList.size() > targets.size())
            return false;

        Collection<Integer> subListHeads = find(subList.get(0));

        for (int subListHead : subListHeads)
            if (containsSubList(subList, subListHead))
                return true;

        return false;
    }

    private Collection<Integer> find(E target) {
        return getPositionMap().get(target);
    }

    private synchronized Multimap<E, Integer> getPositionMap() {
        if (positionMap == null)
            positionMap = createPositionMap();

        return positionMap;
    }

    private Multimap<E, Integer> createPositionMap() {
        Multimap<E, Integer> map = HashMultimap.create();

        for (int index = 0; index < targets.size(); ++index)
            map.put(targets.get(index), index);

        return map;
    }

    private boolean containsSubList(List<E> subList, int subListHead) {
        //
        // The first element of the sublist already matches the target
        // as position "subListHead"...
        //
        if (subList.size() > targets.size() - subListHead)
            return false;

        for (int subListIndex = 1; subListIndex < subList.size(); ++subListIndex) {
            int targetIndex = subListIndex + subListHead;

            if (!targets.get(targetIndex).equals(subList.get(subListIndex)))
                return false;
        }

        return true;
    }

    @Override public boolean contains(Object obj) {
        return getPositionMap().containsKey(obj);
    }

    @Override public E get(int index) {
        return targets.get(index);
    }

    @Override public int size() {
        return targets.size();
    }
}
