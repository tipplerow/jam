
package jam.xml;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Represents a sequential list of DOM {@code Element} nodes.
 */
public final class ElementList extends AbstractList<Element> {
    private final List<Element> elements;

    private ElementList(List<Element> elements) {
        this.elements = elements;
    }

    /**
     * Extracts the child elements (direct descendents) of an XML
     * element.
     *
     * @param element the XML element of interest.
     *
     * @return a new list containing all child elements of the input
     * element.
     */
    public static ElementList childrenOf(Element element) {
        return create(element.getChildNodes());
    }

    /**
     * Extracts at most {@code maxSize} child elements (direct
     * descendents) of an XML element.
     *
     * @param element the XML element of interest.
     *
     * @param maxSize the maximum number of children to extract.
     *
     * @return a new list containing the first {@code maxSize} child
     * elements of the input element (or all elements if there are
     * fewer than {@code maxSize} children present).
     */
    public static ElementList childrenOf(Element element, int maxSize) {
        return create(element.getChildNodes(), maxSize);
    }

    /**
     * Extracts all elements from a node list.
     *
     * @param nodeList the node list to process.
     *
     * @return a new list containing all DOM {@code Element} nodes in
     * the input list.
     */
    public static ElementList create(NodeList nodeList) {
        return create(nodeList, nodeList.getLength());
    }

    /**
     * Extracts at most {@code maxSize} elements from a node list.
     *
     * @param nodeList the node list to process.
     *
     * @param maxSize the maximum number of elements to extract.
     *
     * @return a new list containing the first {@code maxSize} DOM
     * {@code Element} nodes in the input list (or all elements
     * if there are fewer than {@code maxSize} elements present).
     */
    public static ElementList create(NodeList nodeList, int maxSize) {
        if (maxSize < 0)
            throw new IllegalArgumentException("Maximum size must be non-negative.");

        int nodeIndex = 0;
        List<Element> elementList = new ArrayList<Element>(maxSize);

        while (nodeIndex < nodeList.getLength() && elementList.size() < maxSize) {
            Node node = nodeList.item(nodeIndex);

            if (DOMUtil.isElement(node))
                elementList.add((Element) node);

            ++nodeIndex;
        }

        return new ElementList(elementList);
    }

    /**
     * Validates the tag names of the elements in this list.
     *
     * @param tagNames the expected tag names for every element in
     * this list.
     *
     * @throws RuntimeException unless the tag names of the elements
     * in this list match the input sequence exactly.
     */
    public void assertTagNames(String... tagNames) {
        for (int index = 0; index < tagNames.length; ++index)
            DOMParser.assertTagName(get(index), tagNames[index]);
    }

    @Override public Element get(int index) {
        return elements.get(index);
    }

    @Override public int size() {
        return elements.size();
    }
}
