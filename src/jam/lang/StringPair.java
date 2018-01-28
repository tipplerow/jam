
package jam.lang;

import jam.util.RegexUtil;

/**
 * Contains an immutable pair of strings, e.g., to serve as a
 * two-dimensional key.  The order of the strings is material
 * for equality tests and for ordering comparisons.
 */
public final class StringPair implements Comparable<StringPair>, Formatted {
    private final String first;
    private final String second;

    /**
     * Creates a new string pair.
     *
     * @param first the first string of the pair.
     *
     * @param second the second string of the pair.
     */
    public StringPair(String first, String second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Parses a string representation of a string pair.
     *
     * @param s a string containing the first and second constituent
     * separated by a comma.
     *
     * @return a new string pair containing the constituents from the
     * input string, with leading and trailing white space removed.
     *
     * @throws IllegalArgumentException unless the input string
     * contains exactly one comma.
     */
    public static StringPair parse(String s) {
        String[] fields = RegexUtil.COMMA.split(s);

        if (fields.length != 2)
            throw new IllegalArgumentException(String.format("Invalid string pair: [%s].", s));

        return new StringPair(fields[0].trim(), fields[1].trim());
    }

    /**
     * Returns a string pair given its constituents.
     *
     * @param first the first string of the pair.
     *
     * @param second the second string of the pair.
     *
     * @return a string pair with the specified constituents.
     */
    public static StringPair valueOf(String first, String second) {
        //
        // One could maintain a registry, but likely not worth it...
        //
        return new StringPair(first, second);
    }

    /**
     * Returns the first string of this pair.
     *
     * @return the first string of this pair.
     */
    public String first() {
        return first;
    }

    /**
     * Returns the second string of this pair.
     *
     * @return the second string of this pair.
     */
    public String second() {
        return second;
    }

    @Override public int compareTo(StringPair that) {
        int firstComp = this.first.compareTo(that.first);

        if (firstComp != 0)
            return firstComp;
        else
            return this.second.compareTo(that.second);
    }

    @Override public String format() {
        return first + ", " + second;
    }

    @Override public boolean equals(Object that) {
        return (that instanceof StringPair) && equalsStringPair((StringPair) that);
    }

    private boolean equalsStringPair(StringPair that) {
        return this.first.equals(that.first) && this.second.equals(that.second);
    }

    @Override public int hashCode() {
        return first.hashCode() + 37 * second.hashCode();
    }

    @Override public String toString() {
        return "StringPair(" + format() + ")";
    }
}
