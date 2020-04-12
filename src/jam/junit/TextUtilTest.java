
package jam.junit;

import java.util.List;

import jam.text.TextUtil;

import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class TextUtilTest {
    @Test public void testNextWordIndex() {
        String text1 = "Jon, my brother-in-law, doesn't like lobster (  and he's from Maine  ].";
        String text2 = "Two essentials: cheese curds and cold beer.";

        assertEquals(0, TextUtil.nextWordIndex(text1, 0));

        assertEquals(5, TextUtil.nextWordIndex(text1, 1));
        assertEquals(5, TextUtil.nextWordIndex(text1, 2));
        assertEquals(5, TextUtil.nextWordIndex(text1, 3));
        assertEquals(5, TextUtil.nextWordIndex(text1, 4));
        assertEquals(5, TextUtil.nextWordIndex(text1, 5));

        assertEquals(8, TextUtil.nextWordIndex(text1, 6));
        assertEquals(8, TextUtil.nextWordIndex(text1, 7));
        assertEquals(8, TextUtil.nextWordIndex(text1, 8));

        assertEquals(24, TextUtil.nextWordIndex(text1, 9));
        assertEquals(24, TextUtil.nextWordIndex(text1, 10));
        assertEquals(24, TextUtil.nextWordIndex(text1, 11));
        assertEquals(24, TextUtil.nextWordIndex(text1, 12));
        assertEquals(24, TextUtil.nextWordIndex(text1, 13));
        assertEquals(24, TextUtil.nextWordIndex(text1, 14));
        assertEquals(24, TextUtil.nextWordIndex(text1, 15));
        assertEquals(24, TextUtil.nextWordIndex(text1, 16));
        assertEquals(24, TextUtil.nextWordIndex(text1, 17));
        assertEquals(24, TextUtil.nextWordIndex(text1, 18));
        assertEquals(24, TextUtil.nextWordIndex(text1, 19));
        assertEquals(24, TextUtil.nextWordIndex(text1, 20));
        assertEquals(24, TextUtil.nextWordIndex(text1, 21));
        assertEquals(24, TextUtil.nextWordIndex(text1, 22));
        assertEquals(24, TextUtil.nextWordIndex(text1, 23));
        assertEquals(24, TextUtil.nextWordIndex(text1, 24));

        assertEquals(48, TextUtil.nextWordIndex(text1, 43));
        assertEquals(48, TextUtil.nextWordIndex(text1, 44));
        assertEquals(48, TextUtil.nextWordIndex(text1, 45));
        assertEquals(48, TextUtil.nextWordIndex(text1, 46));
        assertEquals(48, TextUtil.nextWordIndex(text1, 47));
        assertEquals(48, TextUtil.nextWordIndex(text1, 48));

        assertEquals(52, TextUtil.nextWordIndex(text1, 49));
        assertEquals(52, TextUtil.nextWordIndex(text1, 50));
        assertEquals(52, TextUtil.nextWordIndex(text1, 51));
        assertEquals(52, TextUtil.nextWordIndex(text1, 52));

        assertEquals(62, TextUtil.nextWordIndex(text1, 59));
        assertEquals(62, TextUtil.nextWordIndex(text1, 60));
        assertEquals(62, TextUtil.nextWordIndex(text1, 61));
        assertEquals(62, TextUtil.nextWordIndex(text1, 62));
        
        assertEquals(71, TextUtil.nextWordIndex(text1, 63));
        assertEquals(71, TextUtil.nextWordIndex(text1, 67));

        assertEquals(16, TextUtil.nextWordIndex(text2, 13));
        assertEquals(16, TextUtil.nextWordIndex(text2, 14));
        assertEquals(16, TextUtil.nextWordIndex(text2, 15));
        assertEquals(16, TextUtil.nextWordIndex(text2, 16));
    }

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

    @Test public void testWordBegins() {
        String text1 = "TP53 is an important cancer gene.";
        String text2 = "He failed miserably at stand-up comedy.";
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.TextUtilTest");
    }
}
