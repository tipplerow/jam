
package jam.xml;

import java.util.List;

import org.jdom2.Element;

import jam.lang.JamBoolean;
import jam.lang.JamException;

/**
 * Decorates a bare {@code org.jdom2.Element} with utility methods.
 */
public class JDOMElement {
    /**
     * The underlying JDOM2 element.
     */
    protected Element element;

    /**
     * Decorates a {@code org.jdom2.Element} with utility methods.
     *
     * @param element the underlying JDOM2 element.
     */
    public JDOMElement(Element element) {
        this.element = element;
    }

    /**
     * Decorates a {@code org.jdom2.Element} with utility methods.
     *
     * @param element the underlying JDOM2 element.
     *
     * @param tagName the expected name of the element.
     *
     * @throws RuntimeException unless the element has the specified
     * name.
     */
    public JDOMElement(Element element, String tagName) {
        this(element);
        assertTagName(tagName);
    }

    /**
     * Asserts that this element has a specific tag.
     *
     * @param tagName the expected tag name.
     *
     * @throws RuntimeException unless this element has the specified
     * tag name.
     */
    public void assertTagName(String tagName) {
        if (!element.getName().equals(tagName))
            throw JamException.runtime("Expected element [%s], but found [%s].", tagName, element.getName());
    }

    /**
     * Finds a child of this element with a desired attribute.
     *
     * @param childName the name of the child element to examine.
     *
     * @param attrName the name of the child attribute to examine.
     *
     * @param targetValue the desired attribute value.
     *
     * @return the first child element with the specified name and
     * attribute (or {@code null} if no matching child is found).
     */
    public Element findChildElement(String childName, String attrName, String targetValue) {
        for (Element child : element.getChildren(childName)) {
            String attrValue = child.getAttributeValue(attrName);

            if (attrValue != null && attrValue.equals(targetValue))
                return child;
        }

        return null;
    }

    /**
     * Returns an optional named attribute from the attribute list
     * of this element.
     *
     * @param attrName the name of the desired attribute.
     *
     * @param defaultVal the default value to be used for missing
     * attributes.
     *
     * @return the string value of the desired attribute, if present,
     * or the default value otherwise.
     */
    public String getOptionalAttribute(String attrName, String defaultVal) {
        String attrValue = element.getAttributeValue(attrName);

        if (attrValue != null)
            return attrValue;
        else
            return defaultVal;
    }

    /**
     * Returns a required named attribute from the attribute list
     * of this element.
     *
     * @param attrName the name of the desired attribute.
     *
     * @return the string value of the desired attribute.
     *
     * @throws RuntimeException unless the desired attribute is
     * present.
     */
    public String getRequiredAttribute(String attrName) {
        String attrValue = element.getAttributeValue(attrName);

        if (attrValue != null)
            return attrValue;
        else
            throw JamException.runtime("Missing required attribute: [%s:%s].",
                                       element.getName(), attrName);
    }

    /**
     * Returns a required boolean attribute from the attribute list of
     * this element.
     *
     * @param attrName the name of the desired attribute.
     *
     * @return the boolean value of the desired attribute.
     *
     * @throws RuntimeException unless the desired attribute is
     * present and is a valid boolean value.
     */
    public boolean getBooleanAttribute(String attrName) {
        return JamBoolean.valueOf(getRequiredAttribute(attrName));
    }

    /**
     * Returns an optional boolean attribute from the attribute list
     * of this element.
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
    public boolean getBooleanAttribute(String attrName, boolean defaultVal) {
        String attrValue = element.getAttributeValue(attrName);

        if (attrValue != null)
            return JamBoolean.valueOf(attrValue);
        else
            return defaultVal;
    }

    /**
     * Returns a required double attribute from the attribute list of
     * this element.
     *
     * @param attrName the name of the desired attribute.
     *
     * @return the double value of the desired attribute.
     *
     * @throws RuntimeException unless the desired attribute is
     * present and is a valid double value.
     */
    public double getDoubleAttribute(String attrName) {
        return Double.parseDouble(getRequiredAttribute(attrName));
    }

    /**
     * Returns an optional double attribute from the attribute list
     * of this element.
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
    public double getDoubleAttribute(String attrName, double defaultVal) {
        String attrValue = element.getAttributeValue(attrName);

        if (attrValue != null)
            return Double.parseDouble(attrValue);
        else
            return defaultVal;
    }

    /**
     * Returns a required integer attribute from the attribute list of
     * this element.
     *
     * @param attrName the name of the desired attribute.
     *
     * @return the integer value of the desired attribute.
     *
     * @throws RuntimeException unless the desired attribute is
     * present and is a valid integer value.
     */
    public int getIntAttribute(String attrName) {
        return Integer.parseInt(getRequiredAttribute(attrName));
    }

    /**
     * Returns an optional integer attribute from the attribute list
     * of this element.
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
    public int getIntAttribute(String attrName, int defaultVal) {
        String attrValue = element.getAttributeValue(attrName);

        if (attrValue != null)
            return Integer.parseInt(attrValue);
        else
            return defaultVal;
    }

    /**
     * Returns all direct children of this element.
     *
     * @return all direct children of this element.
     */
    public List<Element> getChildren() {
        return element.getChildren();
    }

    /**
     * Returns the direct children of this element having a given tag
     * name.
     *
     * @param tagName the child tag name to match.
     *
     * @return the direct children of this element matching the given
     * tag name.
     */
    public List<Element> getChildren(String tagName) {
        return element.getChildren(tagName);
    }

    /**
     * Returns the normalized text from a direct child of this
     * element (which may be missing).
     *
     * @param childName the name of the text-containing child.
     *
     * @return the normalized text contained in the specified child
     * ({@code null} if the child is not present).
     */
    public String getChildText(String childName) {
        Element child = element.getChild(childName);

        if (child != null)
            return child.getTextNormalize();
        else
            return null;
    }

    /**
     * Returns the first child element with a given name.
     *
     * @param tagName the name of the desired element.
     *
     * @return the first child element with the given name
     * ({@code null} if there is no such child).
     */
    public Element getOptionalChild(String tagName) {
        return element.getChild(tagName);
    }

    /**
     * Returns the first child element with a given name.
     *
     * @param tagName the name of the desired element.
     *
     * @return the first child element with the given name.
     */
    public Element getRequiredChild(String tagName) {
        Element child = element.getChild(tagName);

        if (child != null)
            return child;
        else
            throw JamException.runtime("Missing child [%s].", tagName);
    }

    /**
     * Determines whether this element has a direct child element.
     *
     * @param childName the name of the child element in question.
     *
     * @return {@code true} iff this element has a child with the
     * given name.
     */
    public boolean hasChild(String childName) {
        return element.getChild(childName) != null;
    }
}
