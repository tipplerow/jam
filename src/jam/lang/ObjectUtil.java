
package jam.lang;

/**
 * Provides utility operations for generic objects.
 */
public final class ObjectUtil {
    /**
     * Compares objects with proper handling of {@code null} values.
     *
     * <p>Two {@code null} values are considered equal; one {@code null}
     * value is never considered equal to a non-{@code null}. This method
     * will never throw a {@code NullPointerException}.
     *
     * @param o1 the first object.
     *
     * @param o2 the second object.
     *
     * @return {@code true} iff {@code (o1 != null && o1.equals(o2)) || (o1 == null && o2 == null)}.
     */
    public static boolean equals(Object o1, Object o2) {
        if (o1 == null)
            return o2 == null;
        else
            return o1.equals(o2);
    }

    /**
     * Identifies classes with publicly accessible default
     * constructors.
     *
     * @param theClass the class to examine.
     *
     * @return {@code true} iff the specified class has a publicly
     * accessible default constructor.
     */
    public static boolean hasDefaultConstructor(Class<?> theClass) {
        boolean result = true;

        try {
            theClass.getConstructor();
        }
        catch (Exception ex) {
            result = false;
        }

        return result;
    }

    /**
     * Creates a new object with the same runtime type as another.
     *
     * @param <T> the runtime type of the object.
     *
     * @param object the object with the desired runtime time; its
     * class must define a public default constructor.
     *
     * @return a new object with the same runtime type as the input
     * object.
     *
     * @throws RuntimeException unless the runtime type of the input
     * object has a public default constructor.
     */
    @SuppressWarnings("unchecked")
    public static <T> T like(T object) {
        return newInstance((Class<T>) object.getClass());
    }

    /**
     * Creates a new instance of a class with a default constructor.
     *
     * @param <T> the runtime type of the object.
     *
     * @param theClass the class of the object to create; the class
     * must define a public default constructor.
     *
     * @return a new instance of the specified class created with the
     * default constructor.
     *
     * @throws RuntimeException unless the specified class defines a
     * public default constructor.
     */
    public static <T> T newInstance(Class<T> theClass) {
        try {
            return (T) theClass.getConstructor().newInstance();
        }
        catch (Exception ex) {
            throw JamException.runtime(ex);
        }
    }
}
