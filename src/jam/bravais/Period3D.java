
package jam.bravais;

import java.util.ArrayList;
import java.util.List;

final class Period3D extends AbstractPeriod {
    private final int nx;
    private final int ny;
    private final int nz;

    Period3D(int nx, int ny, int nz) {
        validateDimension(nx);
        validateDimension(ny);
        validateDimension(nz);

        this.nx = nx;
        this.ny = ny;
        this.nz = nz;
    }

    @Override public int dimensionality() {
        return 3;
    }

    @Override public UnitIndex imageOf(UnitIndex index) {
        validateDimensionality(index);

        return UnitIndex.at(Period.imageOf(index.coord(0), nx),
                            Period.imageOf(index.coord(1), ny),
                            Period.imageOf(index.coord(2), nz));
    }

    @Override public int period(int dim) {
        switch (dim) {
        case 0:
            return nx;

        case 1:
            return ny;

        case 2:
            return nz;

        default:
            throw new IllegalArgumentException("Invalid period dimension.");
        }
    }

    @Override public List<UnitIndex> enumerate() {
        List<UnitIndex> images = new ArrayList<UnitIndex>(nx * ny * nz);

        for (int k = 0; k < nz; ++k)
            for (int j = 0; j < ny; ++j)
                for (int i = 0; i < nx; ++i)
                    images.add(UnitIndex.at(i, j, k));

        assert images.size() == countSites();
        return images;
    }

    @Override public String toString() {
        return String.format("Period(%d, %d, %d)", nx, ny, nz);
    }
}
