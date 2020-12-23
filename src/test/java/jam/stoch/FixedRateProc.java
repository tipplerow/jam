
package jam.stoch;

import org.junit.*;
import static org.junit.Assert.*;

final class FixedRateProc extends StochProc {
    private final StochRate rate;

    private FixedRateProc(double rate) {
        this.rate = StochRate.valueOf(rate);
    }

    static FixedRateProc create(double rate) {
        return new FixedRateProc(rate);
    }

    @Override public StochRate getStochRate() {
        return rate;
    }

    @Override public String toString() {
        return String.format("FixedRateProc(%d, %f)", getProcIndex(), rate.doubleValue());
    }
}
