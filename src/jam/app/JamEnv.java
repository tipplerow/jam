
package jam.app;

import jam.lang.JamException;

/**
 * Provides utility methods for mananging environment variables.
 */
public final class JamEnv {
    private JamEnv() {}

    /**
     * Returns a required environment variable.
     *
     * @param varName the name of the environment variable.
     *
     * @return the string value of the environment variable.
     *
     * @throws RuntimeException if the environment variable is not
     * set.
     */
    public static String getRequired(String varName) {
        String result = System.getenv(varName);

        if (result != null)
            return result;
        else
            throw JamException.runtime("Environment variable [%s] is not set.", varName);
    }

    /**
     * Returns an optional environment variable.
     *
     * @param varName the name of the environment variable.
     *
     * @param defaultValue the default value to assign if the system
     * property has not been set.
     *
     * @return the string value of the environment variable, if it is set,
     * or the default value otherwise.
     */
    public static String getOptional(String varName, String defaultValue) {
        String result = System.getenv(varName);

        if (result != null)
            return result;
        else
            return defaultValue;
    }

    /**
     * Identifies environment variables that have been assigned values.
     *
     * @param varName the name of the property to check.
     *
     * @return {@code true} iff there is an environment variable with
     * the given name.
     */
    public static boolean isSet(String varName) {
        return System.getenv(varName) != null;
    }

    /**
     * Identifies environment variables that have not been assigned
     * values.
     *
     * @param varName the name of the property to check.
     *
     * @return {@code true} unless there is an environment variable
     * with the given name.
     */
    public static boolean isUnset(String varName) {
        return !isSet(varName);
    }
}
