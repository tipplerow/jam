
package jam.math;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class LongRangeIterator implements Iterator<Long> {
    private long nextk;
    private final LongRange range;

    LongRangeIterator(LongRange range) {
        this.range = range;
        this.nextk = range.lower();
    }

    @Override public boolean hasNext() {
        return nextk <= range.upper();
    }

    @Override public Long next() {
        if (!hasNext())
            throw new NoSuchElementException();

        Long nextI = Long.valueOf(nextk);
        ++nextk;

        return nextI;
    }

    @Override public void remove() {
        throw new UnsupportedOperationException();
    }
}
