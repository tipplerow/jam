
package jam.util;

import java.util.List;
import java.util.regex.Pattern;

import org.junit.*;
import static org.junit.Assert.*;

public class StringUtilTest {
    @Test public void testBackslash() {
        assertEquals(1, StringUtil.BACK_SLASH.length());
        assertEquals(2, StringUtil.DOUBLE_BACK_SLASH.length());
    }

    @Test public void testChop() {
        assertEquals("cho", StringUtil.chop("chop"));
        assertEquals("", StringUtil.chop("c"));
    }

    @Test(expected = RuntimeException.class)
    public void testChopEmpty() {
        StringUtil.chop("");
    }

    @Test public void testMultiLine() {
        assertEquals(List.of(""), StringUtil.multiLine("", 5));
        assertEquals(List.of("abc"), StringUtil.multiLine("abc", 5));
        assertEquals(List.of("abcde"), StringUtil.multiLine("abcde", 5));
        assertEquals(List.of("abcde", "f"), StringUtil.multiLine("abcdef", 5));
        assertEquals(List.of("abcde", "fghij", "klm"), StringUtil.multiLine("abcdefghijklm", 5));
    }

    @Test public void testRemoveCommentText() {
        Pattern delim1 = RegexUtil.PYTHON_COMMENT;
        Pattern delim2 = RegexUtil.CPP_COMMENT;

        assertEquals("",  StringUtil.removeCommentText("#", delim1));
        assertEquals("#", StringUtil.removeCommentText("#", delim2));

        assertEquals("//", StringUtil.removeCommentText("//", delim1));
        assertEquals("",   StringUtil.removeCommentText("//", delim2));

        assertEquals("abc ",      StringUtil.removeCommentText("abc # def", delim1));
        assertEquals("abc # def", StringUtil.removeCommentText("abc # def", delim2));

        assertEquals("abc // def", StringUtil.removeCommentText("abc // def", delim1));
        assertEquals("abc ",       StringUtil.removeCommentText("abc // def", delim2));

        assertEquals("abc / def", StringUtil.removeCommentText("abc / def", delim1));
        assertEquals("abc / def", StringUtil.removeCommentText("abc / def", delim2));

        assertEquals("",          StringUtil.removeCommentText("# abc def", delim1));
        assertEquals("# abc def", StringUtil.removeCommentText("# abc def", delim2));

        assertEquals("// abc def", StringUtil.removeCommentText("// abc def", delim1));
        assertEquals("",           StringUtil.removeCommentText("// abc def", delim2));
    }

    @Test public void testRemovePrefix() {
        assertEquals("abc", StringUtil.removePrefix("abc", "def"));
        assertEquals("abc", StringUtil.removePrefix("abc", "abcdef"));
        assertEquals("def", StringUtil.removePrefix("abcdef", "abc"));
    }

    @Test public void testRemoveSuffix() {
        assertEquals("abc", StringUtil.removeSuffix("abc", "def"));
        assertEquals("abc", StringUtil.removeSuffix("abc", "defabc"));
        assertEquals("def", StringUtil.removeSuffix("defabc", "abc"));
    }

    @Test public void testRemoveWhiteSpace() {
        assertEquals("", StringUtil.removeWhiteSpace(""));
        assertEquals("abc", StringUtil.removeWhiteSpace("abc")); 
        assertEquals("abc", StringUtil.removeWhiteSpace("   a b  c  "));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.util.StringUtilTest");
    }
}
