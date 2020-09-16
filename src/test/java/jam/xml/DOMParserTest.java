
package jam.xml;

import org.w3c.dom.Element;

import org.junit.*;
import static org.junit.Assert.*;

public class DOMParserTest {
    private static final String DESC_SAMPLE_FILE = "data/test/mesh_desc_sample.xml";

    @Test public void testParser() {
        DOMDocument document = DOMDocument.parse(DESC_SAMPLE_FILE);
        DOMParser parser = new DOMParser();

        parser.assertTagName(document.getDocumentElement(), "DescriptorRecordSet");

        ElementList descriptorList = document.getDocElementChildren();
        assertEquals(3, descriptorList.size());

        for (Element descriptorElement : descriptorList)
            parser.assertTagName(descriptorElement, "DescriptorRecord");

        assertEquals("1", parser.getRequiredAttribute(descriptorList.get(0), "DescriptorClass"));
        assertEquals("bar", parser.getOptionalAttribute(descriptorList.get(0), "foo", "bar"));

        assertEquals(1, parser.getIntAttribute(descriptorList.get(0), "DescriptorClass"));
        assertEquals(2, parser.getIntAttribute(descriptorList.get(0), "foo", 2));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.xml.DOMParserTest");
    }
}
