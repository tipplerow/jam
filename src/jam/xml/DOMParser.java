
package jam.xml;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import jam.lang.JamException;

/**
 * Provides a base class for customized DOM parsers.
 */
public abstract class DOMParser {
    /**
     * The XML file to be parsed.
     */
    protected final File xmlFile;

    /**
     * The parsed XML document.
     */
    protected final Document document;

    /**
     * The root document element.
     */
    protected final Element docElement;

    /**
     * All child elements of the document element.
     */
    protected final ElementList docElementChildren;

    /**
     * Creates a new DOM parser for a given XML file.
     *
     * <p>When this constructor is called, the XML file is parsed and the
     * {@code document}, {@code docElement} and {@code docElementChildren}
     * attributes are populated.
     *
     * @param xmlFile the xmlFile to parse.
     *
     * @throws RuntimeException unless the XML file can be successfully
     * opened and parsed.
     */
    protected DOMParser(String xmlFile) {
        this(new File(xmlFile));
    }

    /**
     * Creates a new DOM parser for a given XML file.
     *
     * <p>When this constructor is called, the XML file is parsed and the
     * {@code document}, {@code docElement} and {@code docElementChildren}
     * attributes are populated.
     *
     * @param xmlFile the xmlFile to parse.
     *
     * @throws RuntimeException unless the XML file can be successfully
     * opened and parsed.
     */
    protected DOMParser(File xmlFile) {
        this.xmlFile = xmlFile;
        this.document = DOMUtil.parse(xmlFile);
        this.docElement = document.getDocumentElement();
        this.docElementChildren = ElementList.create(docElement.getChildNodes());
    }

    /**
     * Asserts that a given element has a specific tag.
     *
     * @param element the element to validate.
     *
     * @param tagName the expected tag name.
     *
     * @throws RuntimeException unless the specified element has the
     * specified tag name.
     */
    public static void assertElement(Element element, String tagName) {
        if (!element.getTagName().equals(tagName))
            throw JamException.runtime("Found [%s] element, expected [%s].", element.getTagName(), tagName);
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
