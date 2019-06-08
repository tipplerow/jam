
package jam.data;

import java.io.File;

/**
 * Reads a data matrix from an input file.
 *
 * @param ROWTYPE the runtime type of the row keys.
 *
 * @param COLTYPE the runtime type of the column keys.
 */
public abstract class DataMatrixLoader<ROWTYPE, COLTYPE> {
    /**
     * The file to load.
     */
    protected final File file;

    /**
     * Creates a new data matrix reader for a given file.
     *
     * @param file the file to load.
     */
    protected DataMatrixLoader(File file) {
        this.file = file;
    }

    /**
     * Creates a new data matrix reader for a given file.
     *
     * @param fileName the name of the file to load.
     */
    protected DataMatrixLoader(String fileName) {
        this(new File(fileName));
    }

    /**
     * Loads the data matrix encoded the input file.
     *
     * @return the data matrix encoded in the input file.
     *
     * @throws RuntimeException unless the file can be opened and
     * contains a properly formatted data matrix.
     */
    public abstract DataMatrix<ROWTYPE, COLTYPE> load();

    /**
     * Converts a string representation into a column key.
     *
     * @param colKeyStr the string representation of a column key.
     *
     * @return the column key represented by the specified string.
     *
     * @throws RuntimeException unless the input string is a valid
     * representation of a column key.
     */
    public abstract COLTYPE parseColKey(String colKeyStr);

    /**
     * Converts a string representation into a row key.
     *
     * @param rowKeyStr the string representation of a row key.
     *
     * @return the row key represented by the specified string.
     *
     * @throws RuntimeException unless the input string is a valid
     * representation of a row key.
     */
    public abstract ROWTYPE parseRowKey(String rowKeyStr);
}
