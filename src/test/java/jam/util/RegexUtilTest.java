
package jam.util;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import jam.junit.NumericTestBase;

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

    @Test public void testCountFields() {
        assertEquals(1, RegexUtil.countFields(RegexUtil.COMMA, "abc"));
        assertEquals(2, RegexUtil.countFields(RegexUtil.COMMA, "a, bc"));
        assertEquals(3, RegexUtil.countFields(RegexUtil.COMMA, "a, b, c"));
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

    @Test public void testDot() {
        testSplit(RegexUtil.DOT,   "abc.def", 2, "abc", "def");
        testSplit(RegexUtil.COLON, "abc.def", 1, "abc.def");
    }

    @Test public void testIntegers() {
        Predicate<String> predicate = RegexUtil.INTEGERS.asPredicate();

        assertTrue(predicate.test("123"));
        assertTrue(predicate.test("2-DG"));
        assertTrue(predicate.test("2-DG"));
        assertTrue(predicate.test("TP53"));
        assertTrue(predicate.test("PIK3CA"));

        assertFalse(predicate.test(""));
        assertFalse(predicate.test("abc"));
        assertFalse(predicate.test("anti-inflammatory"));
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
        String  string   = "  1,  2,  3, 1E4  ";
        int[]   expected = new int[] { 1, 2, 3, 10000 };

        assertTrue(Arrays.equals(expected, RegexUtil.parseInt(pattern, string)));
    }

    @Test(expected = RuntimeException.class)
    public void testParseIntInvalid() {
        RegexUtil.parseInt(RegexUtil.COMMA, "1, 2, 3", 4);
    }

    @Test public void testPipe() {
        testSplit(RegexUtil.PIPE, "abc def", 1, "abc def");
        testSplit(RegexUtil.PIPE, "abc|def", 2, "abc", "def");
    }

    @Test public void testPunctuation() {
        testSplit(RegexUtil.PUNCTUATION, "abc, def. ghi", 3, "abc", " def", " ghi");
    }

    @Test public void testPunctuationWhiteSpace() {
        assertSplit(RegexUtil.PUNCTUATION_WHITE_SPACE, "abc, def. ghi", "abc", "def", "ghi");
        assertSplit(RegexUtil.PUNCTUATION_WHITE_SPACE, "abc, def: ghi. foo (bar).", "abc", "def", "ghi", "foo", "bar");
    }

    private void assertSplit(Pattern pattern, String string, String... expected) {
        assertTrue(Arrays.equals(expected, pattern.split(string)));
    }

    @Test public void testPythonComment() {
        testSplit(RegexUtil.CPP_COMMENT,    "abc//def", 1, "abc//def");
        testSplit(RegexUtil.PYTHON_COMMENT, "abc#def",  2, "abc", "def");
    }

    @Test public void testRemove() {
        assertEquals("2DG", RegexUtil.remove(RegexUtil.MULTI_NON_WORD, "2DG"));
        assertEquals("2DG", RegexUtil.remove(RegexUtil.MULTI_NON_WORD, "2-DG"));
        assertEquals("2DG", RegexUtil.remove(RegexUtil.MULTI_NON_WORD, "2---DG"));
        assertEquals("2DG", RegexUtil.remove(RegexUtil.MULTI_NON_WORD, "2-D   G"));
        assertEquals("2_DG", RegexUtil.remove(RegexUtil.MULTI_NON_WORD, "2_DG"));
    }

    @Test public void testReplacement() {
        assertEquals("2DG", RegexUtil.replace(RegexUtil.MULTI_NON_WORD, "2DG", "_"));
        assertEquals("2_DG", RegexUtil.replace(RegexUtil.MULTI_NON_WORD, "2-DG", "_"));
        assertEquals("2_DG", RegexUtil.replace(RegexUtil.MULTI_NON_WORD, "2---DG", "_"));
        assertEquals("2_D_G", RegexUtil.replace(RegexUtil.MULTI_NON_WORD, "2-D   G", "_"));
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
        org.junit.runner.JUnitCore.main("jam.util.RegexUtilTest");
    }
}
