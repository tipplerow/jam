
package jam.lang;

/**
 * Encapsulates utility methods operating on {@code boolean} values.
 */
public final class JamBoolean {
    /**
     * Converts a boolean to a floating-point value.
     *
     * @param bool the boolean value to convert.
     *
     * @return {@code 0.0} for {@code false}; {@code 1.0} for {@code true}.
     */
    public static double doubleValue(boolean bool) {
	return bool ? 1.0 : 0.0;
    }

    /**
     * Converts a boolean to an integer value.
     *
     * @param bool the boolean value to convert.
     *
     * @return {@code 0} for {@code false}; {@code 1} for {@code true}.
     */
    public static int intValue(boolean bool) {
	return bool ? 1 : 0;
    }

    /**
     * Parses a character representation of a boolean value.
     *
     * <p>The characters {@code 'T'} and {@code '1'} evaluate to
     * {@code true}, while {@code 'F'} and {@code '0'} evaluate to
     * {@code false}; all other characters result in an exception.
     *
     * @param c the character to parse.
     *
     * @return the boolean value represented by the given character.
     *
     * @throws RuntimeException unless the character is a valid
     * boolean representation as described above.
     */
    public static boolean valueOf(char c) {
	switch (c) {
	case 'T':
	    return true;

	case '1':
	    return true;

	case 'F':
	    return false;

	case '0':
	    return false;
	}

	throw JamException.runtime("Undefined boolean character [%c]", c);
    }
}
