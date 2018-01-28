
package jam.lattice;

import java.lang.reflect.Array;

/**
 * Provides a single-occupancy lattice implementation backed by a
 * dense 3D array of occupants.
 */
public final class DenseSOLattice<T> extends SOLattice<T> {
    private final T[][][] sites;

    /**
     * Creates an empty dense single-occupancy lattice.
     *
     * @param period the period of the lattice.
     */
    public DenseSOLattice(Period period) {
	this.sites = createSites(period);
    }

    @SuppressWarnings("unchecked")
    private static <T> T[][][] createSites(Period period) {
	int nx = period.getPeriodX();
	int ny = period.getPeriodY();
	int nz = period.getPeriodZ();

	return (T[][][]) Array.newInstance(Object.class, nx, ny, nz);
    }

    @Override public T occupantAt(Image image) {
        return sites[image.x][image.y][image.z];
    }

    @Override public void assignOccupant(T occupant, Image image) {
        sites[image.x][image.y][image.z] = occupant;
    }

    @Override public void removeOccupant(Image image) {
        assignOccupant(null, image);
    }
}
