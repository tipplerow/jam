
package jam.html;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jsoup.nodes.Element;

/**
 * Represents the body of a table in an HTML document.
 */
public final class HTMLTableBody extends HTMLElement {
    private final List<HTMLTableRow> rows;

    HTMLTableBody(Element element) {
        super(element);

        this.rows= extractRows();
        validate();
    }

    private List<HTMLTableRow> extractRows() {
        List<Element> children = findChildrenByTag(HTMLTableRow.TAG_NAME);
        List<HTMLTableRow> rowList = new ArrayList<HTMLTableRow>(children.size());

        for (Element child : children)
            rowList.add(new HTMLTableRow(child));

        return Collections.unmodifiableList(rowList);
    }

    private void validate() {
        validateTagName(TAG_NAME);
    }

    /**
     * The tag name for table body elements.
     */
    public static final String TAG_NAME = "tbody";

    /**
     * Returns the number of rows in this table body.
     *
     * @return the number of rows in this table body.
     */
    public int countRows() {
        return rows.size();
    }

    /**
     * Returns the row at a particular position in this table body.
     *
     * @param index the zero-offset index of the desired row.
     *
     * @return the row at the specified position in this table body.
     *
     * @throws RuntimeException unless the row index is valid.
     */
    public HTMLTableRow getRow(int index) {
        return rows.get(index);
    }

    /**
     * Returns a read-only view of the rows in this table body.
     *
     * @return a read-only view of the rows in this table body.
     */
    public List<HTMLTableRow> viewRows() {
        return rows;
    }
}
