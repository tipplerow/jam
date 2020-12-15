
package jam.stoch;

import jam.lang.OrdinalIndex;

import org.junit.*;
import static org.junit.Assert.*;

final class FixedRateProc implements StochProc {
    private final int index;
    private final StochRate rate;

    private static final OrdinalIndex ordinalIndex = OrdinalIndex.create();

    private FixedRateProc(double rate) {
        this.rate = StochRate.valueOf(rate);
        this.index = (int) ordinalIndex.next();
    }

    static FixedRateProc create(double rate) {
        return new FixedRateProc(rate);
    }

    @Override public int getProcIndex() {
        return index;
    }

    @Override public StochRate getStochRate() {
        return rate;
    }

    @Override public String toString() {
        return String.format("FixedRateProc(%d, %f)", getProcIndex(), rate.doubleValue());
    }
}
