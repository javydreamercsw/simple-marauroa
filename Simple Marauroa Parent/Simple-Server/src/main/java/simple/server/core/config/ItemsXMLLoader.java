package simple.server.core.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import simple.server.core.action.WellKnownActionConstant;
import simple.server.core.rule.defaultruleset.DefaultItem;

public class ItemsXMLLoader extends DefaultHandler {

    /**
     * the logger instance.
     */
    private static final Logger LOG
            = Logger.getLogger(ItemsXMLLoader.class.getSimpleName());
    private String name;
    private String clazz;
    private String subclass;
    private String description;
    private String text;
    private double weight;
    private int value;
    /**
     * slots where this item can be equipped.
     */
    private List<String> slots;
    /**
     * Attributes of the item.
     */
    private Map<String, String> attributes;
    private List<DefaultItem> list;
    private boolean attributesTag;
    protected Class<?> implementation;

    public List<DefaultItem> load(URI uri) throws SAXException {
        list = new LinkedList<>();
        // Use the default (non-validating) parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        InputStream is = null;
        try {
            // Parse the input
            SAXParser saxParser = factory.newSAXParser();

            is = getClass().getResourceAsStream(uri.getPath());

            if (is == null) {
                throw new FileNotFoundException("cannot find resource '" + uri + "' in classpath");
            }
            saxParser.parse(is, this);
        } catch (ParserConfigurationException t) {
            LOG.log(Level.SEVERE, null, t);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
            throw new SAXException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
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
    public void startElement(String namespaceURI, String lName, String qName,
            Attributes attrs) {
        text = "";
        if (qName.equals("item")) {
            name = attrs.getValue("name");
            attributes = new LinkedHashMap<>();
            slots = new LinkedList<>();
            description = "";
            implementation = null;
        } else if (qName.equals(WellKnownActionConstant.TYPE)) {
            clazz = attrs.getValue("class");
            subclass = attrs.getValue("subclass");
        } else if (qName.equals("implementation")) {

            String className = attrs.getValue("class-name");

            try {
                implementation = Class.forName(className);
            } catch (ClassNotFoundException ex) {
                LOG.log(Level.SEVERE, "Unable to load class: {0}", className);
            }
        } else if (qName.equals("weight")) {
            weight = Double.parseDouble(attrs.getValue("value"));
        } else if (qName.equals("value")) {
            value = Integer.parseInt(attrs.getValue("value"));
        } else if (qName.equals("slot")) {
            slots.add(attrs.getValue("name"));
        } else if (qName.equals("attributes")) {
            attributesTag = true;
        } else if (attributesTag) {
            attributes.put(qName, attrs.getValue("value"));
        }
    }

    @Override
    public void endElement(String namespaceURI, String sName, String qName) {
        switch (qName) {
            case "item":
                DefaultItem item = new DefaultItem(clazz, subclass, name, -1);
                item.setWeight(weight);
                item.setEquipableSlots(slots);
                item.setAttributes(attributes);
                item.setDescription(description);
                item.setValue(value);
                if (implementation == null) {
                    LOG.log(Level.SEVERE,
                            "Item without defined implementation: {0}", name);
                    return;
                }
                item.setImplementation(implementation);
                list.add(item);
                break;
            case "attributes":
                attributesTag = false;
                break;
            case "description":
                if (text != null) {
                    description = text.trim();
                    description = description.replaceAll(" +", " ");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void characters(char[] buf, int offset, int len) {
        text += (new String(buf, offset, len)).trim();
    }
}
