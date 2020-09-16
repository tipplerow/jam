
package jam.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.RandomAccess;

/**
 * Provides a read-only, random-access view of a {@code List}.
 *
 * <p>Methods returning unmodifiable lists may use this class as the
 * return type (in place of {@code Collections.unmodifiableList}) to
 * explicit mark the list as unmodifiable.
 *
 * @param <E> the type of elements contained in the list.
 */
public final class ListView<E> extends AbstractList<E> implements RandomAccess {
    private final List<E> elements;

    private ListView(List<E> elements) {
        if (elements instanceof RandomAccess)
            this.elements = elements;
        else
            this.elements = new ArrayList<E>(elements);
    }

    /**
     * Wraps an array in a read-only view.
     *
     * @param <E> the type of element to wrap.
     *
     * @param items the items to wrap.
     *
     * @return the read-only view.
     */
    @SuppressWarnings("unchecked")
    public static <E> ListView<E> create(E... items) {
        return new ListView<E>(Arrays.asList(items));
    }

    /**
     * Wraps a list in a read-only view.
     *
     * @param <E> the type of element to wrap.
     *
     * @param list the list to wrap.
     *
     * @return the read-only view.
     */
    public static <E> ListView<E> create(List<E> list) {
        return new ListView<E>(list);
    }

    @Override public E get(int index) {
        return elements.get(index);
    }

    @Override public int size() {
        return elements.size();
    }
}
