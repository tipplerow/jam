
package jam.junit;

import java.util.List;

import jam.sql.SQLColumn;

import org.junit.*;
import static org.junit.Assert.*;

public class SQLColumnTest {
    @Test public void testCreate() {
        SQLColumn column = SQLColumn.create("pmid", "int");

        assertEquals("pmid", column.getName());
        assertEquals("int", column.getType());
        assertEquals(List.of(), column.getQualifiers());
    }

    @Test public void testJoin() {
        assertEquals("pmid int", SQLColumn.create("pmid", "int").join());
        assertEquals("pmid int UNIQUE", SQLColumn.create("pmid", "int").unique().join());
    }

    @Test public void testNotNull() {
        assertEquals("pmid int NOT NULL", SQLColumn.create("pmid", "int").notNull().join());
    }

    @Test public void testPrimaryKey() {
        assertEquals("pmid int PRIMARY KEY", SQLColumn.create("pmid", "int").primaryKey().join());
    }

    @Test public void testForeignKey() {
        assertEquals("journal_ta text REFERENCES journals(medline_ta)",
                     SQLColumn.create("journal_ta", "text").foreignKey("journals", "medline_ta").join());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.SQLColumnTest");
    }
}








