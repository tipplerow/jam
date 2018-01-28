
package jam.lang;

/**
 * Encapsulates exception management for the JAM library.
 */
public final class JamException {
    /**
     * Wraps another exception in a new runtime exception.
     *
     * @param ex the exeception to wrap.
     *
     * @return the new exception.
     */
    public static RuntimeException runtime(Exception ex) {
        return new RuntimeException(ex);
    }

    /**
     * Creates a new runtime exception with a formatted message.
     *
     * @param format message format string.
     *
     * @param args message format arguments.
     *
     * @return the new exception.
     */
    public static RuntimeException runtime(String format, Object... args) {
        return new RuntimeException(String.format(format, args));
    }
}
