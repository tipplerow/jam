
package jam.xml;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.StAXStreamBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import jam.app.JamLogger;
import jam.io.IOUtil;
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
        //this.document = parseStax(xmlFile);
        this.rootElement = document.getRootElement();
        this.rootElementChildren = rootElement.getChildren();
    }

    private static Document parseSAX(File xmlFile) {
        JamLogger.info("Parsing [%s]...", xmlFile);

        try (InputStream stream = IOUtil.openInputStream(xmlFile)) {
            SAXBuilder builder = new SAXBuilder();
            builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            return builder.build(stream);
        }
        catch (Exception ex) {
            throw JamException.runtime(ex);
        }
    }

    private static Document parseStax(File xmlFile) {
        JamLogger.info("Parsing [%s]...", xmlFile);

        try (InputStream stream = IOUtil.openInputStream(xmlFile)) {
            XMLInputFactory   factory = XMLInputFactory.newFactory();
            XMLStreamReader   reader  = factory.createXMLStreamReader(stream);
            StAXStreamBuilder builder = new StAXStreamBuilder();

            return builder.build(reader);
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

    /**
     * Writes this document to an XML file with pretty formatting.
     *
     * @param xmlFile the XML file to write.
     *
     * @throws RuntimeException if any I/O errors occur.
     */
    public void unparse(String xmlFile) {
        unparse(new File(xmlFile));
    }

    /**
     * Writes this document to an XML file with pretty formatting.
     *
     * @param xmlFile the XML file to write.
     *
     * @throws RuntimeException if any I/O errors occur.
     */
    public void unparse(File xmlFile) {
        unparse(xmlFile, Format.getPrettyFormat());
    }

    /**
     * Writes this document to an XML file with custom formatting.
     *
     * @param xmlFile the XML file to write.
     *
     * @param format the XML format to use.
     *
     * @throws RuntimeException if any I/O errors occur.
     */
    public void unparse(File xmlFile, Format format) {
        unparse(xmlFile, new XMLOutputter(format));
    }

    private void unparse(File xmlFile, XMLOutputter outputter) {
        JamLogger.info("Unparsing document into [%s]...", xmlFile);

        try (OutputStream stream = IOUtil.openOutputStream(xmlFile)) {
            outputter.output(document, stream);
        }
        catch (Exception ex) {
            throw JamException.runtime(ex);
        }
    }
}
