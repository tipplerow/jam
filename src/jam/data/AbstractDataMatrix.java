
package jam.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jam.lang.JamException;
import jam.math.DoubleComparator;

/**
 * Provides a partial implementation of the {@code DataMatrix}
 * interface that is independent of the element storage scheme
 * (dense or sparse).
 */
public abstract class AbstractDataMatrix<ROWTYPE, COLTYPE> implements DataMatrix<ROWTYPE, COLTYPE> {
    /**
     * The fixed row keys in index order.
     */
    protected final List<ROWTYPE> rowKeyList;

    /**
     * The fixed column keys in index order.
     */
    protected final List<COLTYPE> colKeyList;

    /**
     * Mapping from row key to row index.
     */
    protected final Map<ROWTYPE, Integer> rowKeyMap;

    /**
     * Mapping from column key to column index.
     */
    protected final Map<COLTYPE, Integer> colKeyMap;

    /**
     * Creates a new data matrix with fixed row and column keys.
     *
     * @param rowKeyList the row keys.
     *
     * @param colKeyList the column keys.
     *
     * @param copy whether or not to make deep copies of the lists.
     *
     * @throws IllegalArgumentException if either key list is empty or
     * contains duplicates.
     */
    protected AbstractDataMatrix(List<ROWTYPE> rowKeyList, List<COLTYPE> colKeyList, boolean copy) {
        if (copy) {
            this.rowKeyList = Collections.unmodifiableList(new ArrayList<ROWTYPE>(rowKeyList));
            this.colKeyList = Collections.unmodifiableList(new ArrayList<COLTYPE>(colKeyList));
        }
        else {
            this.rowKeyList = Collections.unmodifiableList(rowKeyList);
            this.colKeyList = Collections.unmodifiableList(colKeyList);
        }

        this.rowKeyMap = mapKeyList(rowKeyList);
        this.colKeyMap = mapKeyList(colKeyList);
    }

    private static <V> Map<V, Integer> mapKeyList(List<V> keyList) {
        if (keyList.isEmpty())
            throw new IllegalArgumentException("Empty key list.");

        Map<V, Integer> keyMap = new HashMap<V, Integer>(keyList.size());

        for (int index = 0; index < keyList.size(); ++index)
            if (keyMap.put(keyList.get(index), index) != null)
                throw JamException.runtime("Duplicate key: [%s].", keyList.get(index));

        return Collections.unmodifiableMap(keyMap);
    }

    @Override public int colIndex(COLTYPE colKey) {
        Integer index = colKeyMap.get(colKey);

        if (index != null)
            return index.intValue();
        else
            return KEY_MISSING;
    }

    @Override public COLTYPE colKey(int colIndex) {
        return colKeyList.get(colIndex);
    }

    @Override public List<COLTYPE> colKeyList() {
        return colKeyList;
    }

    @Override public Set<COLTYPE> colKeySet() {
        return colKeyMap.keySet();
    }

    @Override public int rowIndex(ROWTYPE rowKey) {
        Integer index = rowKeyMap.get(rowKey);

        if (index != null)
            return index.intValue();
        else
            return KEY_MISSING;
    }

    @Override public ROWTYPE rowKey(int rowIndex) {
        return rowKeyList.get(rowIndex);
    }

    @Override public List<ROWTYPE> rowKeyList() {
        return rowKeyList;
    }

    @Override public Set<ROWTYPE> rowKeySet() {
        return rowKeyMap.keySet();
    }

    @Override public boolean equals(Object that) {
        return (that instanceof DataMatrix) && equalsDataMatrix((DataMatrix) that);
    }

    private boolean equalsDataMatrix(DataMatrix that) {
        //
        // Must have identical dimensions...
        //
        if (this.nrow() != that.nrow())
            return false;

        if (this.ncol() != that.ncol())
            return false;

        // Must have identical keys in the same order...
        for (int rowIndex = 0; rowIndex < nrow(); ++rowIndex)
            if (!this.rowKey(rowIndex).equals(that.rowKey(rowIndex)))
                return false;

        for (int colIndex = 0; colIndex < ncol(); ++colIndex)
            if (!this.colKey(colIndex).equals(that.colKey(colIndex)))
                return false;

        // Must have identical elements...
        for (int rowIndex = 0; rowIndex < nrow(); ++rowIndex)
            for (int colIndex = 0; colIndex < ncol(); ++colIndex)
                if (DoubleComparator.DEFAULT.NE(this.get(rowIndex, colIndex),
                                                that.get(rowIndex, colIndex)))
                    return false;

        return true;
    }

    @Override public int hashCode() {
        throw new UnsupportedOperationException("Data matrices should not be used as keys.");
    }

    @Override public String toString() {
        StringBuilder builder = new StringBuilder();

        for (int rowIndex = 0; rowIndex < nrow(); ++rowIndex)
            for (int colIndex = 0; colIndex < ncol(); ++colIndex)
                builder.append(formatElement(rowIndex, colIndex));

        return builder.toString();
    }

    private String formatElement(int rowIndex, int colIndex) {
        return String.format("(%s, %s) => %s\n",
                             rowKey(rowIndex),
                             colKey(colIndex),
                             Double.toString(get(rowIndex, colIndex)));
    }
}
