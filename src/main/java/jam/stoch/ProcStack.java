
package jam.stoch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import jam.lang.JamException;
import jam.math.DoubleComparator;

public final class ProcStack<P extends StochProc> {
    private final LinkedList<P> stack = new LinkedList<P>();

    private long nextCount = 0L;
    private long selectCount = 0L;

    private ProcStack(Collection<P> procs) {
        fillStack(procs);
    }

    private void fillStack(Collection<P> procs) {
        ArrayList<P> procList = new ArrayList<P>(procs);
        procList.sort(StochProc.DESCENDING_RATE_COMPARATOR);
        stack.addAll(procList);
    }

    public static <P extends StochProc> ProcStack<P> create(Collection<P> procs) {
        return new ProcStack<P>(procs);
    }

    public P select(double threshold) {
        ++selectCount;

        double rateTotal = 0.0;
        Iterator<P> iterator = stack.iterator();

        while (iterator.hasNext()) {
            ++nextCount;

            P proc = iterator.next();
            rateTotal += proc.getStochRate().doubleValue();

            if (DoubleComparator.DEFAULT.GE(rateTotal, threshold)) {
                //
                // Move this process to the top of the stack...
                //
                iterator.remove();
                stack.push(proc);

                return proc;
            }
        }

        // This should never happen if the threshold has been
        // specified properly....
        throw JamException.runtime("Process selection failed.");
    }

    public double getEfficiencyGain() {
        double linear = 0.5 * stack.size();
        double actual = ((double) nextCount) / ((double) selectCount);

        return linear / actual;
    }
}
