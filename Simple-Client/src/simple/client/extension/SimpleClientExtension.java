package simple.client.extension;

import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

public abstract class SimpleClientExtension implements Lookup.Provider {

    private Lookup lookup = new AbstractLookup(new InstanceContent());

    @Override
    public Lookup getLookup() {
        return lookup;
    }
}
