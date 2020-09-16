
package jam.xml;

import org.w3c.dom.Element;

import org.junit.*;
import static org.junit.Assert.*;

public class DOMDocumentTest {
    private static final String DESC_SAMPLE_FILE = "data/test/mesh_desc_sample.xml";

    @Test public void testParse() {
        DOMDocument document = DOMDocument.parse(DESC_SAMPLE_FILE);

        assertEquals("DescriptorRecordSet", document.getDocumentElement().getTagName());
        
        ElementList descriptorList = document.getDocElementChildren();
        assertEquals(3, descriptorList.size());

        for (Element descriptorElement : descriptorList)
            assertEquals("DescriptorRecord", descriptorElement.getTagName());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.xml.DOMDocumentTest");
    }
}
