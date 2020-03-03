
package jam.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import jam.io.IOUtil;
import jam.lang.JamException;

/**
 * Runs command-line processes.
 */
public final class ProcessRunner {
    /**
     * Runs a command-line process.
     *
     * @param command the command-line program and its arguments.
     *
     * @return the console output from the process.
     */
    public static List<String> run(String... command) {
        return run(List.of(command));
    }

    /**
     * Runs a command-line process.
     *
     * @param command the command-line program and its arguments.
     *
     * @return the console output from the process.
     */
    public static List<String> run(List<String> command) {
        ProcessBuilder builder = new ProcessBuilder(command);

        try {
            Process process = builder.start();
            BufferedReader reader = IOUtil.openReader(process.getInputStream());

            return IOUtil.readLines(reader);
        }
        catch (IOException ex) {
            throw JamException.runtime(ex);
        }
    }
}
