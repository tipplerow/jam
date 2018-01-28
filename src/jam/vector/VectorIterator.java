
package jam.vector;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class VectorIterator implements Iterator<Double> {
    private int index;
    private final VectorView vector;

    VectorIterator(VectorView vector) {
        this.index  = 0;
        this.vector = vector;
    }

    @Override public boolean hasNext() {
        return index < vector.length();
    }

    @Override public Double next() {
        if (!hasNext())
            throw new NoSuchElementException();

        return vector.getDouble(index++);
    }

    @Override public void remove() {
        throw new UnsupportedOperationException();
    }
}
