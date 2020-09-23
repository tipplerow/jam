
package jam.html;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jam.lang.JamException;
import jam.math.DoubleUtil;
import jam.math.IntUtil;
import jam.util.ListUtil;

/**
 * Represents an immutable HTML element in the context of its parent
 * document.
 */
public class HTMLElement {
    final Element element;

    HTMLElement(Element element) {
        this.element = element;
    }

    List<Element> findChildrenByTag(String tagName) {
        return ListUtil.filter(element.children(), child -> child.tagName().equals(tagName));
    }

    Element findOptionalChild(String tagName) {
        List<Element> children = findChildrenByTag(tagName);

        switch (children.size()) {
        case 0:
            return null;

        case 1:
            return children.get(0);

        default:
            throw JamException.runtime("Found [%d] children with tag [%s].", children.size(), tagName);
        }
    }

    Element findRequiredChild(String tagName) {
        List<Element> children = findChildrenByTag(tagName);

        switch (children.size()) {
        case 0:
            throw JamException.runtime("Missing required child: [%s].", tagName);

        case 1:
            return children.get(0);

        default:
            throw JamException.runtime("Found [%d] children with tag [%s].", children.size(), tagName);
        }
    }

    /**
     * Returns the number of children for this element.
     *
     * @return the number of children for this element.
     */
    public int countChildren() {
        return element.childrenSize();
    }

    /**
     * Returns a read-only view of the class name attributes for this
     * element.
     *
     * @return a read-only view of the class name attributes for this
     * element.
     */
    public Set<String> getClassNames() {
        return Collections.unmodifiableSet(element.classNames());
    }

    /**
     * Returns the double-precision value of this element.
     *
     * @return the double-precision value of this element.
     *
     * @throws RuntimeException unless this element contains a
     * properly formatted {@code double} value.
     */
    public double getDouble() {
        return DoubleUtil.parseDouble(getText());
    }

    /**
     * Returns the integer value of this element.
     *
     * @return the integern value of this element.
     *
     * @throws RuntimeException unless this element contains a
     * properly formatted {@code int} value.
     */
    public int getInt() {
        return IntUtil.parseInt(getText());
    }

    /**
     * Returns the tag name of this element.
     *
     * @return the tag name of this element.
     */
    public String getTagName() {
        return element.tagName();
    }

    /**
     * Returns the combined text of this element and all of its
     * children; whitespace is normalized and trimmed.
     *
     * @return the combined text of this element and all of its children,
     * with whitespace normalized and trimmed.
     */
    public String getText() {
        return element.text();
    }

    /**
     * Identifies class names for this element.
     *
     * @param className a class name in question.
     *
     * @return {@code true} iff this element has the specified class name.
     */
    public boolean hasClass(String className) {
        return element.hasClass(className);
    }

    /**
     * Finds elements that match a CSS query; matching elements may
     * include this element or any element below.
     *
     * @param cssQuery a CSS query to execute.
     *
     * @return the elements that match the CSS query.
     */
    public List<HTMLElement> select(String cssQuery) {
        return ListUtil.apply(element.select(cssQuery), element -> new HTMLElement(element));
    }

    /**
     * Finds elements with a particular tag name, attribute name, and
     * attribute value; the matching elements may include this element
     * or any element below.
     *
     * @param tagName the element tag name to match.
     *
     * @param attrName the attribute name to examine.
     *
     * @param attrValue the attribute value to match.
     *
     * @return the elements that match the specified tag name,
     * attribute name, and attribute value.
     */
    public List<HTMLElement> select(String tagName, String attrName, String attrValue) {
        return select(formatQuery(tagName, attrName, attrValue));
    }

    private static String formatQuery(String tagName, String attrName, String attrValue) {
        return String.format("%s[%s=%s]", tagName, attrName, attrValue);
    }

    /**
     * Finds a unique element that matches a CSS query; the matching
     * element may be this element or any element below.
     *
     * @param cssQuery a CSS query to execute.
     *
     * @return the unique element that matches the CSS query.
     *
     * @throws RuntimeException unless the query matches exactly one
     * element.
     */
    public HTMLElement selectUnique(String cssQuery) {
        List<HTMLElement> matches = select(cssQuery);

        if (matches.size() == 1)
            return matches.get(0);
        else
            throw JamException.runtime("Found [%d] elements matching query [%s].", matches.size(), cssQuery);
    }

    /**
     * Finds a unique element with a particular tag name, attribute
     * name, and attribute value; the matching element may be this
     * element or any element below.
     *
     * @param tagName the element tag name to match.
     *
     * @param attrName the attribute name to examine.
     *
     * @param attrValue the attribute value to match.
     *
     * @return the unique element that matches the specified tag name,
     * attribute name, and attribute value.
     *
     * @throws RuntimeException unless the query matches exactly one
     * element.
     */
    public HTMLElement selectUnique(String tagName, String attrName, String attrValue) {
        return selectUnique(formatQuery(tagName, attrName, attrValue));
    }

    /**
     * Ensures that this element has an expected class name.
     *
     * @param className the expected class name.
     *
     * @throws RuntimeException unless this element has a class name
     * that matches the input value.
     */
    public void validateClassName(String className) {
        if (!hasClass(className))
            throw JamException.runtime("Expected a [%s] element but found [%s]", className, element.className());
    }

    /**
     * Ensures that this element has an expected tag name.
     *
     * @param tagName the expected tag name.
     *
     * @throws RuntimeException unless the tag name of this element
     * matches the input value.
     */
    public void validateTagName(String tagName) {
        if (!getTagName().equals(tagName))
            throw JamException.runtime("Expected [%s] tag but found [%s].", tagName, getTagName());
    }

    @Override public String toString() {
        return element.toString();
    }
}
