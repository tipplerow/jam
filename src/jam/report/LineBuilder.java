
package jam.report;

/**
 * Builds delimited report lines field by field.
 */
public final class LineBuilder {
    private final String delim;
    private final StringBuilder builder;

    // Number of fields already added to the line...
    private int fieldCount = 0;

    /**
     * Creates a new line builder with a fixed delimiter.
     *
     * @param delim the field delimiter.
     */
    public LineBuilder(String delim) {
        this.delim = delim;
        this.builder = new StringBuilder();
    }

    /**
     * Creates a new comma-delimted line builder.
     *
     * @return a new comma-delimted line builder.
     */
    public static LineBuilder csv() {
        return new LineBuilder(",");
    }

    private void newField() {
        //
        // The delimiter is added only after the first field...
        // 
        if (fieldCount > 0)
            builder.append(delim);

        ++fieldCount;
    }

    /**
     * Appends a {@code double} value to the line with default
     * formatting.
     *
     * @param d the {@code double} value to append.
     */
    public void append(double d) {
        newField();
        builder.append(d);
    }

    /**
     * Appends a {@code double} value to the line with specific
     * formatting.
     *
     * @param d the {@code double} value to append.
     *
     * @param fmt the format string.
     */
    public void append(double d, String fmt) {
        newField();
        builder.append(String.format(fmt, d));
    }

    /**
     * Appends an {@code int} value to the line.
     *
     * @param i the {@code int} value to append.
     */
    public void append(int i) {
        newField();
        builder.append(i);
    }

    /**
     * Appends an {@code long} value to the line.
     *
     * @param lng the {@code long} value to append.
     */
    public void append(long lng) {
        newField();
        builder.append(lng);
    }

    /**
     * Appends a {@code Object} field to the line.
     *
     * @param o {@code Object} field to append.
     */
    public void append(Object o) {
        newField();
        builder.append(o);
    }

    /**
     * Appends a {@code String} field to the line.
     *
     * @param s {@code String} field to append.
     */
    public void append(String s) {
        newField();
        builder.append(s);
    }

    @Override public String toString() {
        return builder.toString();
    }
}
