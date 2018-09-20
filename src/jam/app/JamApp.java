
package jam.app;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
     * Creates a new application instance and reads system properties
     * from a set of property files.
     *
     * @param propertyFiles files containing system properties that
     * define the application parameters (may be empty if properties
     * are specfied elsewhere).
     */
    protected JamApp(String... propertyFiles) {
        this.propertyFiles = Arrays.copyOf(propertyFiles, propertyFiles.length);

        if (propertyFiles.length > 0)
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
        // Parent directory of the first property file or the working
        // directory...
        //
        if (propertyFiles.length > 0)
            return FileUtil.getParentName(new File(propertyFiles[0]));
        else
            return ".";
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
}
