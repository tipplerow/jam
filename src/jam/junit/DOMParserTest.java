
package jam.junit;

import org.w3c.dom.Element;

import jam.xml.DOMParser;
import jam.xml.ElementList;

import org.junit.*;
import static org.junit.Assert.*;

final class MyParser extends DOMParser {
    public MyParser(String xmlFile) {
        super(xmlFile);
    }
}

public class DOMParserTest {
    private static final String DESC_SAMPLE_FILE = "data/test/mesh_desc_sample.xml";

    @Test public void testParse() {
        MyParser parser = new MyParser(DESC_SAMPLE_FILE);

        parser.assertTagName(parser.getDocumentElement(), "DescriptorRecordSet");

        ElementList descriptorList = parser.getDocElementChildren();
        assertEquals(3, descriptorList.size());

        for (Element descriptorElement : descriptorList)
            parser.assertTagName(descriptorElement, "DescriptorRecord");

        assertEquals("1", parser.getRequiredAttribute(descriptorList.get(0), "DescriptorClass"));
        assertEquals("bar", parser.getOptionalAttribute(descriptorList.get(0), "foo", "bar"));

        assertEquals(1, parser.getIntAttribute(descriptorList.get(0), "DescriptorClass"));
        assertEquals(2, parser.getIntAttribute(descriptorList.get(0), "foo", 2));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.DOMParserTest");
    }
}
