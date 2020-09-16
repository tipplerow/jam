
package jam.vector;

final class ArrayWrapper extends AbstractVector {
    private final double[] array;

    ArrayWrapper(double[] array) {
        this.array = array;
    }

    @Override public int length() {
        return array.length;
    }

    @Override public double getDouble(int index) {
        return array[index];
    }
}
