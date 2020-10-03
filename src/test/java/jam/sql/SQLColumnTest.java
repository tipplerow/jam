
package jam.sql;

import org.junit.*;
import static org.junit.Assert.*;

public class SQLColumnTest {
    private static final SQLEngine engine = SQLEngine.POSTGRES;

    @Test public void testCreate() {
        SQLColumn column = SQLColumn.ofInteger("pmid");

        assertEquals("pmid", column.getName());
        assertEquals(SQLType.INTEGER, column.getType());
    }

    @Test public void testJoin() {
        assertEquals("pmid INTEGER", SQLColumn.ofInteger("pmid").join(engine));
        assertEquals("pmid INTEGER UNIQUE", SQLColumn.ofInteger("pmid").unique().join(engine));
    }

    @Test public void testNotNull() {
        assertEquals("pmid INTEGER NOT NULL", SQLColumn.ofInteger("pmid").notNull().join(engine));
    }

    @Test public void testPrimaryKey() {
        assertEquals("pmid INTEGER PRIMARY KEY", SQLColumn.ofInteger("pmid").primaryKey().join(engine));
    }

    @Test public void testWithIndex() {
        assertEquals("pmid INTEGER", SQLColumn.ofInteger("pmid").withIndex().join(engine));
        assertEquals("pmid INTEGER NOT NULL", SQLColumn.ofInteger("pmid").withIndex().notNull().join(engine));
    }

    @Test public void testImmutability() {
        SQLColumn column = SQLColumn.ofInteger("pmid");

        column.primaryKey();
        assertEquals("pmid INTEGER", column.join(engine));

        column.unique();
        assertEquals("pmid INTEGER", column.join(engine));

        column.notNull();
        assertEquals("pmid INTEGER", column.join(engine));
    }

    @Test public void testQualifiers() {
        SQLColumn base = SQLColumn.ofInteger("pmid");
        assertQualifiers(base, false, false, false, false, false);

        SQLColumn compkey = base.compositeKey();
        assertQualifiers(compkey, true, false, false, false, true);

        SQLColumn pkey = base.primaryKey();
        assertQualifiers(base, false, false, false, false, false);
        assertQualifiers(pkey, false, true, false, false, false);

        SQLColumn unique = base.unique();
        assertQualifiers(base, false, false, false, false, false);
        assertQualifiers(unique, false, false, true, false, false);

        SQLColumn uniqueNotNull = unique.notNull();
        assertQualifiers(base, false, false, false, false, false);
        assertQualifiers(unique, false, false, true, false, false);
        assertQualifiers(uniqueNotNull, false, false, true, true, false);

        SQLColumn withIndex = base.withIndex();
        assertQualifiers(base, false, false, false, false, false);
        assertQualifiers(withIndex, false, false, false, false, true);

        SQLColumn withIndexNotNull = withIndex.notNull();
        assertQualifiers(base, false, false, false, false, false);
        assertQualifiers(withIndex, false, false, false, false, true);
        assertQualifiers(withIndexNotNull, false, false, false, true, true);
    }

    private void assertQualifiers(SQLColumn column,
                                  boolean   isCompositeKey,
                                  boolean   isPrimaryKey,
                                  boolean   isUnique,
                                  boolean   isNotNull,
                                  boolean   hasIndex) {
        assertEquals(isCompositeKey, column.isCompositeKey());
        assertEquals(isPrimaryKey, column.isPrimaryKey());
        assertEquals(isUnique, column.isUnique());
        assertEquals(isNotNull, column.isNotNull());
        assertEquals(hasIndex, column.hasIndex());
    }

    @Test public void testSerial() {
        SQLColumn serial = SQLColumn.ofSerial("key");
        assertEquals("key SERIAL PRIMARY KEY", serial.join(engine));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.sql.SQLColumnTest");
    }
}








