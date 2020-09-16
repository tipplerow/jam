
package jam.bravais;

final class UnitIndex2D implements UnitIndex {
    final int i;
    final int j;

    private UnitIndex2D(int i, int j) {
        this.i = i;
        this.j = j;
    }

    static final UnitIndex2D ORIGIN = at(0, 0);

    static UnitIndex2D at(int i, int j) {
        return new UnitIndex2D(i, j);
    }

    @Override public int coord(int dim) {
        switch (dim) {
        case 0:
            return i;

        case 1:
            return j;

        default:
            throw new IndexOutOfBoundsException("Invalid index dimension.");
        }
    }

    @Override public int dimensionality() {
        return 2;
    }

    @Override public UnitIndex2D plus(UnitIndex that) {
        return plus2D((UnitIndex2D) that);
    }

    private UnitIndex2D plus2D(UnitIndex2D that) {
        return at(this.i + that.i,
                  this.j + that.j);
    }

    @Override public UnitIndex2D times(int scalar) {
        return at(scalar * i, scalar * j);
    }

    @Override public int[] toArray() {
        return new int[] { i, j };
    }

    @Override public boolean equals(Object obj) {
        return (obj instanceof UnitIndex2D) && equalsUnitIndex2D((UnitIndex2D) obj);
    }

    private boolean equalsUnitIndex2D(UnitIndex2D that) {
        return this.i == that.i
            && this.j == that.j;
    }

    @Override public int hashCode() {
        return i + (j << 15);
    }

    @Override public String toString() {
        return String.format("UnitIndex(%d, %d)", i, j);
    }
}
