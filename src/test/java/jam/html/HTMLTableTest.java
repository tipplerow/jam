
package jam.html;

import java.io.File;

import org.junit.*;
import static org.junit.Assert.*;

public class HTMLTableTest {
    private static final File vxxFile = new File("data/test/VXX_options.html");
    private static final HTMLDocument document = HTMLDocument.parse(vxxFile);

    private static final HTMLTable puts = HTMLTable.create(document.selectUniqueTagClass("table", "puts"));
    private static final HTMLTable calls = HTMLTable.create(document.selectUniqueTagClass("table", "calls"));

    @Test public void testRowCount() {
        assertEquals(64, calls.countRows());
        assertEquals(46, puts.countRows());
    }

    @Test public void testRowData() {
        HTMLTableRow row15 = puts.getRow(15);

        assertEquals(11, row15.countRowData());
        assertEquals("VXX200925P00025500", row15.getRowData(0).getText());
        assertEquals("2020-09-22 10:47AM EDT", row15.getRowData(1).getText());
        assertEquals("25.50", row15.getRowData(2).getText());
        assertEquals(25.50, row15.getRowData(2).getDouble(), 0.01);
        assertEquals("3,785", row15.getRowData(9).getText());
        assertEquals(3785, row15.getRowData(9).getInt());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.html.HTMLTableTest");
    }
}
