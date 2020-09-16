
package jam.math;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class IntRangeIterator implements Iterator<Integer> {
    private int nextk;
    private final IntRange range;

    IntRangeIterator(IntRange range) {
        this.range = range;
        this.nextk = range.lower();
    }

    @Override public boolean hasNext() {
        return nextk <= range.upper();
    }

    @Override public Integer next() {
        if (!hasNext())
            throw new NoSuchElementException();

        Integer nextI = Integer.valueOf(nextk);
        ++nextk;

        return nextI;
    }

    @Override public void remove() {
        throw new UnsupportedOperationException();
    }
}
