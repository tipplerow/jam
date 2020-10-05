
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

    /**
     * Identifies objects that implement an interface.
     *
     * @param object an object to examine.
     *
     * @param interfaceClass the interface class.
     *
     * @return {@code true} iff the specified object implements the
     * specified interface.
     */
    public static boolean implements_(Object object, Class<?> interfaceClass) {
        return object != null && interfaceClass.isInterface() && interfaceClass.isAssignableFrom(object.getClass());
    }
}
