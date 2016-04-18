package simple.server.extension;

import marauroa.common.game.*;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import simple.common.SimpleException;
import simple.common.game.ClientObjectInterface;
import simple.server.core.action.ActionInterface;

public abstract class SimpleServerExtension implements MarauroaServerExtension,
        Lookup.Provider, ActionInterface {

    private final Lookup lookup = new AbstractLookup(new InstanceContent());

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
    public void modifyRootRPClassDefinition(RPClass client) {
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

    @Override
    public void clientObjectUpdate(ClientObjectInterface client) throws SimpleException {
        //Do nothing by default
    }

    @Override
    public void rootRPClassUpdate(RPObject entity) {
        //Do nothing by default
    }

    @Override
    public void onRPClassAddAttribute(RPClass rpclass,
            String name, Definition.Type type, byte flags) {
        //Do nothing by default
    }

    @Override
    public void onRPClassAddAttribute(RPClass rpclass,
            String name, Definition.Type type) {
        //Do nothing by default
    }
}
