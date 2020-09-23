
package jam.html;

import java.util.List;

import org.jsoup.nodes.Element;

/**
 * Represents a table in an HTML document.
 */
public final class HTMLTable extends HTMLElement {
    //private final HTMLTableRow head;
    private final HTMLTableBody body;

    HTMLTable(Element element) {
        super(element);

        //this.head = extractHead();
        this.body = extractBody();

        validate();
    }

    private HTMLTableBody extractBody() {
        Element bodyElement = findOptionalChild(HTMLTableBody.TAG_NAME);

        if (bodyElement != null)
            return new HTMLTableBody(bodyElement);
        else
            return null;
    }

    private void validate() {
        validateTagName(TAG_NAME);
    }

    /**
     * The tag name for table elements.
     */
    public static final String TAG_NAME = "table";

    /**
     * Returns the number of rows in the body of this table.
     *
     * @return the number of rows in the body of this table.
     */
    public int countRows() {
        return body != null ? body.countRows() : 0;
    }

    /**
     * Returns the row at a particular position in this table.
     *
     * @param index the zero-offset index of the desired row.
     *
     * @return the row at the specified position in this table.
     *
     * @throws RuntimeException unless the row index is valid.
     */
    public HTMLTableRow getRow(int index) {
        return body.getRow(index);
    }

    /**
     * Returns a read-only view of the rows in the body of this table.
     *
     * @return a read-only view of the rows in the body of this table.
     */
    public List<HTMLTableRow> viewRows() {
        return body != null ? body.viewRows() : List.of();
    }
}
