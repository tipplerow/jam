
package jam.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.junit.*;
import static org.junit.Assert.*;

public class ElementListTest {
    private static final String DESC_SAMPLE_FILE = "data/test/mesh_desc_sample.xml";

    private static final Document    DOCUMENT    = DOMUtil.parse(DESC_SAMPLE_FILE);
    private static final Element     DOC_ELEMENT = DOCUMENT.getDocumentElement();
    private static final ElementList PARENT_LIST = ElementList.childrenOf(DOC_ELEMENT);
    private static final ElementList CHILD_LIST  = ElementList.childrenOf(PARENT_LIST.get(0));

    @Test public void testAssertTagNames() {
        PARENT_LIST.assertTagNames("DescriptorRecord",
                                   "DescriptorRecord",
                                   "DescriptorRecord");

        CHILD_LIST.assertTagNames("DescriptorUI",
                                  "DescriptorName",
                                  "DateCreated",
                                  "DateRevised",
                                  "DateEstablished",
                                  "AllowableQualifiersList",
                                  "HistoryNote",
                                  "OnlineNote",
                                  "PublicMeSHNote",
                                  "PreviousIndexingList",
                                  "PharmacologicalActionList",
                                  "TreeNumberList",
                                  "ConceptList");
    }

    @Test(expected = RuntimeException.class)
    public void testAssertTagNamesException() {
        PARENT_LIST.assertTagNames("DescriptorRecord", "DescriptorRecord");
    }

    @Test public void testByTagName() {
        ElementList conceptList;

        conceptList = ElementList.byTagName(PARENT_LIST.get(0), "Concept");
        conceptList.assertTagNames("Concept", "Concept");

        conceptList = ElementList.byTagName(PARENT_LIST.get(1), "Concept");
        conceptList.assertTagNames("Concept", "Concept", "Concept");

        conceptList = ElementList.byTagName(PARENT_LIST.get(2), "Concept");
        conceptList.assertTagNames("Concept");
    }

    @Test public void testChildrenOf() {
        assertEquals("D000001", CHILD_LIST.get(0).getTextContent());
        assertEquals("Calcimycin", CHILD_LIST.get(1).getTextContent());
    }

    @Test public void testGetOptional() {
        assertEquals("DateCreated", CHILD_LIST.getOptional("DateCreated").getTagName());
        assertNull(CHILD_LIST.getOptional("missing"));
    }

    @Test(expected = RuntimeException.class)
    public void testGetOptionalDuplicate() {
        PARENT_LIST.getOptional("DescriptorRecord");
    }

    @Test public void testGetRequired() {
        assertEquals("DateRevised", CHILD_LIST.getRequired("DateRevised").getTagName());
    }

    @Test(expected = RuntimeException.class)
    public void testGetRequiredMissing() {
        PARENT_LIST.getRequired("missing");
    }

    @Test(expected = RuntimeException.class)
    public void testGetRequiredDuplicate() {
        PARENT_LIST.getRequired("DescriptorRecord");
    }

    @Test public void testSize() {
        assertEquals(3, PARENT_LIST.size());
        assertEquals(13, CHILD_LIST.size());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.xml.ElementListTest");
    }
}
