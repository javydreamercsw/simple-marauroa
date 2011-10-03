package simple.client.extension;

import java.util.HashMap;
import java.util.Map;
import marauroa.client.extension.MarauroaClientExtension;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

public abstract class SimpleClientExtension implements MarauroaClientExtension,
        Lookup.Provider {

    /**
     * the logger instance.
     */
    private static final Logger logger = Log4J.getLogger(SimpleClientExtension.class);
    /**
     * Lists the instances of the loaded extensions.
     */
    private static Map<String, SimpleClientExtension> loadedInstances = new HashMap<String, SimpleClientExtension>();
    private Lookup lookup = new AbstractLookup(new InstanceContent());

    @Override
    public Lookup getLookup() {
        return lookup;
    }
}
