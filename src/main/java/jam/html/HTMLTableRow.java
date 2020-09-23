
package jam.html;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Represents a single data row of an HTML table.
 */
public final class HTMLTableRow extends HTMLElement {
    private final List<HTMLTableRowData> rowData;

    HTMLTableRow(Element element) {
        super(element);
        this.rowData = extractRowData();
        validate();
    }

    private List<HTMLTableRowData> extractRowData() {
        List<Element> children = findChildrenByTag(HTMLTableRowData.TAG_NAME);
        List<HTMLTableRowData> dataList = new ArrayList<HTMLTableRowData>(children.size());

        for (Element child : children)
            dataList.add(new HTMLTableRowData(child));

        return Collections.unmodifiableList(dataList);
    }

    private void validate() {
        validateTagName(TAG_NAME);
    }

    /**
     * The tag name for table elements.
     */
    public static final String TAG_NAME = "tr";

    /**
     * Returns the number of data elements in this row.
     *
     * @return the number of data elements in this row.
     */
    public int countRowData() {
        return rowData.size();
    }

    /**
     * Returns a data element at a particular position in this row.
     *
     * @param index the zero-offset index of the desired data element.
     *
     * @return the data element at the specified position in this row.
     *
     * @throws RuntimeException unless the element index is valid.
     */
    public HTMLTableRowData getRowData(int index) {
        return rowData.get(index);
    }

    /**
     * Returns the data elements in this row.
     *
     * @return an unmodifiable list containing the data elements in
     * this row.
     */
    public List<HTMLTableRowData> viewRowData() {
        return rowData;
    }
}
