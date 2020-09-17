
package jam.app;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Manages the logger used by the Jam library and provides a thin
 * layer of logging methods above the underlying logger implementation
 * (currently Apache Log4J).
 */
public final class JamLogger {
    private static Logger logger = null;

    private static final String CONFIGURATION_ENV_NAME = "LOG4J_CONF";
    private static final String CONFIGURATION_PROPERTY_NAME = "log4j.configurationFile";

    // Prevent instantiation
    private JamLogger() {}

    static {
	configure();
	logger = LogManager.getLogger("jam.app.JamLogger");
    }

    private static void configure() {
	String configFile = System.getenv(CONFIGURATION_ENV_NAME);

	if (configFile != null)
	    System.setProperty(CONFIGURATION_PROPERTY_NAME, configFile);
	else
	    System.out.println("Using default log4j configuration...");
    }

    public static enum Level { FATAL, ERROR, WARN, INFO, DEBUG, SILENT };

    public static void fatal(Object message) {
        logger.fatal(message);
    }

    public static void fatal(String format, Object... args) {
        logger.fatal(String.format(format, args));
    }

    public static void error(Object message) {
        logger.error(message);
    }

    public static void error(String format, Object... args) {
        logger.error(String.format(format, args));
    }

    public static void warn(Object message) {
        logger.warn(message);
    }

    public static void warn(String format, Object... args) {
        logger.warn(String.format(format, args));
    }

    public static void info(Object message) {
        logger.info(message);
    }

    public static void info(String format, Object... args) {
        logger.info(String.format(format, args));
    }

    public static void debug(Object message) {
        logger.debug(message);
    }

    public static void debug(String format, Object... args) {
        logger.debug(String.format(format, args));
    }

    public static void log(Level level, Object message) {
        switch (level) {
        case FATAL:
            fatal(message);
            break;

        case ERROR:
            error(message);
            break;

        case WARN:
            warn(message);
            break;

        case INFO:
            info(message);
            break;

        case DEBUG:
            debug(message);
            break;
        }
    }

    public static void log(Level level, String format, Object... args) {
        log(level, String.format(format, args));
    }
}
