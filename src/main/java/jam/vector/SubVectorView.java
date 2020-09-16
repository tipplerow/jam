
package jam.vector;

final class SubVectorView implements VectorView {
    private final int begin;
    private final int end;
    private final VectorView parent;

    SubVectorView(VectorView parent, int begin, int end) {
        validate(parent, begin, end);

        this.parent = parent;
        this.begin  = begin;
        this.end    = end;
    }

    private static void validate(VectorView parent, int begin, int end) {
        if (begin < 0)
            throw new IllegalArgumentException("Beginning index must be non-negative.");

        if (begin >= parent.length())
            throw new IllegalArgumentException("Beginning index must be less than the parent length.");

        if (end < begin)
            throw new IllegalArgumentException("Ending index must be greater than or equal to the beginning index.");
    }

    @Override public int length() {
        return end - begin;
    }

    @Override public double getDouble(int index) {
        return parent.getDouble(index + begin);
    }
}
