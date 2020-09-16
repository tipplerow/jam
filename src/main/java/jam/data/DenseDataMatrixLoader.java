
package jam.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jam.app.JamLogger;
import jam.io.TableReader;
import jam.lang.JamException;
import jam.matrix.JamMatrix;
import jam.vector.JamVector;

/**
 * Loads a data matrix from an input file in <em>dense matrix</em>
 * format.
 *
 * @param R the runtime type of the row keys.
 *
 * @param C the runtime type of the column keys.
 */
public abstract class DenseDataMatrixLoader<R, C> extends DataMatrixLoader<R, C> {
    private TableReader reader;
    private List<R> rowKeys;
    private List<C> colKeys;
    private List<JamVector> rowData;

    /**
     * Creates a new data matrix reader for a given file.
     *
     * @param file the file to load.
     */
    protected DenseDataMatrixLoader(File file) {
        super(file);
    }

    /**
     * Creates a new data matrix reader for a given file.
     *
     * @param fileName the name of the file to load.
     */
    protected DenseDataMatrixLoader(String fileName) {
        super(fileName);
    }

    /**
     * Returns a new dense data matrix given the keys and values read
     * from the input file.
     *
     * <p>This default implementation returns an instance of the base
     * class {@code DenseDataMatrix}, but subclass loaders may return
     * a specialized subclass.
     *
     * @param rowKeys the row keys.
     *
     * @param colKeys the column keys.
     *
     * @param elements the element values.
     *
     * @return a new dense data matrix given the keys and values read
     * from the input file.
     */
    public DenseDataMatrix<R, C> newMatrix(List<R> rowKeys, List<C> colKeys, JamMatrix elements) {
        return new DenseDataMatrix<R, C>(rowKeys, colKeys, elements, false, false);
    }

    @Override public DenseDataMatrix<R, C> load() {
        reader = TableReader.open(file);

        try {
            parseColKeys();
            parseRowData();

            JamLogger.info("DenseDataMatrixLoader: Loaded [%d] rows.", rowKeys.size());
            JamLogger.info("DenseDataMatrixLoader: Loaded [%d] columns.", colKeys.size());

            return newMatrix(rowKeys, colKeys, JamMatrix.rbind(rowData));
        }
        finally {
            reader.close();
        }
    }

    private void parseColKeys() {
        //
        // The first key in the header line describes the rows and is
        // ignored...
        //
        if (reader.ncol() < 2)
            throw JamException.runtime("Input file must contain at least two columns.");

        colKeys = new ArrayList<C>(reader.ncol() - 1);

        for (int readerIndex = 1; readerIndex < reader.ncol(); ++readerIndex)
            colKeys.add(parseColKey(reader.columnKeys().get(readerIndex)));
    }

    private void parseRowData() {
        rowKeys = new ArrayList<R>();
        rowData = new ArrayList<JamVector>();

        for (List<String> line : reader)
            parseRowData(line);
    }

    private void parseRowData(List<String> line) {
        //
        // The first column is the row key, the remaining columns are
        // the row elements...
        //
        rowKeys.add(parseRowKey(line.get(0)));
        rowData.add(JamVector.parse(line.subList(1, line.size())));
    }
}
