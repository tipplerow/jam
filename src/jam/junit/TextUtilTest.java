
package jam.junit;

import java.util.List;

import jam.util.TextUtil;

import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class TextUtilTest {
    @Test public void testSplitWords() {
        assertEquals(List.of("Too", "boo-yah", "foo", "therefore", "abc", "def"),
                     TextUtil.splitWords("Too boo-yah foo, therefore: abc def."));
    }

    @Test public void testTrimWord() {
        assertEquals("Word", TextUtil.trimWord("Word"));
        assertEquals("Word", TextUtil.trimWord("Word."));
        assertEquals("Word", TextUtil.trimWord("Word,"));
        assertEquals("Word", TextUtil.trimWord("(Word)"));

        assertEquals("Word's", TextUtil.trimWord(" Word's" ));
        assertEquals("Word's", TextUtil.trimWord(" Word's." ));
        assertEquals("Word's", TextUtil.trimWord(" Word's," ));
        assertEquals("Word's", TextUtil.trimWord(" (Word's)" ));
    }

   public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.TextUtilTest");
    }
}
