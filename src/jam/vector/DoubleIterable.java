
package jam.vector;

import java.util.Iterator;

final class DoubleIterable implements Iterable<Double> {
    private final VectorView vector;

    DoubleIterable(VectorView vector) {
        this.vector = vector;
    }

    @Override public Iterator<Double> iterator() {
        return new VectorIterator(vector);
    }
}
