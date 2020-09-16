
package jam.xml;

import java.util.List;

import org.jdom2.Element;

import org.junit.*;
import static org.junit.Assert.*;

public class JDOMParserTest {
    private static final String DESC_SAMPLE_FILE = "data/test/mesh_desc_sample.xml";

    private static final JDOMDocument document = JDOMDocument.parse(DESC_SAMPLE_FILE);
    private static final JDOMParser parser = new JDOMParser();
    private static final List<Element> descriptorList = document.getRootElementChildren();

    @Test public void testFindChildElement() {
        Element descriptorElement = descriptorList.get(0);
        Element conceptListElement = descriptorElement.getChild("ConceptList");

        Element preferredConcept =
            parser.findChildElement(conceptListElement, "Concept", "PreferredConceptYN", "Y");

        Element secondaryConcept =
            parser.findChildElement(conceptListElement, "Concept", "PreferredConceptYN", "N");

        assertEquals("M0000001", parser.getChildText(preferredConcept, "ConceptUI"));
        assertEquals("M0353609", parser.getChildText(secondaryConcept, "ConceptUI"));

        assertNull(parser.findChildElement(conceptListElement, "Concept", "xyz", "Y"));
        assertNull(parser.findChildElement(conceptListElement, "Concept", "PreferredConceptYN", "xyz"));
    }

    @Test public void testGetChildText() {
        Element descriptorElement = descriptorList.get(0);
        Element dateCreatedElement = descriptorElement.getChild("DateCreated");

        assertEquals("", parser.getChildText(dateCreatedElement, "NoChild"));
        assertEquals("1974", parser.getChildText(dateCreatedElement, "Year"));
    }

    @Test public void testGetElementText() {
        Element descriptorElement = descriptorList.get(0);
        Element descriptorUIElement = descriptorElement.getChild("DescriptorUI");

        assertEquals("", parser.getElementText(descriptorElement));
        assertEquals("D000001", parser.getElementText(descriptorUIElement));
    }

    @Test public void testHasChild() {
        Element descriptorElement = descriptorList.get(0);

        assertTrue(parser.hasChild(descriptorElement, "DescriptorUI"));
        assertFalse(parser.hasChild(descriptorElement, "String"));
    }

    @Test public void testParser() {
        parser.assertTagName(document.getRootElement(), "DescriptorRecordSet");

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
        org.junit.runner.JUnitCore.main("jam.xml.JDOMParserTest");
    }
}
