
package jam.bravais;

import java.util.ArrayList;
import java.util.List;

final class Period1D extends AbstractPeriod {
    private final int nx;

    Period1D(int nx) {
        validateDimension(nx);

        this.nx = nx;
    }

    @Override public int dimensionality() {
        return 1;
    }

    @Override public UnitIndex imageOf(UnitIndex index) {
        validateDimensionality(index);

        return UnitIndex.at(Period.imageOf(index.coord(0), nx));
    }

    @Override public int period(int dim) {
        if (dim == 0)
            return nx;
        else
            throw new IllegalArgumentException("Invalid period dimension.");
    }

    @Override public List<UnitIndex> enumerate() {
        List<UnitIndex> images = new ArrayList<UnitIndex>(nx);

        for (int i = 0; i < nx; ++i)
            images.add(UnitIndex.at(i));

        assert images.size() == countSites();
        return images;
    }

    @Override public String toString() {
        return String.format("Period(%d)", nx);
    }
}
