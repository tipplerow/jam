
package jam.collect;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

public abstract class MultiTableTestBase {
    public static final String name1 = "Alex";
    public static final String name2 = "Neil";
    public static final String name3 = "Geddy";

    public static final LocalDate date1 = LocalDate.parse("1976-04-01");
    public static final LocalDate date2 = LocalDate.parse("2112-12-21");

    public static final CompKey key11 = new CompKey(name1, date1);
    public static final CompKey key12 = new CompKey(name1, date2);
    public static final CompKey key21 = new CompKey(name2, date1);
    public static final CompKey key22 = new CompKey(name2, date2);
    public static final CompKey key31 = new CompKey(name3, date1);
    public static final CompKey key32 = new CompKey(name3, date2);

    public static final CompRecord rec111 = new CompRecord(key11, 111);
    public static final CompRecord rec112 = new CompRecord(key11, 112);
    public static final CompRecord rec121 = new CompRecord(key12, 121);
    public static final CompRecord rec122 = new CompRecord(key12, 122);
    public static final CompRecord rec21  = new CompRecord(key21, 21);
    public static final CompRecord rec22  = new CompRecord(key22, 22);
    public static final CompRecord rec31  = new CompRecord(key31, 31);
    public static final CompRecord rec32  = new CompRecord(key32, 32);

    private <K> void assertFetch(MultiTable<K, CompRecord> table, K key, List<CompRecord> expected) {
        assertTrue(JamCollections.equalsContents(table.fetch(key), expected));
    }

    private void populate(MultiTable<?, CompRecord> table) {
        table.delete();

        table.store(rec111);
        table.store(rec121);
        table.store(rec21);
        table.store(rec22);
        table.store(rec31);
        table.store(rec32);

        table.store(rec111);
        table.store(rec121);
        table.store(rec21);
        table.store(rec22);
        table.store(rec31);
        table.store(rec32);

        assertEquals(6, table.count());
    }

    public void runNameKeyTest(MultiTable<String, CompRecord> table) {
        populate(table);

        assertFetch(table, name1, List.of(rec111, rec121));
        assertFetch(table, name2, List.of(rec21, rec22));
        assertFetch(table, name3, List.of(rec31, rec32));

        assertTrue(table.delete(rec111));
        assertTrue(table.delete(rec22));
        assertFalse(table.delete(rec111));
        assertFalse(table.delete(rec22));

        assertEquals(4, table.count());

        assertFetch(table, name1, List.of(rec121));
        assertFetch(table, name2, List.of(rec21));
        assertFetch(table, name3, List.of(rec31, rec32));
    }
}
