
package jam.math;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class ContiguousIntSequenceIterator implements Iterator<Integer> {
    private int index;
    private final ContiguousIntSequence sequence;

    ContiguousIntSequenceIterator(ContiguousIntSequence sequence) {
        this.index    = sequence.begin();
        this.sequence = sequence;
    }

    @Override public boolean hasNext() {
        return index < sequence.end();
    }

    @Override public Integer next() {
        if (!hasNext())
            throw new NoSuchElementException();

        return Integer.valueOf(index++);
    }

    @Override public void remove() {
        throw new UnsupportedOperationException();
    }
}
