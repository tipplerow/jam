
package jam.junit;

import java.util.List;

import jam.text.TextMatcher;

import org.junit.*;
import static org.junit.Assert.*;

public class TextMatcherTest {
    @Test public void testCaseSensitive() {
        String text1 = "The witness took the stand";
        String text2 = "The witness did not understand the question.";
        String text3 = "He bombed as a stand-up comic.";
        String text4 = "Jon, my brother-in-law, doesn't like lobster (  and he's from Maine  ].";
        String text5 = "Two essentials: cheese curds and cold beer.";

        TextMatcher matcher = TextMatcher.create(List.of("grandstand", "standard", "stand"), false);
        assertEquals("stand", matcher.matchOne(text1));

        assertNull(matcher.matchOne(text2));
        assertNull(matcher.matchOne(text3));

        matcher = TextMatcher.create(List.of("Stand"), false);
        assertNull(matcher.matchOne(text1));

        matcher = TextMatcher.create(List.of("bandstand", "Stand"), true);
        assertEquals("Stand", matcher.matchOne(text1));

        matcher = TextMatcher.create(List.of("ABC", "brother-in-law", "Maine", "essentials"), false);
        assertEquals("brother-in-law", matcher.matchOne(text4));
        assertEquals("essentials", matcher.matchOne(text5));

        matcher = TextMatcher.create(List.of("Jon, my", "boy"), false);
        assertEquals("Jon, my", matcher.matchOne(text4));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.TextMatcherTest");
    }
}
