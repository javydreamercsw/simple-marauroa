package simple.server.core.config;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import simple.server.core.rule.defaultruleset.DefaultItem;

/**
 * Load and configure items via an XML configuration file.
 */
public class ItemGroupsXMLLoader extends DefaultHandler {

    private static final Logger LOG
            = Logger.getLogger(ItemGroupsXMLLoader.class.getSimpleName());
    /**
     * The main item configuration file.
     */
    protected URI uri;

    /**
     * Create an xml based loader of item groups.
     *
     * @param uri The location of the configuration file.
     */
    public ItemGroupsXMLLoader(URI uri) {
        this.uri = uri;
    }

    /**
     * Load items.
     *
     * @return list of items
     * @throws SAXException If a SAX error occurred.
     * @throws IOException If an I/O error occurred.
     */
    public List<DefaultItem> load() throws SAXException, IOException {
        GroupsXMLLoader groupsLoader = new GroupsXMLLoader(uri);
        List<URI> groups = groupsLoader.load();

        ItemsXMLLoader loader = new ItemsXMLLoader();
        List<DefaultItem> list = new LinkedList<>();
        for (URI groupUri : groups) {
            LOG.log(Level.FINE, "Loading item group [{0}]", groupUri);
            list.addAll(loader.load(groupUri));
        }

        return list;
    }
}
