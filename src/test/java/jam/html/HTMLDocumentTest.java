
package jam.html;

import java.io.File;
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

public class HTMLDocumentTest {
    private static final File vxxFile = new File("data/test/VXX_options.html");
    private static final HTMLDocument document = HTMLDocument.parse(vxxFile);

    @Test public void testFindTable() {
        HTMLTable table = document.findTable("calls");
        assertTrue(table.hasClass("calls"));
    }

    @Test public void testFindAllTables() {
        List<HTMLTable> tables = document.findTables();

        assertEquals(2, tables.size());
        assertTrue(tables.get(0).hasClass("calls"));
        assertTrue(tables.get(1).hasClass("puts"));
    }

    @Test public void testFindClassTables() {
        List<HTMLTable> tables = document.findTables("calls");

        assertEquals(1, tables.size());
        assertTrue(tables.get(0).hasClass("calls"));
    }

    @Test public void testSelect() {
        assertEquals(2, document.select("table").size());
    }

    @Test public void testSelectTagAttr() {
        HTMLElement element = document.selectUnique("span[data-reactid=50]");
        assertEquals(25.89, element.getDouble(), 0.01);

        element = document.selectUniqueTagAttr("span", "data-reactid", "50");
        assertEquals(25.89, element.getDouble(), 0.01);
    }

    @Test public void testSelectTagClass() {
        assertEquals(1, document.select("table.calls").size());
        assertEquals(1, document.select("table.puts").size());
        assertEquals(0, document.select("table.foo").size());

        assertEquals(1, document.selectTagClass("table", "calls").size());
        assertEquals(1, document.selectTagClass("table", "puts").size());
        assertEquals(0, document.selectTagClass("table", "foo").size());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.html.HTMLDocumentTest");
    }
}
