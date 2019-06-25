
package jam.rna;

/**
 * Enumerates types of RNA expression profiles.
 */
public enum ExpressionProfileType {
    /**
     * One aggregate expression profile applies to an entire cohort
     * (typically the median expression within another proxy cohort).
     */
    AGGREGATE,

    /**
     * Expression profiles are uniform within a cancer type (typically
     * computed as the median expression of a cancer-specific cohort).
     */
    CANCER_TYPE,

    /**
     * Each tumor has a unique expression profile.
     */
    INDIVIDUAL;
}
