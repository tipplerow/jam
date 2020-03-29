
package jam.junit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import jam.xml.DOMUtil;
import jam.xml.ElementList;

import org.junit.*;
import static org.junit.Assert.*;

public class ElementListTest {
    private static final String DESC_SAMPLE_FILE = "data/test/mesh_desc_sample.xml";

    @Test public void testCreate() {
        Document document = DOMUtil.parse(DESC_SAMPLE_FILE);

        Element docElement = document.getDocumentElement();
        assertEquals("DescriptorRecordSet", docElement.getTagName());

        ElementList descriptorList = ElementList.childrenOf(docElement);
        assertEquals(3, descriptorList.size());

        for (Element descriptorElement : descriptorList)
            assertEquals("DescriptorRecord", descriptorElement.getTagName());

        ElementList childList = ElementList.childrenOf(descriptorList.get(0), 2);
        assertEquals(2, childList.size());

        childList.assertTagNames("DescriptorUI", "DescriptorName");

        assertEquals("DescriptorUI", childList.get(0).getTagName());
        assertEquals("DescriptorName", childList.get(1).getTagName());

        assertEquals("D000001", childList.get(0).getTextContent());
        assertEquals("Calcimycin", childList.get(1).getTextContent());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.ElementListTest");
    }
}
