
package jam.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import jam.app.JamLogger;
import jam.io.IOUtil;
import jam.lang.JamException;

/**
 * Executes a command-line process and stores the results.
 */
public final class JamProcess {
    private final ProcessBuilder builder;

    private Process process;
    private List<String> stdout;
    private List<String> stderr;

    private JamProcess(List<String> command) {
        this.builder = new ProcessBuilder(command);
    }

    /**
     * Runs a command-line process.
     *
     * @param command the command-line program and its arguments.
     *
     * @return the process object which holds the standard output
     * standard error lines.
     */
    public static JamProcess run(String... command) {
        return run(List.of(command));
    }

    /**
     * Runs a command-line process and logs console error messages.
     *
     * @param command the command-line program and its arguments.
     *
     * @return the process object which holds the standard output
     * standard error lines.
     */
    public static JamProcess run(List<String> command) {
        JamProcess process = new JamProcess(command);

        process.run();
        return process;
    }

    private void run() {
        JamLogger.info("Running system command: %s", builder.command());

        try {
            process = builder.start();

            BufferedReader stdoutReader = IOUtil.openReader(process.getInputStream());
            BufferedReader stderrReader = IOUtil.openReader(process.getErrorStream());

            stdout = IOUtil.readLines(stdoutReader);
            stderr = IOUtil.readLines(stderrReader);

            for (String error : stderr)
                JamLogger.error(error);
        }
        catch (IOException ex) {
            throw JamException.runtime(ex);
        }
    }

    /**
     * Returns the console output written to the {@code stderr} stream.
     *
     * @return the console output written to the {@code stderr} stream.
     */
    public List<String> stderr() {
        return Collections.unmodifiableList(stderr);
    }

    /**
     * Returns the console output written to the {@code stdout} stream.
     *
     * @return the console output written to the {@code stdout} stream.
     */
    public List<String> stdout() {
        return Collections.unmodifiableList(stdout);
    }

    /**
     * Infers the success or failure of the command by checking for
     * data on the {@code stderr} stream.
     *
     * @return {@code true} iff the {@code stderr} output stream is
     * empty.
     */
    public boolean success() {
        return stderr.isEmpty();
    }
}
