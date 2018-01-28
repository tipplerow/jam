
package jam.lang;

import java.util.Iterator;

// Allows iteration over sequence elements from left to right.
//
final class CursorIterable implements Iterable<Cursor> {
    private final int length;

    CursorIterable(int length) {
        this.length = length;
    }

    @Override public Iterator<Cursor> iterator() {
        return new CursorIterator(length);
    }
}
