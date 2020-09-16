
package jam.data;

import jam.junit.NumericTestBase;
import jam.lang.KeyedObject;

import org.junit.*;
import static org.junit.Assert.*;

final class Asset extends KeyedObject<String> {
    public Asset(String key) {
        super(key);
    }
}

final class Factor extends KeyedObject<String> {
    public Factor(String key) {
        super(key);
    }
}

final class ExposureLoader extends DenseDataMatrixLoader<Asset, Factor> {
    private ExposureLoader(String fileName) {
        super(fileName);
    }

    public static DataMatrix<Asset, Factor> load(String fileName) {
        ExposureLoader loader = new ExposureLoader(fileName);
        return loader.load();
    }

    @Override public Factor parseColKey(String factorKey) {
        return new Factor(factorKey);
    }

    @Override public Asset parseRowKey(String assetKey) {
        return new Asset(assetKey);
    }
}

public class DataMatrixLoaderTest extends NumericTestBase {
    private static Asset AMAT = new Asset("AMAT");
    private static Asset KLAC = new Asset("KLAC");
    private static Asset LRCX = new Asset("LRCX");

    private static Factor MOMENTUM = new Factor("MOMENTUM");
    private static Factor VALUE    = new Factor("VALUE");

    @Test public void testLoad() {
        DataMatrix<Asset, Factor> matrix =
            ExposureLoader.load("data/test/factor_exposure.csv");

        assertEquals(3, matrix.nrow());
        assertEquals(2, matrix.ncol());

        assertEquals(AMAT, matrix.rowKey(0));
        assertEquals(KLAC, matrix.rowKey(1));
        assertEquals(LRCX, matrix.rowKey(2));

        assertEquals(MOMENTUM, matrix.colKey(0));
        assertEquals(VALUE,    matrix.colKey(1));

        assertDouble( 1.23, matrix.get(AMAT, MOMENTUM));
        assertDouble(-0.12, matrix.get(KLAC, MOMENTUM));
        assertDouble( 2.25, matrix.get(LRCX, MOMENTUM));

        assertDouble(-3.21, matrix.get(AMAT, VALUE));
        assertDouble( 0.33, matrix.get(KLAC, VALUE));
        assertDouble( 1.88, matrix.get(LRCX, VALUE));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.data.DataMatrixLoaderTest");
    }
}
