
package jam.junit;

import jam.sql.SQLColumn;

import org.junit.*;
import static org.junit.Assert.*;

public class SQLColumnTest {
    @Test public void testCreate() {
        SQLColumn column = SQLColumn.create("pmid", "integer");

        assertEquals("pmid", column.getName());
        assertEquals("integer", column.getType());
    }

    @Test public void testJoin() {
        assertEquals("pmid integer", SQLColumn.create("pmid", "integer").join());
        assertEquals("pmid integer UNIQUE", SQLColumn.create("pmid", "integer").unique().join());
    }

    @Test public void testNotNull() {
        assertEquals("pmid integer NOT NULL", SQLColumn.create("pmid", "integer").notNull().join());
    }

    @Test public void testPrimaryKey() {
        assertEquals("pmid integer PRIMARY KEY", SQLColumn.create("pmid", "integer").primaryKey().join());
    }

    @Test public void testWithIndex() {
        assertEquals("pmid integer", SQLColumn.create("pmid", "integer").withIndex().join());
        assertEquals("pmid integer NOT NULL", SQLColumn.create("pmid", "integer").withIndex().notNull().join());
    }

    @Test public void testImmutability() {
        SQLColumn column = SQLColumn.create("pmid", "integer");

        column.primaryKey();
        assertEquals("pmid integer", column.join());

        column.unique();
        assertEquals("pmid integer", column.join());

        column.notNull();
        assertEquals("pmid integer", column.join());
    }

    @Test public void testQualifiers() {
        SQLColumn base = SQLColumn.create("pmid", "integer");
        assertQualifiers(base, false, false, false, false);

        SQLColumn pkey = base.primaryKey();
        assertQualifiers(base, false, false, false, false);
        assertQualifiers(pkey, true, false, false, false);

        SQLColumn unique = base.unique();
        assertQualifiers(base, false, false, false, false);
        assertQualifiers(unique, false, true, false, false);

        SQLColumn uniqueNotNull = unique.notNull();
        assertQualifiers(base, false, false, false, false);
        assertQualifiers(unique, false, true, false, false);
        assertQualifiers(uniqueNotNull, false, true, true, false);

        SQLColumn withIndex = base.withIndex();
        assertQualifiers(base, false, false, false, false);
        assertQualifiers(withIndex, false, false, false, true);

        SQLColumn withIndexNotNull = withIndex.notNull();
        assertQualifiers(base, false, false, false, false);
        assertQualifiers(withIndex, false, false, false, true);
        assertQualifiers(withIndexNotNull, false, false, true, true);
    }

    private void assertQualifiers(SQLColumn column,
                                  boolean   isPrimaryKey,
                                  boolean   isUnique,
                                  boolean   isNotNull,
                                  boolean   hasIndex) {
        assertEquals(isPrimaryKey, column.isPrimaryKey());
        assertEquals(isUnique, column.isUnique());
        assertEquals(isNotNull, column.isNotNull());
        assertEquals(hasIndex, column.hasIndex());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.SQLColumnTest");
    }
}








