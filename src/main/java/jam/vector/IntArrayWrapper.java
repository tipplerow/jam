
package jam.vector;

final class IntArrayWrapper extends AbstractVector {
    private final int[] array;

    IntArrayWrapper(int[] array) {
        this.array = array;
    }

    @Override public int length() {
        return array.length;
    }

    @Override public double getDouble(int index) {
        return array[index];
    }
}
