
package jam.lang;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class SequenceIterator<E> implements Iterator<E> {
    private int index;
    private final Sequence<E> sequence;

    SequenceIterator(Sequence<E> sequence) {
        this.index = 0;
        this.sequence = sequence;
    }

    @Override public boolean hasNext() {
        return index < sequence.length();
    }

    @Override public E next() {
        if (hasNext())
            return sequence.objectAt(index++);
        else
            throw new NoSuchElementException();
    }

    @Override public void remove() {
        throw new UnsupportedOperationException();
    }
}
