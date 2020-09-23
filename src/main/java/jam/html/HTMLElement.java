
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
