
package jam.text;

import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

public class TextMatcherTest {
    @Test public void testCaseSensitive() {
        String text1 = "The witness took the stand";
        String text2 = "The witness did not understand the question.";
        String text3 = "He bombed as a stand-up comic.";
        String text4 = "Jon, my brother-in-law, doesn't like lobster (  and he's from Maine  ].";
        String text5 = "Two essentials: cheese curds and cold beer.";

        TextMatcher matcher = TextMatcher.create(false, "grandstand", "standard", "stand");
        assertTrue(matcher.matches(text1));
        assertEquals("stand", matcher.matchOne(text1));

        assertNull(matcher.matchOne(text2));
        assertNull(matcher.matchOne(text3));

        assertFalse(matcher.matches(text2));
        assertFalse(matcher.matches(text3));

        matcher = TextMatcher.caseSensitive(List.of("Stand"));
        assertNull(matcher.matchOne(text1));

        matcher = TextMatcher.ignoreCase("bandstand", "Stand");
        assertEquals("Stand", matcher.matchOne(text1));

        matcher = TextMatcher.create(List.of("ABC", "brother-in-law", "Maine", "essentials"), false);
        assertEquals("brother-in-law", matcher.matchOne(text4));
        assertEquals("essentials", matcher.matchOne(text5));

        matcher = TextMatcher.create(List.of("Jon, my", "boy"), false);
        assertEquals("Jon, my", matcher.matchOne(text4));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.text.TextMatcherTest");
    }
}
