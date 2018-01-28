
package jam.util;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

import jam.lang.ObjectUtil;

/**
 * Provides efficient (in time and space) implementations for lists
 * with fixed lengths.
 *
 * <p>A {@code FixedList} is immutable: the length of the list and its
 * components are fixed and cannot be changed.
 *
 * @param E the type of elements contained in the list.
 */
public abstract class FixedList<E> extends AbstractList<E> implements RandomAccess {

    /**
     * Returns an empty fixed list.
     *
     * @param <E> the list element type.
     *
     * @return an empty fixed list.
     */
    @SuppressWarnings("unchecked")
    public static <E> FixedList<E> empty() {
        return (FixedList<E>) EMPTY;
    }

    /**
     * Wraps items in a fixed list using the most efficient
     * implementation for the item count.
     *
     * @param <E> the type of element to wrap.
     *
     * @param items the items to wrap.
     *
     * @return a fixed list containing the specified items.
     */
    @SuppressWarnings("unchecked")
    public static <E> FixedList<E> create(E... items) {
        switch (items.length) {
        case 0:
            return empty();

        case 1:
            return singleList(items[0]);

        case 2:
            return pairList(items[0], items[1]);

        default:
            return arrayList(items, true);
        }
    }

    /**
     * Wraps items in a fixed list using the most efficient
     * implementation for the item count.
     *
     * @param <E> the type of element to wrap.
     *
     * @param items the items to wrap.
     *
     * @return a fixed list containing the specified items.
     */
    @SuppressWarnings("unchecked")
    public static <E> FixedList<E> create(List<E> items) {
        switch (items.size()) {
        case 0:
            return empty();

        case 1:
            return singleList(items.get(0));

        case 2:
            return pairList(items.get(0), items.get(1));

        default:
            return arrayList((E[]) items.toArray(), false);
        }
    }

    /**
     * Wraps items in a fixed list.
     *
     * @param <E> the type of element to wrap.
     *
     * @param items the items to wrap.
     *
     * @return a fixed list containing the specified items.
     */
    @SuppressWarnings("unchecked")
    public static <E> FixedList<E> create(Collection<E> items) {
        if (items.size() <= 2)
            return create(new ArrayList<E>(items));
        else
            return arrayList((E[]) items.toArray(), false);
    }

    /**
     * Appends another fixed list to this and returns the result in a
     * (logically) new fixed list; this list is unchanged.
     *
     * @param that the list to append.
     *
     * @return the concatenation of this list and the input list.
     */
    public FixedList<E> cat(FixedList<E> that) {
        switch (that.size()) {
        case 0:
            return this;

        case 1:
            return cat1(that.get(0));

        case 2:
            return cat2(that.get(0), that.get(1));

        default:
            //
            // Any fixed list with length greater than two must be an ArrayFixedList...
            //
            return catList((ArrayFixedList<E>) that);
        }
    }

    protected abstract FixedList<E> cat1(E item);
    protected abstract FixedList<E> cat2(E first, E second);
    protected abstract FixedList<E> catList(ArrayFixedList<E> that);

    @Override public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    // ---------------------------
    // The one and only empty list
    // ---------------------------

    private static final FixedList<Object> EMPTY = new EmptyFixedList<Object>();

    private static final class EmptyFixedList<E> extends FixedList<E> {
        private EmptyFixedList() {
        }

        @Override protected FixedList<E> cat1(E item) {
            return singleList(item);
        }

        @Override protected FixedList<E> cat2(E first, E second) {
            return pairList(first, second);
        }

        @Override protected FixedList<E> catList(ArrayFixedList<E> list) {
            return list;
        }

        @Override public boolean contains(Object o) {
            return false;
        }

        @Override public E get(int index) {
            throw new IndexOutOfBoundsException();
        }

        @Override public int size() {
            return 0;
        }
    }

    // --------------------------------------
    // Fixed list containing exactly one item
    // --------------------------------------

    private static <E> FixedList<E> singleList(E item) {
        return new SingleFixedList<E>(item);
    }

    private static final class SingleFixedList<E> extends FixedList<E> {
        private final E item;

        private SingleFixedList(E item) {
            this.item = item;
        }

        @Override protected FixedList<E> cat1(E newItem) {
            return pairList(this.item, newItem);
        }

        @SuppressWarnings("unchecked")
        @Override protected FixedList<E> cat2(E firstNew, E secondNew) {
            return arrayList(this.item, firstNew, secondNew);
        }

        @SuppressWarnings("unchecked")
        @Override protected FixedList<E> catList(ArrayFixedList<E> that) {
            return arrayList((E[]) new Object[] { item }, that.items);
        }

        @Override public boolean contains(Object object) {
            return ObjectUtil.equals(item, object);
        }

        @Override public E get(int index) {
            if (index == 0)
                return item;
            else
                throw new IndexOutOfBoundsException();
        }

        @Override public int size() {
            return 1;
        }
    }

    // ---------------------------------------
    // Fixed list containing exactly two items
    // ---------------------------------------

    private static <E> FixedList<E> pairList(E first, E second) {
        return new PairFixedList<E>(first, second);
    }

    private static final class PairFixedList<E> extends FixedList<E> {
        private final E first;
        private final E second;

        private PairFixedList(E first, E second) {
            this.first = first;
            this.second = second;
        }

        @SuppressWarnings("unchecked")
        @Override protected FixedList<E> cat1(E newItem) {
            return arrayList(this.first, this.second, newItem);
        }

        @SuppressWarnings("unchecked")
        @Override protected FixedList<E> cat2(E firstNew, E secondNew) {
            return arrayList(this.first, this.second, firstNew, secondNew);
        }

        @SuppressWarnings("unchecked")
        @Override protected FixedList<E> catList(ArrayFixedList<E> that) {
            return arrayList((E[]) new Object[] { first, second }, that.items);
        }

        @Override public boolean contains(Object object) {
            return ObjectUtil.equals(first, object) || ObjectUtil.equals(second, object);
        }

        @Override public E get(int index) {
            if (index == 0)
                return first;
            else if (index == 1)
                return second;
            else
                throw new IndexOutOfBoundsException();
        }

        @Override public int size() {
            return 2;
        }
    }

    // -----------------------------
    // Fixed list backed by an array
    // -----------------------------

    @SuppressWarnings("unchecked")
    private static <E> FixedList<E> arrayList(E... items) {
        return arrayList(items, false);
    }

    @SuppressWarnings("unchecked")
    private static <E> FixedList<E> arrayList(E[] array1, E[] array2) {
        E[] items = (E[]) new Object[array1.length + array2.length];

        System.arraycopy(array1, 0, items, 0, array1.length);
        System.arraycopy(array2, 0, items, array1.length, array2.length);

        return arrayList(items, false);
    }

    private static <E> FixedList<E> arrayList(E[] items, boolean copy) {
        return new ArrayFixedList<E>(items, copy);
    }

    private static final class ArrayFixedList<E> extends FixedList<E> {
        private final E[] items;

        private ArrayFixedList(E[] items, boolean copy) {
            if (copy)
                this.items = Arrays.copyOf(items, items.length);
            else
                this.items = items;
        }

        @SuppressWarnings("unchecked")
        @Override protected FixedList<E> cat1(E item) {
            return arrayList(this.items, (E[]) new Object[] { item });
        }

        @SuppressWarnings("unchecked")
        @Override protected FixedList<E> cat2(E firstNew, E secondNew) {
            return arrayList(this.items, (E[]) new Object[] { firstNew, secondNew });
        }

        @Override protected FixedList<E> catList(ArrayFixedList<E> that) {
            return arrayList(this.items, that.items);
        }

        @Override public E get(int index) {
            return items[index];
        }

        @Override public int size() {
            return items.length;
        }
    }
}
