
package jam.report;

/**
 * Defines the records handled by the {@code ReportWriter} class.
 */
public interface ReportRecord {
    /**
     * Returns the base name of the report file.
     *
     * @return the base name of the report file.
     */
    public abstract String getBaseName();

    /**
     * Returns the header line for the report file.
     *
     * @return the header line for the report file.
     */
    public abstract String getHeaderLine();

    /**
     * Formats this record for writing to the report file.
     *
     * @return a string representation of this record.
     */
    public abstract String formatLine();
}
