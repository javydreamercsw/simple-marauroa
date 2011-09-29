/*
 * $Rev$
 * $LastChangedDate$
 * $LastChangedBy$
 */
package simple.server.core.config;



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

/**
 * Loads a list of files from an xml file.
 */
class GroupsXMLLoader extends DefaultHandler {

    private static final Logger logger = Log4J.getLogger(GroupsXMLLoader.class);
    /** The main configuration file. */
    protected URI uri;
    /**
     * A list of files.
     */
    protected LinkedList<URI> groups;

    /**
     * Create an xml based loader of groups.
     *
     * @param uri
     *            The location of the configuration file.
     */
    public GroupsXMLLoader(URI uri) {
        this.uri = uri;
    }

    /**
     * Loads and @return the list.
     *
     * @return list of group entries
     * @throws SAXException
     *             If a SAX error occurred.
     * @throws IOException
     *             If an I/O error occurred.
     * @throws FileNotFoundException
     *             If the resource was not found.
     */
    public List<URI> load() throws SAXException, IOException {
        InputStream in = getClass().getResourceAsStream(uri.getPath());

        if (in == null) {
            throw new FileNotFoundException("Cannot find resource: " + uri.getPath());
        }

        try {
            return load(in);
        } finally {
            in.close();
        }
    }

    /**
     * Load and @return the list of files.
     *
     * @param in
     *            The config file stream.
     * @return list of group entries
     *
     * @throws SAXException
     *             If a SAX error occurred.
     * @throws IOException
     *             If an I/O error occurred.
     */
    protected List<URI> load(InputStream in) throws SAXException, IOException {
        SAXParser saxParser;

        // Use the default (non-validating) parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            saxParser = factory.newSAXParser();
        } catch (ParserConfigurationException ex) {
            throw new SAXException(ex);
        }

        // Parse the XML
        groups = new LinkedList<URI>();
        saxParser.parse(in, this);
        return groups;
    }

    /**
     * Is called when a XML-element is started.
     * <p>
     * The outer groups tag is ignored.
     * <p>
     * Any exception occurring while examining a group value is ignored and written to the loggers error channel
     *
     */
    @Override
    public void startElement(String namespaceURI, String lName, String qName, Attributes attrs) {
        if (qName.equals("group")) {
            String uriValue = attrs.getValue("uri");
            if (uriValue == null) {
                logger.warn("group without 'uri'");
            } else {
                try {
                    groups.add(uri.resolve(uriValue));
                } catch (IllegalArgumentException ex) {
                    logger.error("Invalid group reference: " + uriValue + " [" + ex.getMessage() + "]");
                }

            }
        } else if (!qName.equals("groups")) {
            logger.warn("Unknown XML element: " + qName);
        }
    }
}
