
package jam.junit;

import java.util.List;

import org.jdom2.Element;

import jam.xml.JDOMDocument;

import org.junit.*;
import static org.junit.Assert.*;

public class JDOMDocumentTest {
    private static final String DESC_SAMPLE_FILE = "data/test/mesh_desc_sample.xml";

    @Test public void testParse() {
        JDOMDocument document = JDOMDocument.parse(DESC_SAMPLE_FILE);

        assertEquals("DescriptorRecordSet", document.getRootElement().getName());
        
        List<Element> descriptorList = document.getRootElementChildren();
        assertEquals(3, descriptorList.size());

        for (Element descriptorElement : descriptorList)
            assertEquals("DescriptorRecord", descriptorElement.getName());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.JDOMDocumentTest");
    }
}
