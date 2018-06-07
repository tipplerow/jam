
package jam.lang;

/**
 * Provides utility operations for {@code Class} objects.
 */
public final class ClassUtil {
    /**
     * Identifies subclass relationships.
     *
     * @param subClass a potential subclass.
     *
     * @param superClass a potential superclass.
     *
     * @return {@code true} iff {@code subClass} is the same class or
     * subclass of {@code superClass}.
     */
    public static boolean isa(Class<?> subClass, Class<?> superClass) {
        return superClass.isAssignableFrom(subClass);
    }
}
