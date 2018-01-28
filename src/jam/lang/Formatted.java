
package jam.lang;

/**
 * Denotes objects that have a well-defined string representation.
 *
 * <p>Classes that implement this interface typically need to be
 * created by parsing string representations found in text files,
 * and the {@link Formatted#format()} method provides a contract 
 * for the structural form of that representation.
 */
public interface Formatted {
    /**
     * Generates the standard string representation of this object.
     *
     * <p>It should be possible to create an identical object by
     * parsing the string returned by this method.
     *
     * @return the standard string representation of this object.
     */
    public abstract String format();

    /**
     * Returns a string representation of this object that should be
     * informative for debugging or other log messages.
     *
     * @return the simple class name of this object followed by the
     * format string enclosed in parentheses.
     */
    public default String debug() {
        return getClass().getSimpleName() + "(" + format() + ")";
    }
}
