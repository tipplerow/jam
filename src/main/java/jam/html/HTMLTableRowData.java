
package jam.html;

import org.jsoup.nodes.Element;

import jam.math.DoubleUtil;
import jam.math.IntUtil;

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
}
