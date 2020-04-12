
package jam.junit;

import java.util.ArrayList;
import java.util.List;

import jam.text.WordIterator;

import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class WordIteratorTest {
    @Test public void testIteration() {
        assertSplit("");
        assertSplit("abc", "abc");
        assertSplit("How now, brown cow... Wow!", "How", "now", "brown", "cow", "Wow");
        assertSplit("  Jon, my brother-in-law, doesn't like lobster (  and he's from Maine  ].  ",
                    "Jon", "my", "brother-in-law", "doesn't", "like", "lobster", "and", "he's", "from", "Maine");
    }

    private void assertSplit(String text, String... words) {
        assertEquals(List.of(words), split(text));
    }

    private List<String> split(String text) {
        List<String> words = new ArrayList<String>();
        WordIterator iterator = WordIterator.split(text);

        while (iterator.hasNext())
            words.add(iterator.next());

        return words;
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemove() {
        WordIterator iterator = WordIterator.split("foo bar");

        iterator.next();
        iterator.remove();
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.WordIteratorTest");
    }
}
