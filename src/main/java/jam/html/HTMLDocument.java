
package jam.html;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jam.lang.JamException;
import jam.util.ListUtil;

/**
 * Represents a complete HTML document.
 */
public final class HTMLDocument extends HTMLElement {
    private final Document document;

    private HTMLDocument(Document document) {
        super(document);
        this.document = document;
    }

    /**
     * Creates a new HTML document by parsing an HTML file.
     *
     * @param file a file containing HTML text.
     *
     * @return the HTML document contained in the specified file.
     *
     * @throws RuntimeException if any errors occur.
     */
    public static HTMLDocument parse(File file) {
        try {
            return new HTMLDocument(Jsoup.parse(file, null));
        }
        catch (Exception ex) {
            throw JamException.runtime(ex);
        }
    }

    /**
     * Creates a new HTML document by parsing HTML text.
     *
     * @param string a string containing HTML text.
     *
     * @return the HTML document encoded in the input text.
     *
     * @throws RuntimeException if any errors occur.
     */
    public static HTMLDocument parse(String string) {
        return new HTMLDocument(Jsoup.parse(string));
    }

    /**
     * Creates a new HTML document by fetching a URL and parsing the
     * HTML text.
     *
     * @param url the URL of the page to parse.
     *
     * @param timeoutMillis the timeout in milliseconds.
     *
     * @return the HTML document retrieved from the specified URL.
     *
     * @throws RuntimeException if any errors occur.
     */
    public static HTMLDocument parse(URL url, int timeoutMillis) {
        try {
            return new HTMLDocument(Jsoup.parse(url, timeoutMillis));
        }
        catch (Exception ex) {
            throw JamException.runtime(ex);
        }
    }

    /**
     * Finds the single table with a given class name.
     *
     * @param className the desired table class name.
     *
     * @return the single table with the specified class name.
     *
     * @throws RuntimeException unless the document contains exactly
     * one table with the specified class name.
     */
    public HTMLTable findTable(String className) {
        List<HTMLTable> tables = findTables(className);

        if (tables.size() == 1)
            return tables.get(0);
        else
            throw JamException.runtime("Found [%d] tables with class name [%s].", tables.size(), className);
    }

    /**
     * Finds all tables in this document.
     *
     * @return a list of all tables in this document.
     */
    public List<HTMLTable> findTables() {
        return ListUtil.apply(document.select("table"), element -> new HTMLTable(element));
    }

    /**
     * Finds all tables with a given class name.
     *
     * @param className the desired table class name.
     *
     * @return a list of all tables with the specified class name.
     */
    public List<HTMLTable> findTables(String className) {
        return ListUtil.filter(findTables(), table -> table.hasClass(className));
    }
}
