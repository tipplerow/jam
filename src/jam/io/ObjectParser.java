
package jam.io;

/**
 * Defines a mechanism to translate the string representation of an
 * object into the object itself.
 */
public interface ObjectParser<T> {
    /**
     * Parses a string.
     *
     * @param s the string to parse.
     *
     * @return the object encoded in the given string.
     *
     * @throws RuntimeException if the input string is not a valid
     * representation of an object in class {@code T}.
     */
    public abstract T parse(String s);
}
