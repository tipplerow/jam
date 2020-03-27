
package jam.xml;

import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import jam.app.JamLogger;
import jam.io.IOUtil;
import jam.lang.JamException;

/**
 * Provides utility methods for parsing XML files.
 */
public final class DOMUtil {
    /**
     * Identifies element nodes.
     *
     * @param node a node to examine.
     *
     * @return {@code true} iff the input node is a DOM {@code Element}.
     */
    public static boolean isElement(Node node) {
        return node.getNodeType() == Node.ELEMENT_NODE;
    }

    /**
     * Identifies empty text nodes.
     *
     * @param node a node to examine.
     *
     * @return {@code true} iff the input node is a DOM {@code Text}
     * node containing only white space characters.
     */
    public static boolean isEmptyText(Node node) {
        return node.getNodeType() == Node.TEXT_NODE && node.getNodeValue().strip().isEmpty();
    }

    /**
     * Parses an XML file and returns the {@code DOM} document
     * contained within.
     *
     * @param xmlFile the XML file to parse.
     *
     * @return the {@code DOM} document contained in the specified
     * file.
     *
     * @throws RuntimeException if any I/O or parsing errors occur.
     */
    public static Document parse(String xmlFile) {
        return parse(new File(xmlFile));
    }

    /**
     * Parses an XML file and returns the {@code DOM} document
     * contained within.
     *
     * @param xmlFile the XML file to parse.
     *
     * @return the {@code DOM} document contained in the specified
     * file.
     *
     * @throws RuntimeException if any I/O or parsing errors occur.
     */
    public static Document parse(File xmlFile) {
        JamLogger.info("Parsing [%s]...", xmlFile);

        try (InputStream stream = IOUtil.openStream(xmlFile)) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            return builder.parse(stream);
        }
        catch (Exception ex) {
            throw JamException.runtime(ex);
        }
    }
}
