
package jam.spin;

import jam.lang.JamException;
import jam.math.JamRandom;

/**
 * Defines the two spin states: up and down.
 */
public enum Spin {
    DOWN {
        @Override public boolean booleanValue() {
            return false;
        }
            
        @Override public char charValue() {
            return '-';
        }
            
        @Override public int intValue() {
            return -1;
        }
            
	@Override public Spin flip() {
	    return UP;
	}
    },

    UP {
        @Override public boolean booleanValue() {
            return true;
        }
            
        @Override public char charValue() {
            return '+';
        }
            
        @Override public int intValue() {
            return +1;
        }
            
	@Override public Spin flip() {
	    return DOWN;
	}
    }; 

    /**
     * Returns the boolean representation of this spin.
     *
     * @return the boolean representation of this spin.
     */
    public abstract boolean booleanValue();

    /**
     * Returns the character representation of this spin.
     *
     * @return the character representation of this spin.
     */
    public abstract char charValue();

    /**
     * Returns the integer value of this spin.
     *
     * @return the integer value of this spin.
     */
    public abstract int intValue();

    /**
     * Returns the floating-point value of this spin.
     *
     * @return the floating-point value of this spin.
     */
    public double doubleValue() {
	return intValue(); // Automatic promotion from int to double...
    }

    /**
     * Returns the flipped value of this spin; this spin is unchanged.
     *
     * @return the flipped value of this spin; this spin is unchanged.
     */
    public abstract Spin flip();

    /**
     * Returns the next spin in a pseudo-random sequence.
     *
     * @param source a random deviate source.
     *
     * @return the next spin in the pseudo-random sequence governed by
     * the specified random source.
     */
    public static Spin next(JamRandom source) {
        return valueOf(source.nextBoolean());
    }

    /**
     * Returns a randomly generated spin.
     *
     * <p>The {@code JamRandom.global()} instance is used as the
     * random number source.
     *
     * @return a randomly generated spin.
     */
    public static Spin random() {
        return next(JamRandom.global());
    }

    /**
     * Converts a boolean value into a spin.
     *
     * @param bool the boolean value to convert.
     *
     * @return {@code UP} for {@code true} values; {@code DOWN} for
     * {@code false}.
     */
    public static Spin valueOf(boolean bool) {
        return bool ? UP : DOWN;
    }

    /**
     * Parses the character representation of a spin.
     *
     * @param c the character representation to parse.
     *
     * @return {@code UP} for {@code '+'} characters and
     * {@code DOWN} for {@code '-'} characters; all other
     * characters will generate a runtime exception.
     *
     * @throws RuntimeException unless the input character is a valid
     * spin representation.
     */
    public static Spin valueOf(char c) {
	switch (c) {
	case '+':
	    return UP;

	case '-':
	    return DOWN;
	}

	throw JamException.runtime("Invalid spin character [%c].", c);
    }

    /**
     * Converts an integer value into a spin.
     *
     * @param intValue the integer value to convert.
     *
     * @return {@code UP} for {@code 1}, {@code DOWN} for {@code -1};
     * all other characters will generate a runtime exception.
     *
     * @throws RuntimeException unless the input integer is a valid
     * spin representation.
     */
    public static Spin valueOf(int intValue) {
	switch (intValue) {
	case 1:
	    return UP;

	case -1:
	    return DOWN;
	}

	throw JamException.runtime("Invalid spin value [%d].", intValue);
    }
}
