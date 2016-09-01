/*
 * This extension takes care of registering RPEvents to both Client Objects and characters.
 */
package simple.server.extension;

import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.server.core.event.api.IRPEvent;

@ServiceProvider(service = MarauroaServerExtension.class, position = 1)
public class RPEventExtension extends SimpleServerExtension {

    @Override
    public String getName() {
        return "RPEvent Extension";
    }

    @Override
    public void modifyClientObjectDefinition(RPClass client) {
        //Register all events
        for (IRPEvent event : Lookup.getDefault().lookupAll(IRPEvent.class)) {
            client.addRPEvent(event.getRPClassName(), Definition.VOLATILE);
        }
    }

    @Override
    public void modifyCharacterRPClassDefinition(RPClass character) {
        //Register all events
        for (IRPEvent event : Lookup.getDefault().lookupAll(IRPEvent.class)) {
            character.addRPEvent(event.getRPClassName(), Definition.VOLATILE);
        }
    }
}
