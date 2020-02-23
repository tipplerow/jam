
package jam.junit;

import java.util.ArrayList;
import java.util.List;

import jam.math.IntPair;
import jam.util.StreamUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class StreamUtilTest {
    @Test public void testApply() {
        int sampleSize = 100000;
        int concurrency = 10;

        List<Integer> source = createSource(sampleSize);
        List<IntPair> result = StreamUtil.apply(source, x -> IntPair.of(x, x * x), concurrency);

        assertSquare(result.get(0));
        assertSquare(result.get(sampleSize - 1));
    }

    private List<Integer> createSource(int size) {
        List<Integer> source = new ArrayList<Integer>(size);

        for (int index = 0; index < size; ++index)
            source.add(index);

        return source;
    }

    private void assertSquare(IntPair pair) {
        assertEquals(pair.first * pair.first, pair.second);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.StreamUtilTest");
    }
}
