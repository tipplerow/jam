
package jam.junit;

import java.util.List;

import jam.sql.SQLColumn;

import org.junit.*;
import static org.junit.Assert.*;

public class SQLColumnTest {
    @Test public void testCreate() {
        SQLColumn column = SQLColumn.create("pmid", "integer");

        assertEquals("pmid", column.getName());
        assertEquals("integer", column.getType());
        assertEquals(List.of(), column.getQualifiers());
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

    @Test public void testForeignKey() {
        assertEquals("journal_ta text REFERENCES journals(medline_ta)",
                     SQLColumn.create("journal_ta", "text").foreignKey("journals", "medline_ta").join());
    }

    @Test public void testImmutability() {
        SQLColumn column = SQLColumn.create("pmid", "integer");

        column.primaryKey();
        assertEquals("pmid integer", column.join());

        column.unique();
        assertEquals("pmid integer", column.join());

        column.notNull();
        assertEquals("pmid integer", column.join());

        column.foreignKey("journals", "medline_ta");
        assertEquals("pmid integer", column.join());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.SQLColumnTest");
    }
}








