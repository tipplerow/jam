
package jam.app;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
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
     * Returns a read-only view of a subset of environment variables,
     * with the keys arranged in alphabetical order.
     *
     * @param prefixes environment variables will be omitted unless
     * their name begins with one of the prefixes.
     *
     * @return a read-only view of a subset of environment variables,
     * with the keys arranged in alphabetical order.
     */
    public static Map<String, String> filter(String... prefixes) {
        Map<String, String> env = new TreeMap<String, String>(System.getenv());
        Iterator<String> nameIterator = env.keySet().iterator();

        while (nameIterator.hasNext()) {
            String name = nameIterator.next();

            if (!startsWith(name, prefixes))
                nameIterator.remove();
        }

        return Collections.unmodifiableMap(env);
    }

    private static boolean startsWith(String name, String... prefixes) {
        for (String prefix : prefixes)
            if (name.startsWith(prefix))
                return true;

        return false;
    }

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
     * Replaces environment variables delimited by <code>${...}</code>
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
