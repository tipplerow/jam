
package jam.junit;

import java.io.File;
import java.util.List;

import org.jdom2.Element;

import jam.xml.JDOMDocument;

import org.junit.*;
import static org.junit.Assert.*;

public class JDOMDocumentTest {
    private static final String DESC_SAMPLE_FILE = "data/test/mesh_desc_sample.xml";

    @Test public void testParse() {
        JDOMDocument document = JDOMDocument.parse(DESC_SAMPLE_FILE);
        assertDocument(document);

        File unparsed = new File("data/test/__foo1.xml.gz");
        unparsed.deleteOnExit();

        document.unparse(unparsed);

        JDOMDocument document2 = JDOMDocument.parse(unparsed);
        assertDocument(document2);
    }

    private void assertDocument(JDOMDocument document) {
        assertEquals("DescriptorRecordSet", document.getRootElement().getName());
        
        List<Element> descriptorList = document.getRootElementChildren();
        assertEquals(3, descriptorList.size());

        for (Element descriptorElement : descriptorList)
            assertEquals("DescriptorRecord", descriptorElement.getName());

        List<Element> descriptorChildren = descriptorList.get(0).getChildren();
        assertEquals(13, descriptorChildren.size());
        assertEquals("DescriptorUI", descriptorChildren.get(0).getName());
        assertEquals("ConceptList", descriptorChildren.get(12).getName());
    }

    @Test public void testRemoveChild() {
        JDOMDocument document = JDOMDocument.parse(DESC_SAMPLE_FILE);
        assertDocument(document);

        List<Element> descriptorList = document.getRootElementChildren();

        assertEquals(3, descriptorList.size());
        assertEquals("D000001", descriptorList.get(0).getChildren().get(0).getTextNormalize());
        assertEquals("D000002", descriptorList.get(1).getChildren().get(0).getTextNormalize());
        assertEquals("D000003", descriptorList.get(2).getChildren().get(0).getTextNormalize());

        document.getRootElement().removeContent(descriptorList.get(0));

        descriptorList = document.getRootElementChildren();

        assertEquals(2, descriptorList.size());
        assertEquals("D000002", descriptorList.get(0).getChildren().get(0).getTextNormalize());
        assertEquals("D000003", descriptorList.get(1).getChildren().get(0).getTextNormalize());

        File unparsed = new File("data/test/__foo2.xml");
        unparsed.deleteOnExit();

        document.unparse(unparsed);

        JDOMDocument document2 = JDOMDocument.parse(unparsed);

        descriptorList = document2.getRootElementChildren();

        assertEquals(2, descriptorList.size());
        assertEquals("D000002", descriptorList.get(0).getChildren().get(0).getTextNormalize());
        assertEquals("D000003", descriptorList.get(1).getChildren().get(0).getTextNormalize());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.JDOMDocumentTest");
    }
}
