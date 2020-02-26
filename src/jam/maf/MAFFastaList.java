
package jam.maf;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Provides an immutable list of {@code MAFFastaRecord}s.
 */
public final class MAFFastaList extends AbstractList<MAFFastaRecord> {
    private final List<MAFFastaRecord> elements;

    private MAFFastaList(Collection<MAFFastaRecord> records) {
        this.elements = new ArrayList<MAFFastaRecord>(records);
    }

    /**
     * The single empty list.
     */
    public static final MAFFastaList EMPTY = new MAFFastaList(List.of());

    /**
     * Creates a new immutable {@code MAFFastaRecord} list.
     *
     * @param records the records that compose the list.
     *
     * @return a new immutable {@code MAFFastaRecord} list with
     * records arranged in the order returned by the iterator of
     * the input collection.
     */
    public static MAFFastaList create(Collection<MAFFastaRecord> records) {
        return new MAFFastaList(records);
    }

    @Override public MAFFastaRecord get(int index) {
        return elements.get(index);
    }

    @Override public int size() {
        return elements.size();
    }
}
