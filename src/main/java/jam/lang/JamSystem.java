
package jam.lang;

import java.util.List;

import jam.app.JamEnv;
import jam.process.ProcessRunner;

/**
 * Provides information about the underlying operating system.
 */
public final class JamSystem {
    private static final String SYSCTL_CORE_COUNT = "machdep.cpu.core_count";
    private static final String SYSCTL_CPU_COUNT  = "hw.ncpu";

    /**
     * Environment variable that defines the number of CPU cores on
     * the machine.
     */
    public static final String CORE_COUNT_ENV = "JAM_CPU_CORE_COUNT";

    /**
     * Environment variable that defines the number of virtual CPUs
     * on the machine.
     */
    public static final String CPU_COUNT_ENV = "JAM_NCPU";

    /**
     * Returns the number of CPU cores on the machine.
     *
     * @return the number of CPU cores on the machine.
     */
    public static int getCoreCount() {
        if (JamEnv.isSet(CORE_COUNT_ENV))
            return Integer.valueOf(JamEnv.getRequired(CORE_COUNT_ENV));

        List<String> output = ProcessRunner.run("sysctl", "-n", SYSCTL_CORE_COUNT);

        if (output.size() != 1)
            throw JamException.runtime("Unexpected result from `sysctl`: [%s].", output);

        return Integer.valueOf(output.get(0));
    }

    /**
     * Returns the number of CPU cores on the machine.
     *
     * @return the number of CPU cores on the machine.
     */
    public static int getCPUCount() {
        if (JamEnv.isSet(CPU_COUNT_ENV))
            return Integer.valueOf(JamEnv.getRequired(CPU_COUNT_ENV));

        List<String> output = ProcessRunner.run("sysctl", "-n", SYSCTL_CPU_COUNT);

        if (output.size() != 1)
            throw JamException.runtime("Unexpected result from `sysctl`: [%s].", output);

        return Integer.valueOf(output.get(0));
    }
}

