
package jam.report;

import java.io.Closeable;
import java.io.File;
import java.io.Flushable;
import java.io.PrintWriter;
import java.util.Collection;

import jam.io.DirUtil;
import jam.io.IOUtil;

/**
 * Provides a simple API for writing objects to report files.
 */
public final class ReportWriter<R extends ReportRecord> implements Closeable, Flushable {
    private final File reportDir;

    private File reportFile;
    private PrintWriter writer;

    private ReportWriter(File reportDir) {
        this.reportDir = reportDir;
        DirUtil.onDemand(reportDir);
    }

    /**
     * Creates a new report writer for a specific output directory.
     *
     * @param <R> the record type.
     *
     * @param reportDir the directory where the report file will be
     * written (created if necessary).
     *
     * @return a new report writer for the specified directory.
     *
     * @throws RuntimeException if the report directory does not exist
     * and cannot be created.
     */
    public static <R extends ReportRecord> ReportWriter<R> create(File reportDir) {
        return new ReportWriter<R>(reportDir);
    }

    /**
     * Writes records in a specific output directory, overwriting the
     * previous report file if it exists.
     *
     * @param <R> the runtime record type.
     *
     * @param reportDir the directory where the report file will be
     * written (created if necessary).
     *
     * @param records the records to write.
     *
     * @throws RuntimeException if the report directory does not exist
     * and cannot be created.
     */
    public static <R extends ReportRecord> void write(File reportDir, Collection<R> records) {
        ReportWriter<R> writer = ReportWriter.create(reportDir);

        try {
            writer.write(records);
        }
        finally {
            writer.close();
        }
    }

    /**
     * Writes records to the report file.
     *
     * @param records the records to write.
     */
    public void write(Collection<R> records) {
        for (R record : records)
            write(record);
    }

    /**
     * Writes a record to the report file.
     *
     * <p>On the first call to this method, an empty report file is
     * created, the header line is written, then the record itself
     * is written.
     *
     * @param record the record to write.
     */
    public void write(R record) {
        getWriter(record).println(record.formatLine());
    }

    private PrintWriter getWriter(R record) {
        if (writer == null)
            openWriter(record);

        return writer;
    }

    private void openWriter(R record) {
        writer = IOUtil.openWriter(getReportFile(record), false);
        writer.println(record.getHeaderLine());
    }

    private File getReportFile(R record) {
        if (reportFile == null)
            reportFile = new File(reportDir, record.getBaseName());

        return reportFile;
    }

    @Override public void close() {
        if (writer != null) {
            writer.close();
            writer = null;
        }
    }

    @Override public void flush() {
        if (writer != null)
            writer.flush();
    }
}
