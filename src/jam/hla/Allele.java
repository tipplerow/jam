
package jam.hla;

/**
 * Represents a single HLA allele.
 */
public final class Allele implements Comparable<Allele> {
    private final Locus locus;
    private final int   superType;
    private final int   subType;

    private final String longKey;
    private final String shortKey;
    private final int    hashCode;

    private Allele(Locus locus, int superType, int subType) {
        validateType(superType);
        validateType(subType);

        this.locus     = locus;
        this.superType = superType;
        this.subType   = subType;

        this.longKey  = formatLongKey(locus, superType, subType);
        this.shortKey = formatShortKey(locus, superType, subType);
        this.hashCode = computeHashCode(locus, superType, subType);
    }

    private static void validateType(int type) {
        if (type < 1 || type > 99)
            throw new IllegalArgumentException("Invalid allele type.");
    }

    private static String formatLongKey(Locus locus, int superType, int subType) {
        return String.format("%s%s*%02d:%02d", PREFIX, locus, superType, subType);
    }

    private static String formatShortKey(Locus locus, int superType, int subType) {
        return String.format("%s%02d%02d", locus, superType, subType);
    }

    private static int computeHashCode(Locus locus, int superType, int subType) {
        return 10000 * locus.ordinal() + 100 * superType + subType;
    }

    /**
     * Standard prefix for HLA allele names.
     */
    public static final String PREFIX = "HLA-";

    /**
     * Returns the allele with a specified locus, supertype, and
     * subtype.
     *
     * @param locus the desired locus.
     *
     * @param superType the desired supertype.
     *
     * @param subType the desired subtype.
     *
     * @return the allele with the specified locus, supertype, and
     * subtype.
     *
     * @throws RuntimeException unless the supertype and subtype are
     * valid.
     */
    public static Allele instance(Locus locus, int superType, int subType) {
        //
        // Think about a private flyweight cache...
        //
        return new Allele(locus, superType, subType);
    }

    /**
     * Returns the allele encoded in a string.
     *
     * @param s a string that encodes a unique allele.
     *
     * @return the allele encoded by the given string.
     *
     * @throws RuntimeException unless the input string encodes a
     * valid allele.
     */
    public static Allele instance(String s) {
        return Parser.parse(s);
    }

    /**
     * Returns the locus of this allele.
     *
     * @return the locus of this allele.
     */
    public Locus getLocus() {
        return locus;
    }

    /**
     * Returns the supertype of this allele.
     *
     * @return the supertype of this allele.
     */
    public int getSuperType() {
        return superType;
    }

    /**
     * Returns the this allele.
     *
     * @return the this allele.
     */
    public int getSubType() {
        return subType;
    }

    /**
     * Returns a long key for this allele (of the form {@code HLA-A*02:01}).
     *
     * @return a long key for this allele.
     */
    public String longKey() {
        return longKey;
    }

    /**
     * Returns a short key for this allele (of the form {@code A0201}).
     *
     * @return a short key for this allele.
     */
    public String shortKey() {
        return shortKey;
    }

    @Override public int compareTo(Allele that) {
        return Integer.compare(this.hashCode, that.hashCode);
    }

    @Override public boolean equals(Object obj) {
        return (obj instanceof Allele) && equalsAllele((Allele) obj);
    }

    private boolean equalsAllele(Allele that) {
        return this.locus.equals(that.locus)
            && this.superType == that.superType
            && this.subType   == that.subType;
    }

    @Override public int hashCode() {
        return hashCode;
    }

    @Override public String toString() {
        return longKey;
    }
}
