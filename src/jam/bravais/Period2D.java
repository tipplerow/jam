
package jam.bravais;

import java.util.ArrayList;
import java.util.List;

final class Period2D extends AbstractPeriod {
    private final int nx;
    private final int ny;

    Period2D(int nx, int ny) {
        validateDimension(nx);
        validateDimension(ny);

        this.nx = nx;
        this.ny = ny;
    }

    @Override public int dimensionality() {
        return 2;
    }

    @Override public UnitIndex imageOf(UnitIndex index) {
        validateDimensionality(index);

        return UnitIndex.at(Period.imageOf(index.coord(0), nx),
                            Period.imageOf(index.coord(1), ny));
    }

    @Override public int period(int dim) {
        switch (dim) {
        case 0:
            return nx;

        case 1:
            return ny;

        default:
            throw new IllegalArgumentException("Invalid period dimension.");
        }
    }

    @Override public List<UnitIndex> enumerate() {
        List<UnitIndex> images = new ArrayList<UnitIndex>(nx * ny);

        for (int j = 0; j < ny; ++j)
            for (int i = 0; i < nx; ++i)
                images.add(UnitIndex.at(i, j));

        assert images.size() == countSites();
        return images;
    }

    @Override public String toString() {
        return String.format("Period(%d, %d)", nx, ny);
    }
}
