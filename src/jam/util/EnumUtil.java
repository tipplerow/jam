
package jam.util;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jam.lang.JamException;

/**
 * Provides utility methods operating on enums.
 */
public final class EnumUtil {
    /**
     * Counts the number of values defined for an enum class.
     *
     * @param <E> the enum type.
     *
     * @param type the runtime type of the enum class.
     *
     * @return the number of values defined for the specified enum
     * class.
     */
    public static <E extends Enum<E>> int count(Class<E> type) {
        try {
            Method method = type.getMethod("values");
            return ((Object[]) method.invoke(null)).length;
        }
        catch (Exception ex) {
            throw JamException.runtime(ex);
        }
    }

    /**
     * Lists all values defined for an enum class.
     *
     * @param <E> the enum type.
     *
     * @param type the runtime type of the enum class.
     *
     * @return a list containing all values defined for the specified
     * enum class.
     */
    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> List<E> list(Class<E> type) {
        try {
            return List.of((E[]) type.getMethod("values").invoke(null));
        }
        catch (Exception ex) {
            throw JamException.runtime(ex);
        }
    }

    /**
     * Returns a set containing the names of all values defined for an
     * enum class.
     *
     * @param <E> the enum type.
     *
     * @param type the runtime type of the enum class.
     *
     * @return a set containing the names of all values defined for
     * the specified enum class.
     */
    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> Set<String> names(Class<E> type) {
        Set<String> result = new LinkedHashSet<String>();

        try {
            Method   method = type.getMethod("values");
            Object[] values = (Object[]) method.invoke(null);

            for (Object value : values)
                result.add(((E) value).name());
        }
        catch (Exception ex) {
            throw JamException.runtime(ex);
        }

        return result;
    }

    /**
     * Converts a string to an enum value.
     *
     * @param <E> the enum type.
     *
     * @param type the runtime type of the enum class.
     *
     * @param str the enum name.
     *
     * @return the enum from the specified class with the specified name.
     *
     * @throws RuntimeException if there is no enum with a name
     * matching the input string.
     */
    @SuppressWarnings("unchecked") 
    public static <E extends Enum<E>> E valueOf(Class<E> type, String str) {
        try {
            Method method = type.getMethod("valueOf", String.class);
            return (E) method.invoke(null, str);
        }
        catch (Exception ex) {
            throw JamException.runtime(ex);
        }
    }

    /**
     * Returns an array containing all values defined for an enum
     * class.
     *
     * @param <E> the enum type.
     *
     * @param type the runtime type of the enum class.
     *
     * @return an array containing all values defined for the given
     * enum class.
     */
    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> E[] values(Class<E> type) {
        try {
            return (E[]) type.getMethod("values").invoke(null);
        }
        catch (Exception ex) {
            throw JamException.runtime(ex);
        }
    }
}
