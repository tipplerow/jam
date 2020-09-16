
package jam.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements a list of items that are computed recursively on demand.
 *
 * <p>When the {@code get(int)} method is called with an index for an
 * element that does not exist, the recursive list computes all of the
 * required elements in order using the user-supplied abstract method
 * {@code compute(int)} and adds them to the list.  A recursive list
 * cannot be modified in any manner other than the automatic addition
 * of elements via recursive computation.
 *
 * @param E the type of elements contained in the list.
 */
public abstract class RecursiveList<E> extends AbstractList<E> {
    private final List<E> elements = new ArrayList<E>();

    /**
     * Creates an empty recursive list.
     */
    protected RecursiveList() {
    }

    /**
     * Computes the element at a specified location.
     *
     * <p>The implementation of this method may assume that all
     * previous elements (with lower indexes) are accessible by
     * calling {@code get(k)} (with {@code k < index}).
     *
     * @param index the index of the desired element.
     *
     * @return the new element for the specified location.
     */
    protected abstract E compute(int index);

    /**
     * Accesses an element at a particular location.
     *
     * <p>If the index refers to an element beyond the end of the list
     * (with {@code index >= size()}, this method computes all of the
     * required elements (in order) by calling {@code compute(int)}
     * and adding them to the list.
     * 
     * @param index the position of the desired element.
     *
     * @return the element at the specified location, computed
     * recursively on-demand if necessary.
     *
     * @throws IllegalArgumentException if the index is negative.
     */
    @Override public E get(int index) {
        if (index < 0)
            throw new IllegalArgumentException("Negative list index.");

        while (index >= size()) {
            //
            // The requested element has not been computed. Compute
            // and add all of the missing required elements...
            //
            elements.add(compute(size()));
        }

        return elements.get(index);
    }

    @Override public int size() {
        return elements.size();
    }
}
