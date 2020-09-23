
package jam.html;

import org.jsoup.nodes.Element;

/**
 * Represents a single entry (data item) in an HTML table row.
 */
public final class HTMLTableRowData extends HTMLElement {
    HTMLTableRowData(Element element) {
        super(element);
        validate();
    }

    private void validate() {
        validateTagName(TAG_NAME);
    }

    /**
     * The tag name for table data elements.
     */
    public static final String TAG_NAME = "td";
}
