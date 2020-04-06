
package jam.junit;

import java.util.List;

import org.jdom2.Element;

import jam.xml.JDOMDocument;
import jam.xml.JDOMParser;

import org.junit.*;
import static org.junit.Assert.*;

public class JDOMParserTest {
    private static final String DESC_SAMPLE_FILE = "data/test/mesh_desc_sample.xml";

    @Test public void testParser() {
        JDOMParser parser = new JDOMParser();
        JDOMDocument document = JDOMDocument.parse(DESC_SAMPLE_FILE);

        parser.assertTagName(document.getRootElement(), "DescriptorRecordSet");

        List<Element> descriptorList = document.getRootElementChildren();
        assertEquals(3, descriptorList.size());

        parser.assertTagNames(descriptorList, "DescriptorRecord", "DescriptorRecord", "DescriptorRecord");

        Element descriptorElement =
            parser.getRequiredChild(document.getRootElement(), "DescriptorRecord");

        assertEquals("1", parser.getRequiredAttribute(descriptorElement, "DescriptorClass"));
        assertEquals("bar", parser.getOptionalAttribute(descriptorElement, "foo", "bar"));

        assertEquals(1, parser.getIntAttribute(descriptorList.get(0), "DescriptorClass"));
        assertEquals(2, parser.getIntAttribute(descriptorList.get(0), "foo", 2));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.JDOMParserTest");
    }
}
