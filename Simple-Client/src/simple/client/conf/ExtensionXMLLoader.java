package simple.client.conf;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import simple.client.extension.SimpleClientExtension;
//TODO replace with Lookup
/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 * Loads client extensions based on xml files
 */
public class ExtensionXMLLoader extends DefaultHandler {

    /** the logger instance. */
    private static final Logger logger = Log4J.getLogger(ExtensionXMLLoader.class);
    private List<SimpleClientExtension> list;
    private String name,  clazz;

    public List<SimpleClientExtension> load(URI uri) throws SAXException {
        list = new LinkedList<SimpleClientExtension>();
        // Use the default (non-validating) parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            // Parse the input
            SAXParser saxParser = factory.newSAXParser();

            InputStream is = getClass().getResourceAsStream(uri.getPath());

            if (is == null) {
                throw new FileNotFoundException("cannot find resource '" + uri + "' in classpath");
            }
            saxParser.parse(is, this);
        } catch (ParserConfigurationException t) {
            logger.error(t);
        } catch (IOException e) {
            logger.error(e);
            throw new SAXException(e);
        }
        return list;
    }

    @Override
    public void startDocument() {
        // do nothing
    }

    @Override
    public void endDocument() {
        // do nothing
    }

    @Override
    public void startElement(String namespaceURI, String lName, String qName, Attributes attrs) {
        if (qName.equals("extension")) {
            try {
                name = attrs.getValue("name");
                clazz = attrs.getValue("class");
                if (clazz != null) {
                    list.add(SimpleClientExtension.getInstance(clazz));
                    list.get(list.size()-1).init();
                    logger.info("Successfully loaded "+ name +" extension!");
                }
            } catch (Exception e) {
                logger.error("Error loading client extension. " + 
                        (name == null ? "" : "(" + name + ")")+
                        (clazz == null ? "" : "(" + clazz + ")"), e);
            }
        }
    }

    @Override
    public void endElement(String namespaceURI, String sName, String qName) {
    }
}
