
package jam.process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

class LengthCounterProcess extends BatchProcess<String, Integer> {
    public LengthCounterProcess(Collection<String> inputFull, int batchSize) {
        super(inputFull, batchSize);
    }

    public static List<Integer> runSequential(Collection<String> inputFull, int batchSize) {
        LengthCounterProcess process =
            new LengthCounterProcess(inputFull, batchSize);

        return process.runSequential();
    }

    @Override public List<Integer> runBatch(Collection<String> inputSlice) {
        List<Integer> outputSlice = new ArrayList<Integer>(inputSlice.size());

        for (String s : inputSlice)
            outputSlice.add(s.length());

        return outputSlice;
    }
}

public class BatchProcessTest {
    @Test public void testSequential() {
        List<Integer> outputFull =
            LengthCounterProcess.runSequential(List.of("abc", "foobar", "x", "yz", "zebra"), 2);

        assertEquals(List.of(3, 6, 1, 2, 5), outputFull);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.process.BatchProcessTest");
    }
}
