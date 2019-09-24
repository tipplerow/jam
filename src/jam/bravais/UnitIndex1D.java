
package jam.bravais;

final class UnitIndex1D implements UnitIndex {
    final int index;

    private UnitIndex1D(int index) {
        this.index = index;
    }

    static UnitIndex1D at(int index) {
        return new UnitIndex1D(index);
    }

    @Override public int coord(int dim) {
        if (dim == 0)
            return index;
        else
            throw new IndexOutOfBoundsException("Invalid index dimension.");
    }

    @Override public int dimensionality() {
        return 1;
    }

    @Override public int[] toArray() {
        return new int[] { index };
    }

    @Override public boolean equals(Object obj) {
        return (obj instanceof UnitIndex1D) && equalsUnitIndex1D((UnitIndex1D) obj);
    }

    private boolean equalsUnitIndex1D(UnitIndex1D that) {
        return this.index == that.index;
    }

    @Override public int hashCode() {
        return index;
    }

    @Override public String toString() {
        return String.format("UnitIndex(%d)", index);
    }
}
