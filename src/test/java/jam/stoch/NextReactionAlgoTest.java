
package jam.stoch;

import jam.stoch.decay.DecayProc;

import org.junit.*;
import static org.junit.Assert.*;

public class NextReactionAlgoTest extends AlgoTestBase {
    @Override public StochAlgo createAlgorithm() {
        return NextReactionAlgo.create(random, system);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.stoch.NextReactionAlgoTest");
    }
}
