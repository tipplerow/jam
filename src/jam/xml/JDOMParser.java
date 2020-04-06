
package jam.xml;

import java.util.List;

import org.jdom2.Element;

import jam.lang.JamBoolean;
import jam.lang.JamException;

/**
 * Provides a base class for customized JDOM parsers.
 */
public class JDOMParser {
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
    public void assertTagName(Element element, String tagName) {
        if (!element.getName().equals(tagName))
            throw JamException.runtime("Expected element [%s], but found [%s].",
                                       tagName, element.getName());
    }

    /**
     * Asserts that elements in a list have specific tags.
     *
     * @param elements the element list to validate.
     *
     * @param tagNames the expected tag names.
     *
     * @throws RuntimeException unless the list contains at least
     * {@code tagNames.length} elements and their names exactly match.
     */
    public void assertTagNames(List<Element> elements, String... tagNames) {
        if (elements.size() < tagNames.length)
            throw JamException.runtime("Expected at least [%d] elements, but found [%d].",
                                       tagNames.length, elements.size());

        for (int index = 0; index < tagNames.length; ++index)
            assertTagName(elements.get(index), tagNames[index]);
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
        String attrValue = element.getAttributeValue(attrName);

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
        String attrValue = element.getAttributeValue(attrName);

        if (attrValue != null)
            return attrValue;
        else
            throw JamException.runtime("Missing required attribute: [%s:%s].",
                                       element.getName(), attrName);
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
        String attrValue = element.getAttributeValue(attrName);

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
        String attrValue = element.getAttributeValue(attrName);

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
        String attrValue = element.getAttributeValue(attrName);

        if (attrValue != null)
            return Integer.parseInt(attrValue);
        else
            return defaultVal;
    }

    /**
     * Returns the first child element with a given name.
     *
     * @param parent the parent element to search.
     *
     * @param tagName the name of the desired element.
     *
     * @return the first child element with the given name.
     */
    public Element getRequiredChild(Element parent, String tagName) {
        Element child = parent.getChild(tagName);

        if (child != null)
            return child;
        else
            throw JamException.runtime("Missing child [%s].", tagName);
    }
}
