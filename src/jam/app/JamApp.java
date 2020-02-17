
package jam.app;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jam.io.FileUtil;
import jam.io.IOUtil;
import jam.lang.JamException;

/**
 * Provides a common framework for all applications that read system
 * properties and write output files.
 */
public abstract class JamApp {
    private final List<String> propertyFiles;

    private final int trialIndex;
    private final File reportDir;
    private final Collection<Writer> autoClose = new ArrayList<Writer>();
    private final Map<String, PrintWriter> writerMap = new TreeMap<String, PrintWriter>();

    /**
     * Name of the system property which specifies the directory in
     * which to write the report files.  If this is not specified, the
     * report files will be written into the directory containing the
     * first property file (or the current working directory if there
     * were no property files specified).
     */
    public static final String REPORT_DIR_PROPERTY = "jam.app.reportDir";

    /**
     * Name of the system property that specifies the index of the
     * simulation trial (only used by iterative applications).
     */
    public static final String TRIAL_INDEX_PROPERTY = "jam.app.trialIndex";

    /**
     * Name of the output file containing all relevant environment
     * variables that were defined at the time of execution.
     */
    public static final String RUNTIME_ENVIRONMENT_FILE_NAME = "runtime.env";

    /**
     * Name of the output file containing all relevant system
     * properties that were defined at the time of execution.
     */
    public static final String RUNTIME_PROPERTY_FILE_NAME = "runtime.prop";

    /**
     * Creates a new application instance and reads system properties
     * from a set of property files.
     *
     * @param propertyFiles files containing system properties that
     * define the application parameters (may be empty if properties
     * are specfied elsewhere).
     */
    protected JamApp(String... propertyFiles) {
        this(List.of(propertyFiles));
    }

    /**
     * Creates a new application instance and reads system properties
     * from a set of property files.
     *
     * @param propertyFiles files containing system properties that
     * define the application parameters (may be empty if properties
     * are specfied elsewhere).
     */
    protected JamApp(List<String> propertyFiles) {
        this.propertyFiles = new ArrayList<String>(propertyFiles);

        if (!propertyFiles.isEmpty())
            JamProperties.loadFiles(propertyFiles, false);

        this.reportDir = resolveReportDir();
        this.trialIndex = resolveTrialIndex();
    }

    private File resolveReportDir() {
        String defaultDir = getDefaultReportDir();
        String reportDir  = JamProperties.getOptional(REPORT_DIR_PROPERTY, defaultDir);

        return new File(reportDir);
    }

    private String getDefaultReportDir() {
        //
        // Parent directory of the first property file or the working
        // directory...
        //
        if (propertyFiles.isEmpty())
            return ".";
        else
            return FileUtil.getParentName(new File(propertyFiles.get(0)));
    }

    private static int resolveTrialIndex() {
        return JamProperties.getOptionalInt(TRIAL_INDEX_PROPERTY, 0);
    }

    /**
     * Closes all writers that were created by the {@code openWriter}
     * method and all other {@code Closeable} objects registered via
     * {@code autoClose(Closeable)}.
     */
    public void autoClose() {
        for (Writer writer : autoClose) {
            IOUtil.flush(writer);
            IOUtil.close(writer);
        }
    }

    /**
     * Registers a writer to be closed by the {@code close()} method.
     *
     * <p>Report writers opened by the {@code openWriter()} method are
     * automatically registered.
     *
     * @param writer the report writer to the closed.
     */
    public void autoClose(Writer writer) {
        autoClose.add(writer);
    }

    /**
     * Returns the directory where report files will be written.
     *
     * @return the directory where report files will be written.
     */
    public final File getReportDir() {
        return reportDir;
    }

    /**
     * Returns a file located in the report directory.
     *
     * @param baseName the base name for the report file.
     *
     * @return a file with the given base name located in the report
     * directory.
     */
    public File getReportFile(String baseName) {
        return new File(reportDir, baseName);
    }

    /**
     * Returns the index of the simulation trial (as assigned via
     * system properties), or zero if the trial index is not set.
     *
     * @return the index of the simulation trial (as assigned via
     * system properties), or zero if the trial index is not set.
     */
    public int getTrialIndex() {
        return trialIndex;
    }

    /**
     * Opens a writer for a file located in the report directory.
     *
     * <p>This class maintains a map of all print writers created by
     * this method (indexed by base name) and will close all writers
     * when the {@code close()} method is called.
     *
     * @param baseName the base name for the report file.
     *
     * @return a writer for the specified file.
     *
     * @throws RuntimeException unless the base name is unique and the
     * file is open for writing.
     */
    public PrintWriter openWriter(String baseName) {
        if (writerMap.containsKey(baseName))
            throw JamException.runtime("Duplicate base name: [%s]", baseName);

        PrintWriter writer = IOUtil.openWriter(getReportFile(baseName));
        writerMap.put(baseName, writer);

        autoClose(writer);
        return writer;
    }

    /**
     * Writes the relevant runtime environment variables and system
     * properties to their respective report files.
     */
    public void reportRuntime() {
        writeRuntimeEnv("JAM_");
        writeRuntimeProperties("jam.");
    }

    /**
     * Writes the relevant runtime environment variables to the file
     * name specified by {@code RUNTIME_ENVIRONMENT_FILE_NAME}.
     *
     * @param prefixes environment variables will be omitted unless
     * their name begins with one of the prefixes.
     */
    public void writeRuntimeEnv(String... prefixes) {
        writeRuntime(RUNTIME_ENVIRONMENT_FILE_NAME, JamEnv.filter(prefixes));
    }

    /**
     * Writes the relevant runtime properties to the file name
     * specified by {@code RUNTIME_PROPERTY_FILE_NAME}.
     *
     * @param prefixes properties will be omitted unless their name
     * begins with one of the prefixes.
     */
    public void writeRuntimeProperties(String... prefixes) {
        writeRuntime(RUNTIME_PROPERTY_FILE_NAME, JamProperties.filter(prefixes));
    }

    private void writeRuntime(String fileName, Map<String, String> runtime) {
        PrintWriter writer = openWriter(fileName);

        for (Map.Entry<String, String> entry : runtime.entrySet())
            writer.println(entry.getKey() + " = " + entry.getValue());

        writer.flush();
        writer.close();
    }
}
