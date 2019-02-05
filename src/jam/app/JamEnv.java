
package jam.app;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jam.lang.JamException;

/**
 * Provides utility methods for mananging environment variables.
 */
public final class JamEnv {
    private JamEnv() {}

    private static final String OPEN_DELIM = "${";
    private static final String CLOSE_DELIM = "}";

    private static final Pattern SYSTEM_ENV_PATTERN =
        Pattern.compile(Pattern.quote(OPEN_DELIM) + "(\\w+)" + Pattern.quote(CLOSE_DELIM));

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

    /**
     * Replaces environment variables delimited by <tt>${...}</tt>
     * with their values (and removes the delimiters).
     *
     * @param s the string to examine.
     *
     * @return a string with all environment variables named in the
     * input string replaced with their environment values (and the
     * delimiters removed).
     *
     * @throws RuntimeException if a referenced environment variable
     * is not set.
     */
    public static String replaceVariable(String s) {
        Matcher matcher = SYSTEM_ENV_PATTERN.matcher(s);

        while (matcher.find()) {
            String envName = matcher.group(1);
            String envValue = getRequired(envName);

            s = s.replace(envName, envValue);
            s = s.replace(OPEN_DELIM, "");
            s = s.replace(CLOSE_DELIM, "");
        }

        return s;
    }
}
