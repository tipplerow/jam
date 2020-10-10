
package jam.collect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

public class ReadOnlyIteratorTest {
    private static final String s1 = "abc";
    private static final String s2 = "def";
    private static final String s3 = "ghi";

    @Test public void testBasic() {
        Iterator<String> iterator = makeIterator();

        assertTrue(iterator.hasNext());
        assertEquals(s1, iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals(s2, iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals(s3, iterator.next());

        assertFalse(iterator.hasNext());
    }

    private Iterator<String> makeIterator() {
        List<String> list = new ArrayList<String>();

        list.add(s1);
        list.add(s2);
        list.add(s3);

        return ReadOnlyIterator.create(list);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemove() {
        Iterator<String> iterator = makeIterator();
        
        iterator.next();
        iterator.remove();
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.collect.ReadOnlyIteratorTest");
    }
}
