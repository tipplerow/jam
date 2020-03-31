
package jam.xml;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import jam.lang.JamBoolean;
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
    public static void assertTagName(Element element, String tagName) {
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

    /**
     * Returns an optional named attribute from the attribute list
     * of an element.
     *
     * @param element the element to examine.
     *
     * @param attrName the name of the desired attribute.
     *
     * @param defaultVal the default value to be used for missing
     * attributes.
     *
     * @return the string value of the desired attribute, if present,
     * or the default value otherwise.
     */
    public String getOptionalAttribute(Element element, String attrName, String defaultVal) {
        String attrValue = DOMUtil.getAttribute(element, attrName);

        if (attrValue != null)
            return attrValue;
        else
            return defaultVal;
    }

    /**
     * Returns a required named attribute from the attribute list
     * of an element.
     *
     * @param element the element to examine.
     *
     * @param attrName the name of the desired attribute.
     *
     * @return the string value of the desired attribute.
     *
     * @throws RuntimeException unless the desired attribute is
     * present.
     */
    public String getRequiredAttribute(Element element, String attrName) {
        String attrValue = DOMUtil.getAttribute(element, attrName);

        if (attrValue != null)
            return attrValue;
        else
            throw JamException.runtime("Missing required attribute: [%s:%s].",
                                       element.getTagName(), attrName);
    }

    /**
     * Returns a required boolean attribute from the attribute list of
     * an element.
     *
     * @param element the element to examine.
     *
     * @param attrName the name of the desired attribute.
     *
     * @return the boolean value of the desired attribute.
     *
     * @throws RuntimeException unless the desired attribute is
     * present and is a valid boolean value.
     */
    public boolean getBooleanAttribute(Element element, String attrName) {
        return JamBoolean.valueOf(getRequiredAttribute(element, attrName));
    }

    /**
     * Returns an optional boolean attribute from the attribute list
     * of an element.
     *
     * @param element the element to examine.
     *
     * @param attrName the name of the desired attribute.
     *
     * @param defaultVal the value to be used for missing attributes.
     *
     * @return the boolean value of the desired attribute, or the
     * default value if the attribute is not present.
     *
     * @throws RuntimeException if the desired attribute is present
     * but is not a valid boolean value.
     */
    public boolean getBooleanAttribute(Element element, String attrName, boolean defaultVal) {
        String attrValue = DOMUtil.getAttribute(element, attrName);

        if (attrValue != null)
            return JamBoolean.valueOf(attrValue);
        else
            return defaultVal;
    }

    /**
     * Returns a required double attribute from the attribute list of
     * an element.
     *
     * @param element the element to examine.
     *
     * @param attrName the name of the desired attribute.
     *
     * @return the double value of the desired attribute.
     *
     * @throws RuntimeException unless the desired attribute is
     * present and is a valid double value.
     */
    public double getDoubleAttribute(Element element, String attrName) {
        return Double.parseDouble(getRequiredAttribute(element, attrName));
    }

    /**
     * Returns an optional double attribute from the attribute list
     * of an element.
     *
     * @param element the element to examine.
     *
     * @param attrName the name of the desired attribute.
     *
     * @param defaultVal the value to be used for missing attributes.
     *
     * @return the double value of the desired attribute, or the
     * default value if the attribute is not present.
     *
     * @throws RuntimeException if the desired attribute is present
     * but is not a valid double value.
     */
    public double getDoubleAttribute(Element element, String attrName, double defaultVal) {
        String attrValue = DOMUtil.getAttribute(element, attrName);

        if (attrValue != null)
            return Double.parseDouble(attrValue);
        else
            return defaultVal;
    }

    /**
     * Returns a required integer attribute from the attribute list of
     * an element.
     *
     * @param element the element to examine.
     *
     * @param attrName the name of the desired attribute.
     *
     * @return the integer value of the desired attribute.
     *
     * @throws RuntimeException unless the desired attribute is
     * present and is a valid integer value.
     */
    public int getIntAttribute(Element element, String attrName) {
        return Integer.parseInt(getRequiredAttribute(element, attrName));
    }

    /**
     * Returns an optional integer attribute from the attribute list
     * of an element.
     *
     * @param element the element to examine.
     *
     * @param attrName the name of the desired attribute.
     *
     * @param defaultVal the value to be used for missing attributes.
     *
     * @return the integer value of the desired attribute, or the
     * default value if the attribute is not present.
     *
     * @throws RuntimeException if the desired attribute is present
     * but is not a valid integer value.
     */
    public int getIntAttribute(Element element, String attrName, int defaultVal) {
        String attrValue = DOMUtil.getAttribute(element, attrName);

        if (attrValue != null)
            return Integer.parseInt(attrValue);
        else
            return defaultVal;
    }
}
