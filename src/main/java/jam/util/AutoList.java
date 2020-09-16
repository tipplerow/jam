
package jam.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

import jam.lang.ObjectFactory;

/**
 * Provides a random-access {@code List} that grows automatically when
 * the index passed to methods {@code add(int, E)}, {@code get(int)},
 * or {@code set(int, E)} refers to an element beyond the end of the
 * list.
 *
 * <p>The new elements that are created on-demand are populated with
 * either {@code null} or a copy of a <em>template</em> object created
 * by the default constructor for its class.  The behavior is fixed by
 * the constructor method used to create the {@code AutoList}.
 *
 * @param <E> the type of elements contained in the list.
 */
public final class AutoList<E> extends AbstractList<E> implements RandomAccess {
    private final ObjectFactory<E> factory;
    private final List<E> elements = new ArrayList<E>();

    /**
     * Creates an empty auto-list with a {@code null} template.
     *
     * <p>All new elements created on demand to satisfy {@code add},
     * {@code get}, and {@code set} requests will be {@code null}
     * references.
     */
    public AutoList() {
        this(ObjectFactory.forNull());
    }

    /**
     * Creates an empty auto-list with a fixed template object.
     *
     * <p>All new elements created on demand to satisfy {@code add},
     * {@code get}, and {@code set} requests will be copies of the
     * template object created by the default constructor for its
     * class. Pass a {@code null} template if {@code null} on-demand
     * elements are desired.
     *
     * @param template the template object for the new elements that
     * are created on demand.
     */
    public AutoList(E template) {
        this(ObjectFactory.like(template));
    }

    /**
     * Creates an empty auto-list with a fixed object factory.
     *
     * <p>All new elements created on demand to satisfy {@code add},
     * {@code get}, and {@code set} requests will be new instance
     * returned by the object factory.
     *
     * @param factory the object factory for new elements that are
     * created on demand.
     */
    public AutoList(ObjectFactory<E> factory) {
        this.factory = factory;
    }

    /**
     * Creates an empty auto-list with a {@code null} template.
     *
     * <p>All new elements created on demand to satisfy {@code add},
     * {@code get}, and {@code set} requests will be {@code null}
     * references.
     *
     * @param <E> the type of element to be stored in the auto-list.
     *
     * @return a new empty auto-list with a {@code null} template.
     */
    public static <E> AutoList<E> create() {
        return new AutoList<E>();
    }

    /**
     * Creates an empty auto-list with a fixed template object.
     *
     * <p>All new elements created on demand to satisfy {@code add},
     * {@code get}, and {@code set} requests will be copies of the
     * template object created by the default constructor for its
     * class. Pass a {@code null} template if {@code null} on-demand
     * elements are desired.
     *
     * @param <E> the type of element to be stored in the auto-list.
     *
     * @param template the template object for the new elements that
     * are created on demand.
     *
     * @return a new empty auto-list with the specified template object.
     */
    public static <E> AutoList<E> create(E template) {
        return new AutoList<E>(template);
    }

    /**
     * Creates an empty auto-list with a fixed object factory.
     *
     * <p>All new elements created on demand to satisfy {@code add},
     * {@code get}, and {@code set} requests will be new instance
     * returned by the object factory.
     *
     * @param <E> the type of element to be stored in the auto-list.
     *
     * @param factory the object factory for new elements that are
     * created on demand.
     *
     * @return a new empty auto-list with the specified object factory.
     */
    public static <E> AutoList<E> create(ObjectFactory<E> factory) {
        return new AutoList<E>(factory);
    }

    /**
     * Adds a new element at a particular location.
     *
     * <p>If the index refers to an element beyond the current end of
     * the list (with {@code index >= size()}, this method will add
     * new filler elements as described in the class header comments.
     * 
     * @param index the position where the element will be added.
     *
     * @param element the element to add.
     *
     * @throws IllegalArgumentException if the index is negative.
     */
    @Override public void add(int index, E element) {
        requireSize(index);
        elements.add(index, element);
    }

    /**
     * Accesses an element at a particular location.
     *
     * <p>If the index refers to an element beyond the current end of
     * the list (with {@code index >= size()}, this method will add
     * new filler elements as described in the class header comments
     * and return one of the new elements.
     * 
     * @param index the position of the desired element.
     *
     * @return the element at the specified location, or a new element
     * if {@code index >= size()}.
     *
     * @throws IllegalArgumentException if the index is negative.
     */
    @Override public E get(int index) {
        requireIndex(index);
        return elements.get(index);
    }

    private void requireIndex(int index) {
        requireSize(index + 1);
    }

    /**
     * Removes an element at a particular location and shifts any
     * subsequent elements one position to the left.  Rather than
     * throw an exception, this method returns without changing the
     * list if the index refers to a non-existent element.
     *
     * @param index the location of the element to remove.
     */
    @Override public E remove(int index) {
        if (index < size())
            return elements.remove(index);
        else
            return null;
    }

    /**
     * Assigns a new value to the element at a particular location.
     *
     * <p>If the index refers to an element beyond the current end of
     * the list (with {@code index >= size()}, this method will add
     * new filler elements as described in the class header comments.
     * 
     * @param index the position of the element to assign.
     *
     * @param element the new element value.
     *
     * @throws IllegalArgumentException if the index is negative.
     */
    @Override public E set(int index, E element) {
        requireIndex(index);
        return elements.set(index, element);
    }

    /**
     * Changes the number of elements in this auto-list.
     *
     * <p>If the new size is less than the current size, elements will
     * be removed from the end of the list; if the new size is greater
     * than the current size, new elements will be added as described
     * in the class header comments.
     *
     * @param newSize the number of elements desired.
     *
     * @throws IllegalArgumentException if the new size is negative.
     */
    public void resize(int newSize) {
        if (newSize < 0)
            throw new IllegalArgumentException("Negative size.");
        else if (newSize < size())
            truncate(newSize);
        else
            requireSize(newSize);
    }

    private void requireSize(int minSize) {
        while (size() < minSize)
            elements.add(factory.newInstance());
    }

    private void truncate(int newSize) {
        while (size() > newSize)
            remove(size() - 1);
    }

    /**
     * Returns an unmodifiable view of this list.
     *
     * @return an unmodifiable view of this list.
     */
    public List<E> view() {
        return Collections.unmodifiableList(elements);
    }

    @Override public boolean add(E element) {
        return elements.add(element);
    }

    @Override public int size() {
        return elements.size();
    }
}
