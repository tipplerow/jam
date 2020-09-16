
package jam.app;

import java.io.File;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jam.io.DataReader;
import jam.io.IOUtil;
import jam.lang.JamException;
import jam.math.DoubleRange;
import jam.math.IntRange;
import jam.math.IntUtil;
import jam.math.LongRange;
import jam.math.LongUtil;
import jam.util.EnumUtil;
import jam.util.RegexUtil;

/**
 * Helps to manange system properties.
 *
 * <p>The active properties start with the system properties present
 * when the JVM is launched.  Each call to {@code loadFile()} will
 * then read additional properties from the specified properties file
 * and assign them to the system properties.
 *
 * <p>Property values with the special form <code>${VARNAME}</code> (e.g.,
 * <code>${JAM_HOME}</code>) are expanded to the value of the <em>environment
 * variable</em> with that name.  Property values enclosed in brackets
 * (e.g., {@code &lt;user.name&gt;}) are expanded to the value of the
 * <em>system property</em> with that name.
 *
 * <p>Property files must contain key-value pairs separated by an
 * equals sign: {@code KEY = VALUE}.  The value may occupy multiple
 * lines joined by the backslash continuation character {@code '\'}.
 * The file may also contain single-line and inline comments starting
 * with the hash sign ({@code '#'}).  All text from the {@code '#'}
 * character to the end of the line will be ignored.
 */
public final class JamProperties {
    private JamProperties() {}

    private static final Pattern COMMENT_PATTERN = RegexUtil.PYTHON_COMMENT;
    private static final Pattern ENUM_SET_DELIM  = RegexUtil.COMMA;
    private static final Pattern KEY_VALUE_DELIM = Pattern.compile("=");

    private static final String OPEN_DELIM = "<";
    private static final String CLOSE_DELIM = ">";

    private static final Pattern PROPERTY_PATTERN =
        Pattern.compile(Pattern.quote(OPEN_DELIM) + "([\\w+\\.]+)" +  Pattern.quote(CLOSE_DELIM));

    /**
     * Returns a read-only view of all system properties, with the
     * keys arranged in alphabetical order.
     *
     * @return a read-only view of all system properties, with the
     * keys arranged in alphabetical order.
     */
    public static Map<String, String> all() {
        Map<String, String> properties = new TreeMap<String, String>();

        for (Object key : System.getProperties().keySet()) {
            String name  = (String) key;
            String value = System.getProperty(name);

            properties.put(name, value);
        }

        return Collections.unmodifiableMap(properties);
    }

    /**
     * Returns a read-only view of a subset of system properties, with
     * the keys arranged in alphabetical order.
     *
     * @param prefixes properties will be omitted unless their name
     * begins with one of the prefixes.
     *
     * @return a read-only view of a subset of system properties, with
     * the keys arranged in alphabetical order.
     */
    public static Map<String, String> filter(String... prefixes) {
        Map<String, String> properties = new TreeMap<String, String>(all());
        Iterator<String> nameIterator = properties.keySet().iterator();

        while (nameIterator.hasNext()) {
            String name = nameIterator.next();

            if (!startsWith(name, prefixes))
                nameIterator.remove();
        }

        return Collections.unmodifiableMap(properties);
    }

    private static boolean startsWith(String name, String... prefixes) {
        for (String prefix : prefixes)
            if (name.startsWith(prefix))
                return true;

        return false;
    }

    /**
     * Returns a required system property.
     *
     * @param propertyName the name of the system property.
     *
     * @return the string value of the system property.
     *
     * @throws RuntimeException if the system property is not defined.
     */
    public static String getRequired(String propertyName) {
        String result = System.getProperty(propertyName);

        if (result == null)
            throw JamException.runtime("Missing required property [%s].", propertyName);

        return result;
    }

    /**
     * Returns a required system property converted to a
     * {@code boolean} value.
     *
     * @param propertyName the name of the system property.
     *
     * @return the {@code boolean} value of the system property.
     *
     * @throws RuntimeException unless the system property is defined
     * and is formatted as a proper floating-point value.
     */
    public static boolean getRequiredBoolean(String propertyName) {
	return Boolean.parseBoolean(getRequired(propertyName));
    }

    /**
     * Returns a required system property converted to a
     * {@code double} value.
     *
     * @param propertyName the name of the system property.
     *
     * @return the {@code double} value of the system property.
     *
     * @throws RuntimeException unless the system property is defined
     * and is formatted as a proper floating-point value.
     */
    public static double getRequiredDouble(String propertyName) {
	return Double.parseDouble(getRequired(propertyName));
    }

    /**
     * Validates and returns a required system property converted to a
     * {@code double} value.
     *
     * @param propertyName the name of the system property.
     *
     * @param validRange the valid range of property values.
     *
     * @return the {@code double} value of the system property.
     *
     * @throws RuntimeException unless the system property is defined
     * and is formatted as a proper floating-point value in the given
     * range.
     */
    public static double getRequiredDouble(String propertyName, DoubleRange validRange) {
	return validate(propertyName, validRange, getRequiredDouble(propertyName));
    }

    private static double validate(String propertyName, DoubleRange validRange, double value) {
        if (!validRange.contains(value))
            throw JamException.runtime("Property [%s = %f] is outside the valid range %s", 
                                       propertyName, value, validRange.format());

        return value;
    }

    /**
     * Returns a required system property converted to an enumerated
     * value.
     *
     * @param <E> the runtime type for the enum class.
     *
     * @param propertyName the name of the system property.
     *
     * @param enumClass the desired runtime enum class.
     *
     * @return the enumerated value with the name matching the sytem
     * property.
     *
     * @throws RuntimeException unless the system property is defined
     * and is the name of an enum in the specified class.
     */
    public static <E extends Enum<E>> E getRequiredEnum(String propertyName, Class<E> enumClass) {
        return EnumUtil.valueOf(enumClass, getRequired(propertyName));
    }

    /**
     * Returns a required system property converted to a set of
     * enumerated values; the property string should contain the
     * enumerated values separated by commas.
     *
     * @param <E> the runtime type for the enum class.
     *
     * @param propertyName the name of the system property.
     *
     * @param enumClass the desired runtime enum class.
     *
     * @return the enumerated values with names matching those in the
     * comma-delimited property string.
     *
     * @throws RuntimeException unless the system property is defined
     * and contains one or more enum values from the specified class.
     */
    public static <E extends Enum<E>> Set<E> getRequiredEnumSet(String propertyName, Class<E> enumClass) {
        Set<E> enumSet = EnumSet.noneOf(enumClass);
        String[] enumNames = RegexUtil.split(ENUM_SET_DELIM, getRequired(propertyName));

        for (String enumName : enumNames)
            enumSet.add(EnumUtil.valueOf(enumClass, enumName));

        return enumSet;
    }

    /**
     * Returns a required system property converted to an integer.
     *
     * @param propertyName the name of the system property.
     *
     * @return the integer value of the system property.
     *
     * @throws RuntimeException unless the system property is defined
     * and is formatted as a proper integer.
     */
    public static int getRequiredInt(String propertyName) {
	return IntUtil.parseInt(getRequired(propertyName));
    }

    /**
     * Validates and returns a required system property converted to
     * an integer.
     *
     * @param propertyName the name of the system property.
     *
     * @param validRange the valid range of property values.
     *
     * @return the integer value of the system property.
     *
     * @throws RuntimeException unless the system property is defined
     * and is formatted as a proper integer in the given range.
     */
    public static int getRequiredInt(String propertyName, IntRange validRange) {
	return validate(propertyName, validRange, getRequiredInt(propertyName));
    }

    private static int validate(String propertyName, IntRange validRange, int value) {
        if (!validRange.contains(value))
            throw JamException.runtime("Property [%s = %d] is outside the valid range %s", 
                                       propertyName, value, validRange.format());

        return value;
    }

    /**
     * Returns a required system property converted to a long value.
     *
     * @param propertyName the name of the system property.
     *
     * @return the long value of the system property.
     *
     * @throws RuntimeException unless the system property is defined
     * and is formatted as a proper long value.
     */
    public static long getRequiredLong(String propertyName) {
	return LongUtil.parseLong(getRequired(propertyName));
    }

    /**
     * Validates and returns a required system property converted to a
     * long value.
     *
     * @param propertyName the name of the system property.
     *
     * @param validRange the valid range of property values.
     *
     * @return the long value of the system property.
     *
     * @throws RuntimeException unless the system property is defined
     * and is formatted as a proper long value in the given range.
     */
    public static long getRequiredLong(String propertyName, LongRange validRange) {
	return validate(propertyName, validRange, getRequiredLong(propertyName));
    }

    private static long validate(String propertyName, LongRange validRange, long value) {
        if (!validRange.contains(value))
            throw JamException.runtime("Property [%s = %d] is outside the valid range %s", 
                                       propertyName, value, validRange.format());

        return value;
    }

    /**
     * Returns an optional system property.
     *
     * @param propertyName the name of the system property.
     *
     * @param defaultValue the default value to assign if the system
     * property has not been set.
     *
     * @return the string value of the system property, if it is set,
     * or the default value otherwise.
     */
    public static String getOptional(String propertyName, String defaultValue) {
        return System.getProperty(propertyName, defaultValue);
    }

    /**
     * Returns an optional system property converted to a 
     * {@code boolean} value.
     *
     * @param propertyName the name of the system property.
     *
     * @param defaultValue the default value to assign if the system
     * property has not been set.
     *
     * @return the boolean value of the system property, if it is set,
     * or the default value otherwise.
     */
    public static boolean getOptionalBoolean(String propertyName, boolean defaultValue) {
	return isSet(propertyName) ? getRequiredBoolean(propertyName) : defaultValue;
    }

    /**
     * Returns an optional system property converted to a 
     * {@code double} value.
     *
     * @param propertyName the name of the system property.
     *
     * @param defaultValue the default value to assign if the system
     * property has not been set.
     *
     * @return the floating-point value of the system property, if it
     * is set, or the default value otherwise.
     */
    public static double getOptionalDouble(String propertyName, double defaultValue) {
	return isSet(propertyName) ? getRequiredDouble(propertyName) : defaultValue;
    }

    /**
     * Validates and returns an optional system property converted to
     * a {@code double} value.
     *
     * @param propertyName the name of the system property.
     *
     * @param validRange the valid range of property values.
     *
     * @param defaultValue the default value to assign if the system
     * property has not been set.
     *
     * @return the floating-point value of the system property, if it
     * is set, or the default value otherwise.
     *
     * @throws RuntimeException unless the value lies in the given
     * range.
     */
    public static double getOptionalDouble(String propertyName, DoubleRange validRange, double defaultValue) {
	return validate(propertyName, validRange, getOptionalDouble(propertyName, defaultValue));
    }

    /**
     * Returns an optional system property converted to an enumerated
     * value.
     *
     * @param <E> the runtime type for the enum class.
     *
     * @param propertyName the name of the system property.
     *
     * @param defaultValue the desired runtime enum class.
     *
     * @return the enumerated value with the name matching the sytem
     * property, if it is set, or the default value otherwise.
     *
     * @throws RuntimeException if the system property is set but does
     * not match a member of the enumerated class.
     */
    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> E getOptionalEnum(String propertyName, E defaultValue) {
	return isSet(propertyName) ? getRequiredEnum(propertyName, (Class<E>) defaultValue.getClass()) : defaultValue;
    }

    /**
     * Returns an optional system property converted to an integer
     * value.
     *
     * @param propertyName the name of the system property.
     *
     * @param defaultValue the default value to assign if the system
     * property has not been set.
     *
     * @return the integer value of the system property, if it is set,
     * or the default value otherwise.
     */
    public static int getOptionalInt(String propertyName, int defaultValue) {
	return isSet(propertyName) ? getRequiredInt(propertyName) : defaultValue;
    }

    /**
     * Returns an optional system property converted to an integer
     * value.
     *
     * @param propertyName the name of the system property.
     *
     * @param validRange the valid range of property values.
     *
     * @param defaultValue the default value to assign if the system
     * property has not been set.
     *
     * @return the integer value of the system property, if it is set,
     * or the default value otherwise.
     *
     * @throws RuntimeException unless the value lies in the given
     * range.
     */
    public static int getOptionalInt(String propertyName, IntRange validRange, int defaultValue) {
	return validate(propertyName, validRange, getOptionalInt(propertyName, defaultValue));
    }

    /**
     * Returns an optional system property converted to a long value.
     *
     * @param propertyName the name of the system property.
     *
     * @param defaultValue the default value to assign if the system
     * property has not been set.
     *
     * @return the long value of the system property, if it is set,
     * or the default value otherwise.
     */
    public static long getOptionalLong(String propertyName, long defaultValue) {
	return isSet(propertyName) ? getRequiredLong(propertyName) : defaultValue;
    }

    /**
     * Returns an optional system property converted to a long value.
     *
     * @param propertyName the name of the system property.
     *
     * @param validRange the valid range of property values.
     *
     * @param defaultValue the default value to assign if the system
     * property has not been set.
     *
     * @return the long value of the system property, if it is set,
     * or the default value otherwise.
     *
     * @throws RuntimeException unless the value lies in the given
     * range.
     */
    public static long getOptionalLong(String propertyName, LongRange validRange, long defaultValue) {
	return validate(propertyName, validRange, getOptionalLong(propertyName, defaultValue));
    }

    /**
     * Identifies system properties that have been assigned values.
     *
     * @param propertyName the name of the property to check.
     *
     * @return {@code true} iff there is a system property with the
     * given name.
     */
    public static boolean isSet(String propertyName) {
        return System.getProperty(propertyName) != null;
    }

    /**
     * Identifies system properties that have not been assigned
     * values.
     *
     * @param propertyName the name of the property to check.
     *
     * @return {@code true} unless there is a system property with
     * the given name.
     */
    public static boolean isUnset(String propertyName) {
        return !isSet(propertyName);
    }

    /**
     * Loads a property file.
     *
     * <p>The file must be formatted as describe in the class
     * comments.
     *
     * <p>Note that properties that duplicate existing (already set)
     * system properties will be ignored unless the {@code override}
     * flag is {@code true}.
     *
     * <p>Property values of the form <code>${VARNAME}</code> will be
     * expanded to the value of the environment variable with that
     * name.  Property values enclosed in double quotation marks
     * (e.g., {@code "user.name"}) will be expanded to the value 
     * of the system property with that name.
     *
     * @param fileName the name of the file to load.
     *
     * @param override whether to override previously assigned values.
     *
     * @return the properties contained in the file.
     *
     * @throws RuntimeException unless the properties file can be
     * located and parsed successfully.
     */
    public static synchronized PropertyList loadFile(String fileName, boolean override) {
        return loadFile(new File(fileName), override);
    }

    /**
     * Loads property files.
     *
     * <p>The files must be formatted as describe in the class
     * comments.
     *
     * <p>Note that properties that duplicate existing (already set)
     * system properties will be ignored unless the {@code override}
     * flag is {@code true}.
     *
     * <p>Property values of the form <code>${VARNAME}</code> will be
     * expanded to the value of the environment variable with that
     * name.  Property values enclosed in double quotation marks
     * (e.g., {@code "user.name"}) will be expanded to the value 
     * of the system property with that name.
     *
     * @param fileNames the names of the files to load.
     *
     * @param override whether to override previously assigned values.
     *
     * @return the properties contained in the files.
     *
     * @throws RuntimeException unless at least one property file is
     * specified and all property files can be located and parsed
     * successfully.
     */
    public static synchronized PropertyList loadFiles(List<String> fileNames, boolean override) {
        if (fileNames.isEmpty())
            throw new IllegalArgumentException("At least one property file must be specified.");

        PropertyList properties = new PropertyList();

        for (String fileName : fileNames)
            properties.append(loadFile(fileName, override));

        return properties;
    }

    /**
     * Loads a property file.
     *
     * <p>The file must be formatted as describe in the class
     * comments.
     *
     * <p>Note that properties that duplicate existing (already set)
     * system properties will be ignored unless the {@code override}
     * flag is {@code true}.
     *
     * <p>Property values of the form <code>${VARNAME}</code> will be
     * expanded to the value of the environment variable with that
     * name.  Property values enclosed in double quotation marks
     * (e.g., {@code "user.name"}) will be expanded to the value 
     * of the system property with that name.
     *
     * @param propFile the file to load.
     *
     * @param override whether to override previously assigned values.
     *
     * @return the properties contained in the file.
     *
     * @throws RuntimeException unless the properties file can be
     * located and parsed successfully.
     */
    public static synchronized PropertyList loadFile(File propFile, boolean override) {
        PropertyList properties = parseFile(propFile);

        for (String propertyName : properties.names())
            setProperty(propertyName, properties.get(propertyName), override);

        return properties;
    }

    /**
     * Reads the property names and values from a property file and
     * returns the mapping <em>but does not set any of those system
     * properties</em>.
     *
     * @param file the file to parse.
     *
     * @return the property names and values contained in the file.
     */
    public static PropertyList parseFile(File file) {
        PropertyList properties = new PropertyList();
        DataReader reader = DataReader.open(file, COMMENT_PATTERN);

        try {
            for (String line : reader) {
                String[] fields = RegexUtil.split(KEY_VALUE_DELIM, line, 2);

                String propertyName = fields[0].trim();
                String propertyValue = fields[1].trim();

                properties.set(propertyName, propertyValue);
            }
        }
        finally {
            reader.close();
        }

        return properties;
    }

    /**
     * Reads the property names and values from a property file and
     * returns the mapping <em>but does not set any of those system
     * properties</em>.
     *
     * @param fileName the name of the file to parse.
     *
     * @return the property names and values contained in the file.
     */
    public static PropertyList parseFile(String fileName) {
        return parseFile(new File(fileName));
    }

    /**
     * Reads the property names and values from a sequence of property
     * files and returns the mapping <em>but does not set any of those
     * system properties</em>.
     *
     * @param fileNames the name of the file to parse.
     *
     * @return the property names and values contained in the files.
     */
    public static PropertyList parseFiles(Iterable<String> fileNames) {
        PropertyList properties = new PropertyList();

        for (String fileName : fileNames)
            properties.append(parseFile(fileName));

        return properties;
    }

    /**
     * Replaces system properties delimited by {@code &lt;...&gt;}
     * with their values (and removes the delimiters).
     *
     * @param s the string to examine.
     *
     * @return a string with any system properties named in the input
     * string replaced with their values (and the delimiters removed).
     *
     * @throws RuntimeException if a referenced system property is not
     * set.
     */
    public static String replaceProperty(String s) {
        Matcher matcher = PROPERTY_PATTERN.matcher(s);

        while (matcher.find()) {
            String refPropName = matcher.group(1);
            String refPropValue = getRequired(refPropName);

            s = s.replace(refPropName, refPropValue);
            s = s.replace(OPEN_DELIM, "");
            s = s.replace(CLOSE_DELIM, "");
        }

        return s;
    }

    /**
     * Resolves a system property or environment variable with an
     * optional default value.
     *
     * <p>If the system property with the specified name is set, this
     * method returns that property.  Otherwise, if the environment
     * variable with the specified name is set, this method returns
     * that variable.  Otherwise, if the default value is not null,
     * this method returns the default value.  Otherwise, this method
     * throws an exception.
     *
     * @param propertyName the name of the system property to resolve.
     *
     * @param envName the name of the environment variable to resolve.
     *
     * @param defaultValue the default value to assign if neither the
     * system property nor the environment variable has been set.
     *
     * @return the first non-missing value among the specified system
     * property, environment variable, and default value.
     */
    public static String resolve(String propertyName, String envName, String defaultValue) {
        if (isSet(propertyName))
            return getRequired(propertyName);

        if (JamEnv.isSet(envName))
            return JamEnv.getRequired(envName);

        if (defaultValue != null)
            return defaultValue;

        throw JamException.runtime("Missing system property [%s] and environment variable [%s].", propertyName, envName);
    }

    /**
     * Assigns a system property.
     *
     * <p>Previously assigned values will be overwritten.
     *
     * @param propertyName the name of the property to assign.
     *
     * @param propertyValue the value of the property to assign.
     */
    public static void setProperty(String propertyName, String propertyValue) {
        setProperty(propertyName, propertyValue, true);
    }

    /**
     * Assigns a system property.
     *
     * <p>Previously assigned values will be preserved unless the
     * {@code override} argument is set to {@code true}.
     *
     * @param propertyName the name of the property to assign.
     *
     * @param propertyValue the value of the property to assign.
     *
     * @param override whether to override previously assigned values.
     */
    public static void setProperty(String propertyName, String propertyValue, boolean override) {
        if (!override && isSet(propertyName)) {
            JamLogger.info("System property is already set: [%s] = [%s]", propertyName, System.getProperty(propertyName));
            return;
        }

        // Expand symbolic property names and environment variables...
        propertyValue = replaceProperty(propertyValue);
        propertyValue = JamEnv.replaceVariable(propertyValue);

        System.setProperty(propertyName, propertyValue);
    }

    private static boolean isEnvironmentVariable(String propertyValue) {
        return propertyValue.startsWith("${") && propertyValue.endsWith("}");
    }

    private static void setEnvironmentProperty(String propertyName, String propertyValue) {
        String envName = parseEnvironmentProperty(propertyValue);
        String envValue = System.getenv(envName);

        if (envValue == null)
            throw JamException.runtime("Required environment variable [%s] is not set.", envName);

        JamLogger.info("Resolved property [%s] => [%s]", envName, envValue);
        System.setProperty(propertyName, envValue);
    }

    private static String parseEnvironmentProperty(String propertyValue) {
        //
        // First two characters are ${, last character is }
        //
        return propertyValue.substring(2, propertyValue.length() - 1);
    }

    private static void setPropertyReference(String propertyName, String propertyValue) {
        String requiredName  = parsePropertyReference(propertyValue);
        String requiredValue = getRequired(requiredName);

        JamLogger.info("Resolved property [%s] => [%s]", propertyName, requiredValue);
        System.setProperty(propertyName, requiredValue);
    }

    private static String parsePropertyReference(String propertyValue) {
        //
        // First and last characters are quotation marks...
        //
        return propertyValue.substring(1, propertyValue.length() - 1);
    }

    /**
     * Writes system properties to a file.
     *
     * <p>Property names may be filtered by a list of desired prefixes
     * to eliminate the standard properties assigned by the JVM.
     *
     * @param file the path to the output file.
     *
     * @param prefixes optional prefixes used to filter the property
     * names: properties will be omitted unless their name begins with
     * one of the prefixes.
     */
    public static void writeProperties(File file, String... prefixes) {
        PrintWriter writer = IOUtil.openWriter(file, false);

        try {
            writeProperties(writer, prefixes);
        }
        finally {
            IOUtil.close(writer);
        }
    }

    /**
     * Writes system properties to an output stream.
     *
     * <p>Property names may be filtered by a list of desired prefixes
     * to eliminate the standard properties assigned by the JVM.
     *
     * @param writer the output destination.
     *
     * @param prefixes optional prefixes used to filter the property
     * names: properties will be omitted unless their name begins with
     * one of the prefixes.
     */
    public static void writeProperties(PrintWriter writer, String... prefixes) {
        Map<String, String> properties;

        if (prefixes.length > 0)
            properties = filter(prefixes);
        else
            properties = all();

        for (String name : properties.keySet())
            writer.println(name + " = " + properties.get(name));
    }
}
