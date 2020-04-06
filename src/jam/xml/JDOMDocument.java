
package jam.xml;

import java.io.File;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import jam.app.JamLogger;
import jam.lang.JamException;

/**
 * Parses an XML file and encapsulates the contents in a
 * {@code JDOM Document} object.
 */
public final class JDOMDocument {
    private final File xmlFile;
    private final Document document;
    private final Element rootElement;
    private final List<Element> rootElementChildren;

    private JDOMDocument(File xmlFile) {
        this.xmlFile = xmlFile;
        this.document = parseSAX(xmlFile);
        this.rootElement = document.getRootElement();
        this.rootElementChildren = rootElement.getChildren();
    }

    private static Document parseSAX(File xmlFile) {
        SAXBuilder builder = new SAXBuilder();
        JamLogger.info("Parsing [%s]...", xmlFile);

        try {
            return builder.build(xmlFile);
        }
        catch (Exception ex) {
            throw JamException.runtime(ex);
        }
    }

    /**
     * Parses an XML file and returns the contents as a {@code JDOMDocument}.
     *
     * @param xmlFile the xmlFile to parse.
     *
     * @return a new {@code JDOMDocument} for the specified XML file.
     *
     * @throws RuntimeException unless the XML file can be successfully
     * opened and parsed.
     */
    public static JDOMDocument parse(String xmlFile) {
        return parse(new File(xmlFile));
    }

    /**
     * Parses an XML file and returns the contents as a {@code JDOMDocument}.
     *
     * @param xmlFile the xmlFile to parse.
     *
     * @return a new {@code JDOMDocument} for the specified XML file.
     *
     * @throws RuntimeException unless the XML file can be successfully
     * opened and parsed.
     */
    public static JDOMDocument parse(File xmlFile) {
        return new JDOMDocument(xmlFile);
    }

    /**
     * Returns the XML file that was parsed.
     *
     * @return the XML file that was parsed.
     */
    public File getXmlFile() {
        return xmlFile;
    }

    /**
     * Returns the parsed document.
     *
     * @return the parsed document.
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Returns the root (document) element.
     *
     * @return the root (document) element.
     */
    public Element getRootElement() {
        return rootElement;
    }

    /**
     * Returns the child elements contained in the document element.
     *
     * @return the child elements contained in the document element.
     */
    public List<Element> getRootElementChildren() {
        return rootElementChildren;
    }
}
