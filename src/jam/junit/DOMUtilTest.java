
package jam.junit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import jam.xml.DOMUtil;
import jam.xml.ElementList;

import org.junit.*;
import static org.junit.Assert.*;

public class DOMUtilTest {
    private static final String DESC_SAMPLE_FILE = "data/test/mesh_desc_sample.xml";

    private static final Document    DOCUMENT    = DOMUtil.parse(DESC_SAMPLE_FILE);
    private static final Element     DOC_ELEMENT = DOCUMENT.getDocumentElement();
    private static final ElementList PARENT_LIST = ElementList.childrenOf(DOC_ELEMENT);
    private static final ElementList CHILD_LIST  = ElementList.childrenOf(PARENT_LIST.get(0));

    @Test public void testGetAttribute() {
        Element parent = PARENT_LIST.get(0);
        Element child  = CHILD_LIST.get(0);

        assertEquals("1", DOMUtil.getAttribute(parent, "DescriptorClass"));

        assertNull(DOMUtil.getAttribute(parent, "foo"));
        assertNull(DOMUtil.getAttribute(child, "bar"));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.DOMUtilTest");
    }
}
