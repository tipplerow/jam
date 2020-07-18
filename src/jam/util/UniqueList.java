
package jam.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Implements a list of unique items: The list is not modified when
 * adding an element that already exists within the list.
 */
public final class UniqueList<E> extends AbstractList<E> {
    private final Set<E> elementSet;
    private final List<E> elementList;

    private UniqueList(List<E> elementList) {
        this.elementList = elementList;
        this.elementSet  = new HashSet<E>(elementList);

        validateElements();
    }

    private void validateElements() {
        if (elementSet.size() != elementList.size())
            throw new IllegalStateException("Duplicate list elements in the private constructor.");
    }

    /**
     * Creates an empty unique list.
     *
     * @param <E> the runtime target class.
     *
     * @param elements the elements to add to the unique list.
     *
     * @return the new unique list.
     */
    @SuppressWarnings("unchecked")
    public static <E> UniqueList<E> create(E... elements) {
        return create(List.of(elements));
    }

    /**
     * Creates an empty unique list with a given capacity.
     *
     * @param <E> the runtime target class.
     *
     * @param capacity the initial capacity of the list.
     *
     * @return the new unique list.
     */
    public static <E> UniqueList<E> create(int capacity) {
        return new UniqueList<E>(new ArrayList<E>(capacity));
    }

    /**
     * Creates a new unique list from an existing collection.
     *
     * @param <E> the runtime target class.
     *
     * @param elements the elements to add to the unique list.
     *
     * @return the new list containing the unique elements in the
     * input collection.
     */
    public static <E> UniqueList<E> create(Collection<? extends E> elements) {
        //
        // The LinkedHashSet will remove duplicates while maintaining
        // the insertion order...
        //
        Set<E> elementSet = new LinkedHashSet<E>(elements);
        List<E> elementList = new ArrayList<E>(elementSet);

        return new UniqueList<E>(elementList);
    }

    @Override public boolean add(E element) {
        if (elementSet.add(element))
            return elementList.add(element);
        else
            return false;
    }

    @Override public void add(int index, E element) {
        if (elementSet.add(element))
            elementList.add(index, element);
    }

    @Override public boolean contains(Object obj) {
        return elementSet.contains(obj);
    }

    @Override public E get(int index) {
        return elementList.get(index);
    }

    @Override public E remove(int index) {
        E element = get(index);
        
        elementList.remove(index);
        elementSet.remove(element);

        return element;
    }

    @Override public boolean remove(Object obj) {
        if (elementList.remove(obj))
            return elementSet.remove(obj);
        else
            return false;
    }

    @Override public int size() {
        return elementList.size();
    }
}
