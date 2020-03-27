
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
     * Extracts the first {@code size} elements from a node list.
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

    @Override public Element get(int index) {
        return elements.get(index);
    }

    @Override public int size() {
        return elements.size();
    }
}
