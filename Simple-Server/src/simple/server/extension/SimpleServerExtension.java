package simple.server.extension;

import marauroa.common.game.*;
import marauroa.server.game.extension.MarauroaServerExtension;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

public abstract class SimpleServerExtension implements MarauroaServerExtension,
        Lookup.Provider {

    private Lookup lookup = new AbstractLookup(new InstanceContent());

    @Override
    public boolean updateMonitor(RPObject object, Perception perception) {
        return true;
    }

    @Override
    public void init() {
        //Do nothing by default
    }

    @Override
    public boolean perform(String name) {
        return true;
    }

    @Override
    public void onAction(RPObject player, RPAction action) {
        //Do nothing by default
    }
    
    @Override
    public void onAddRPZone(IRPZone zone) {
        //Do nothing by default
    }

    @Override
    public RPObject onRPObjectAddToZone(RPObject object) {
        return object;
    }

    @Override
    public RPObject onRPObjectRemoveFromZone(RPObject object) {
        return object;
    }

    @Override
    public void getPerception(RPObject object, byte type, Perception p) {
        //Do nothing by default
    }

    @Override
    public void modifyClientObjectDefinition(RPClass client) {
        //Do nothing by default
    }

    @Override
    public void afterWorldInit() {
        //Do nothing by default
    }

    @Override
    public void updateDatabase() {
        //Do nothing by default
    }
    
    @Override
    public void onRemoveRPZone(IRPZone zone) {
        //Do nothing by default
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }
}
