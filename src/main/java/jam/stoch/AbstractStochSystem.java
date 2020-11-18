
package jam.stoch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jam.lang.JamException;

/**
 * Provides a base class for stochastic systems containing a fixed
 * number of processes.
 */
public abstract class AbstractStochSystem<P extends StochProc> implements StochSystem<P> {
    private final List<P> procs;

    /**
     * Creates a new stochastic system with a fixed number of
     * processes.
     *
     * <p>This base class makes a private copy of the input process
     * list, so the new system will not reflect subsequent changes to
     * the input list.
     *
     * @param procs the stochastic process which compose the system.
     *
     * @throws RuntimeException unless the indexes of the stochastic
     * processes match their location in the input list.
     */
    protected AbstractStochSystem(List<P> procs) {
        this.procs = Collections.unmodifiableList(new ArrayList<P>(procs));
        validate();
    }

    private void validate() {
        if (procs.isEmpty())
            throw JamException.runtime("No stochastic processes.");

        validateIndexing();
    }

    @Override public int countProcesses() {
        return procs.size();
    }

    @Override public P getProcess(int index) {
        return procs.get(index);
    }

    @Override public List<P> listProcesses() {
        return procs;
    }
}
