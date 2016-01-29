
package simple.server.core.config;


import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import simple.server.core.rule.defaultruleset.DefaultItem;

/**
 * Load and configure items via an XML configuration file.
 */
public class ItemGroupsXMLLoader extends DefaultHandler {

    private static final Logger logger = Log4J.getLogger(ItemGroupsXMLLoader.class);
    /** The main item configuration file. */
    protected URI uri;

    /**
     * Create an xml based loader of item groups.
     *
     * @param uri
     *            The location of the configuration file.
     */
    public ItemGroupsXMLLoader(URI uri) {
        this.uri = uri;
    }

    /**
     * Load items.
     *
     * @return list of items
     * @throws SAXException
     *             If a SAX error occurred.
     * @throws IOException
     *             If an I/O error occurred.
     */
    public List<DefaultItem> load() throws SAXException, IOException {
        GroupsXMLLoader groupsLoader = new GroupsXMLLoader(uri);
        List<URI> groups = groupsLoader.load();

        ItemsXMLLoader loader = new ItemsXMLLoader();
        List<DefaultItem> list = new LinkedList<DefaultItem>();
        for (URI groupUri : groups) {
            logger.debug("Loading item group [" + groupUri + "]");
            list.addAll(loader.load(groupUri));
        }

        return list;
    }
}