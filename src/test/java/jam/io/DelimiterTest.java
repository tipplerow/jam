
package jam.io;

import java.util.Arrays;

import jam.io.Delimiter;

import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class DelimiterTest {
    private void runOne(Delimiter delim, String line, String... fields) {
        assertEquals(line, delim.join(fields));
        assertTrue(Arrays.equals(fields, delim.split(line)));
    }

    private void testSplit(Delimiter delim, String line, String... expected) {
        assertTrue(Arrays.equals(expected, delim.split(line)));
    }

    @Test public void testComma() {
        runOne(Delimiter.COMMA, "A,B,C,D", "A", "B", "C", "D");
        runOne(Delimiter.COMMA, "A,B\\,C,D", "A", "B,C", "D");
    }

    @Test public void testHyphen() {
        runOne(Delimiter.HYPHEN, "A-B-C", "A", "B", "C");
    }

    @Test public void testPipe() {
        runOne(Delimiter.PIPE, "A|", "A", "");
        runOne(Delimiter.PIPE, "A||", "A", "", "");
        runOne(Delimiter.PIPE, "A||B|||C", "A", "", "B", "", "", "C");
        runOne(Delimiter.PIPE, "A|B|C|D", "A", "B", "C", "D");
        runOne(Delimiter.PIPE, "A|B\\|C|D", "A", "B|C", "D");
    }

    @Test public void testTab() {
        runOne(Delimiter.TAB, "A\tB\tC\tD", "A", "B", "C", "D");
    }

    @Test public void testWhiteSpace() {
        assertTrue(Arrays.equals(new String[] { "A", "B", "C", "D" }, Delimiter.WHITE_SPACE.split("A\tB    C D")));
        assertEquals("A B C D", Delimiter.WHITE_SPACE.join("A", "B", "C", "D"));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.io.DelimiterTest");
    }
}
