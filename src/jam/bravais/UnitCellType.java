
package jam.bravais;

public enum UnitCellType {
    /**
     * The two-dimensional hexagonal unit cell.
     */
    HEXAGONAL(HexagonalUnitCell.FUNDAMENTAL),

    /**
     * The two-dimensional square unit cell.
     */
    SQUARE(SquareUnitCell.FUNDAMENTAL);

    private UnitCell fundamental;

    private UnitCellType(UnitCell fundamental) {
        this.fundamental = fundamental;
    }

    /**
     * Returns the fundamental unit cell for this type (typically with
     * unit side length).
     *
     * @return the fundamental unit cell for this type (typically with
     * unit side length).
     */
    public UnitCell fundamental() {
        return fundamental;
    }
}
