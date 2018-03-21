
package jam.app;

import java.io.File;
import java.io.PrintWriter;
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
    private final String[] propertyFiles;
    private final File reportDir;
    private final Map<String, PrintWriter> writerMap = new TreeMap<String, PrintWriter>();

    /**
     * Name of the system property which specifies the directory in
     * which to write the report files.  If this is not specified, the
     * report files will be written into the directory containing the
     * first property file.
     */
    public static final String REPORT_DIR_PROPERTY = "jam.app.reportDir";

    /**
     * Creates a new application instance and reads system properties
     * from a set of property files.
     *
     * @param propertyFiles one or more files containing the system
     * properties that define the application parameters.
     *
     * @throws IllegalArgumentException unless at least one property
     * file is specified.
     */
    protected JamApp(String[] propertyFiles) {
        if (propertyFiles.length < 1)
            throw new IllegalArgumentException("At least one property file is required.");

        this.propertyFiles = propertyFiles;
        JamProperties.loadFiles(propertyFiles, false);

        this.reportDir = resolveReportDir();
    }

    private File resolveReportDir() {
        String defaultDir = getDefaultReportDir();
        String reportDir  = JamProperties.getOptional(REPORT_DIR_PROPERTY, defaultDir);

        return new File(reportDir);
    }

    private String getDefaultReportDir() {
        //
        // Parent directory of the first property file...
        //
        return FileUtil.getParentName(new File(propertyFiles[0]));
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
     * Opens a writer for a file located in the report directory.
     *
     * <p>This class maintains a map of all print writers created by
     * this method (indexed by base name) and will close all writers
     * when the {@code closeWriters()} method is called.
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

        PrintWriter writer = IOUtil.openWriter(getReportFile(baseName), false);
        writerMap.put(baseName, writer);

        return writer;
    }

    /**
     * Closes all writers that were created by the {@code openWriter}
     * method.
     */
    public void closeWriters() {
        for (PrintWriter writer : writerMap.values())
            IOUtil.close(writer);
    }
}
