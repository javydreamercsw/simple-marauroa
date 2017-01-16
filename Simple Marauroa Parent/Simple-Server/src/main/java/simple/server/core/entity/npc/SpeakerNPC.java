package simple.server.core.entity.npc;

import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.RPObject;
import org.openide.util.Lookup;
import simple.server.core.entity.npc.action.NPCAction;

/**
 * NPC's that talk with players.
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
public abstract class SpeakerNPC extends NPC {

    private static final Logger LOG
            = Logger.getLogger(SpeakerNPC.class.getSimpleName());

    public SpeakerNPC() {
        loadDefaultActions();
    }

    public SpeakerNPC(RPObject object) {
        super(object);
        loadDefaultActions();
    }

    private void loadDefaultActions() {
        //Load the default actions
        Lookup.getDefault().lookupAll(NPCAction.class).forEach((a) -> {
            LOG.log(Level.FINE, "Loading action: {0}", a.getDescription());
            context.add(a);
        });
        //Subclasses should add actions on creation.
        loadCustomActions();
    }

    /**
     * Custom loading of actions.
     */
    protected abstract void loadCustomActions();
}
