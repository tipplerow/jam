
package jam.junit;

import java.util.regex.Pattern;

import jam.util.RegexUtil;
import jam.util.StringUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class StringUtilTest {

    @Test public void testChop() {
        assertEquals("cho", StringUtil.chop("chop"));
        assertEquals("", StringUtil.chop("c"));
    }

    @Test(expected = RuntimeException.class)
    public void testChopEmpty() {
        StringUtil.chop("");
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

    @Test public void testRemoveWhiteSpace() {
        assertEquals("", StringUtil.removeWhiteSpace(""));
        assertEquals("abc", StringUtil.removeWhiteSpace("abc")); 
        assertEquals("abc", StringUtil.removeWhiteSpace("   a b  c  "));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.StringUtilTest");
    }
}
