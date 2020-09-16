
package jam.xml;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Parses an XML file and encapsulates the contents in a
 * {@code DOM Document} object.
 */
public final class DOMDocument {
    private final File xmlFile;
    private final Document document;
    private final Element docElement;
    private final ElementList docElementChildren;

    private DOMDocument(File xmlFile) {
        this.xmlFile = xmlFile;
        this.document = DOMUtil.parse(xmlFile);
        this.docElement = document.getDocumentElement();
        this.docElementChildren = ElementList.create(docElement.getChildNodes());
    }

    /**
     * Parses an XML file and returns the contents as a {@code DOMDocument}.
     *
     * @param xmlFile the xmlFile to parse.
     *
     * @return a new {@code DOMDocument} for the specified XML file.
     *
     * @throws RuntimeException unless the XML file can be successfully
     * opened and parsed.
     */
    public static DOMDocument parse(String xmlFile) {
        return parse(new File(xmlFile));
    }

    /**
     * Parses an XML file and returns the contents as a {@code DOMDocument}.
     *
     * @param xmlFile the xmlFile to parse.
     *
     * @return a new {@code DOMDocument} for the specified XML file.
     *
     * @throws RuntimeException unless the XML file can be successfully
     * opened and parsed.
     */
    public static DOMDocument parse(File xmlFile) {
        return new DOMDocument(xmlFile);
    }

    /**
     * Returns the XML file that was parsed.
     *
     * @return the XML file that was parsed.
     */
    public File getXmlFile() {
        return xmlFile;
    }

    /**
     * Returns the parsed document.
     *
     * @return the parsed document.
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Returns the document element.
     *
     * @return the document element.
     */
    public Element getDocumentElement() {
        return docElement;
    }

    /**
     * Returns the child elements contained in the document element.
     *
     * @return the child elements contained in the document element.
     */
    public ElementList getDocElementChildren() {
        return docElementChildren;
    }
}
