
package jam.xml;

import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;

import jam.app.JamLogger;
import jam.io.IOUtil;
import jam.lang.JamException;

/**
 * Provides utility methods for parsing XML files.
 */
public final class XmlUtil {
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
    public static Document parseDOM(File xmlFile) {
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
