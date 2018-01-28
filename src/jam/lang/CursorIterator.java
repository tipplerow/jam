
package jam.lang;

import java.util.Iterator;
import java.util.NoSuchElementException;

// Allows iteration over sequence elements from left to right.
//
final class CursorIterator implements Iterator<Cursor> {
    private int index;
    private final int length;

    CursorIterator(int length) {
        this.index = 0; // Current index...
        this.length = length;
    }

    @Override public boolean hasNext() {
        return index < length;
    }

    @Override public Cursor next() {
        if (hasNext())
            return new Cursor(index++);
        else
            throw new NoSuchElementException();
    }
}
