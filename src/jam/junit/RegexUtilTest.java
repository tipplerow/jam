package jam.junit;

import java.util.Arrays;
import java.util.regex.Pattern;

import jam.util.RegexUtil;

import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class RegexUtilTest extends NumericTestBase {
        
    private static void testSplit(Pattern pattern, String string, int limit, String... expected) {
	String[] fields = pattern.split(string, limit);
	assertEquals(expected.length, fields.length);

	for (int k = 0; k < expected.length; k++)
	    assertEquals(expected[k], fields[k]);
    }

    @Test public void testCloseParen() {
        testSplit(RegexUtil.CLOSE_PAREN, "abc(def)ghi", 2, "abc(def", "ghi");
    }

    @Test public void testColon() {
        testSplit(RegexUtil.COLON, "abc:def", 2, "abc", "def");
        testSplit(RegexUtil.COLON, "abc;def", 1, "abc;def");
    }

    @Test public void testCppComment() {
        testSplit(RegexUtil.CPP_COMMENT,    "abc//def", 2, "abc", "def");
        testSplit(RegexUtil.PYTHON_COMMENT, "abc#def",  1, "abc#def");
    }

    @Test public void testMultiWhiteSpace() {
	testSplit(RegexUtil.MULTI_WHITE_SPACE, "", 0, "");
	testSplit(RegexUtil.MULTI_WHITE_SPACE, "abc", 0, "abc");
	testSplit(RegexUtil.MULTI_WHITE_SPACE, "abc def", 0, "abc", "def");
	testSplit(RegexUtil.MULTI_WHITE_SPACE, "abc   def  ghi", 2, "abc", "def  ghi");
    }

    @Test public void testOpenParen() {
        testSplit(RegexUtil.OPEN_PAREN, "abc(def)ghi", 2, "abc", "def)ghi");
    }

    @Test public void testParseDouble() {
        Pattern  pattern  = RegexUtil.COMMA;
        String   string   = "  1.0,  2.0,  3.0  ";
        double[] expected = new double[] { 1.0, 2.0, 3.0 };

        assertDouble(expected, RegexUtil.parseDouble(pattern, string));
    }

    @Test(expected = RuntimeException.class)
    public void testParseDoubleInvalid() {
        RegexUtil.parseDouble(RegexUtil.COMMA, "1, 2, 3", 4);
    }

    @Test public void testParseInt() {
        Pattern pattern  = RegexUtil.COMMA;
        String  string   = "  1,  2,  3  ";
        int[]   expected = new int[] { 1, 2, 3 };

        assertTrue(Arrays.equals(expected, RegexUtil.parseInt(pattern, string)));
    }

    @Test(expected = RuntimeException.class)
    public void testParseIntInvalid() {
        RegexUtil.parseInt(RegexUtil.COMMA, "1, 2, 3", 4);
    }

    @Test public void testPythonComment() {
        testSplit(RegexUtil.CPP_COMMENT,    "abc//def", 1, "abc//def");
        testSplit(RegexUtil.PYTHON_COMMENT, "abc#def",  2, "abc", "def");
    }

    @Test public void testSemiolon() {
        testSplit(RegexUtil.SEMICOLON, "abc:def", 1, "abc:def");
        testSplit(RegexUtil.SEMICOLON, "abc;def", 2, "abc", "def");
    }

    @Test public void testStripComment() {
        assertEquals("abc def", RegexUtil.stripComment(RegexUtil.PYTHON_COMMENT, " abc def "));
        assertEquals("abc", RegexUtil.stripComment(RegexUtil.PYTHON_COMMENT, " abc # def "));
        assertEquals("", RegexUtil.stripComment(RegexUtil.PYTHON_COMMENT, " # abc def "));
        assertEquals("", RegexUtil.stripComment(RegexUtil.PYTHON_COMMENT, "# abc def "));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.RegexUtilTest");
    }
}
