
package jam.junit;

import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import jam.util.IndexView;

import org.junit.*;
import static org.junit.Assert.*;

final class MyRecord {
    public final String key;
    public final Integer value;

    public MyRecord(String key, Integer value) {
        this.key = key;
        this.value = value;
    }
}

final class MyIndex extends IndexView<String, MyRecord> {
    private MyIndex(Collection<MyRecord> records) {
        super(new TreeMap<String, MyRecord>(), records, x -> x.key);
    }

    public static MyIndex create(Collection<MyRecord> records) {
        return new MyIndex(records);
    }
}

public class IndexViewTest {
    private static final String key1 = "abc";
    private static final String key2 = "def";
    private static final String key3 = "ghi";
    private static final String key4 = "jkl";

    private static final Integer val1 = 1;
    private static final Integer val2 = 2;
    private static final Integer val3 = 3;
    
    private static final MyRecord rec1 = new MyRecord(key1, val1);
    private static final MyRecord rec2 = new MyRecord(key2, val2);
    private static final MyRecord rec3 = new MyRecord(key3, val3);
    
    private static final MyIndex index =
        MyIndex.create(List.of(rec1, rec2, rec3));

    @Test public void testContains() {
        assertTrue(index.containsKey(key1));
        assertTrue(index.containsKey(key2));
        assertTrue(index.containsKey(key3));
        assertFalse(index.containsKey(key4));
    }

    @Test public void testGet() {
        assertTrue(rec1.equals(index.get(key1)));
        assertTrue(rec2.equals(index.get(key2)));
        assertTrue(rec3.equals(index.get(key3)));
        assertNull(index.get(key4));
    }

    @Test(expected = RuntimeException.class)
    public void testDuplicate() {
        MyIndex.create(List.of(rec1, new MyRecord(key1, 33)));
    }

    @Test(expected = RuntimeException.class)
    public void testPut() {
        index.put(key4, rec1);
    }

    @Test(expected = RuntimeException.class)
    public void testRemove() {
        index.remove(key2);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.IndexViewTest");
    }
}
