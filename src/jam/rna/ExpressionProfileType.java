
package jam.rna;

/**
 * Enumerates types of RNA expression profiles.
 */
public enum ExpressionProfileType {
    /**
     * One expression profile applies to an entire cohort.
     */
    AGGREGATE,

    /**
     * Each member of a cohort has a unique expression profile.
     */
    INDIVIDUAL;
}
