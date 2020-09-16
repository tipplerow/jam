
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

    private static final String CONFIGURATION_PROPERTY_NAME = "log4j.configurationFile";
    private static final String DEFAULT_CONFIGURATION_FILE  = "conf/log4j.xml";

    public static enum Level { FATAL, ERROR, WARN, INFO, DEBUG, SILENT };

    // Prevent instantiation
    private JamLogger() {}

    static {
	configure();
	logger = LogManager.getLogger("jam.app.JamLogger");
    }

    private static void configure() {
	//
	// Note that we do not use JamProperties to handle this
	// because it uses the logger...
	//
	String configFile = System.getProperty(CONFIGURATION_PROPERTY_NAME);

	if (configFile == null) {
	    System.setProperty(CONFIGURATION_PROPERTY_NAME, DEFAULT_CONFIGURATION_FILE);
	}
	else {
	    System.out.println("Configuration file: " + configFile);
	}
    }

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
