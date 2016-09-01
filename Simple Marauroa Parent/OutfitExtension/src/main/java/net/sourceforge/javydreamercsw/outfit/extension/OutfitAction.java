package net.sourceforge.javydreamercsw.outfit.extension;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.server.game.rp.IRPRuleProcessor;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.common.game.ClientObjectInterface;
import simple.server.core.action.ActionProvider;
import simple.server.core.action.CommandCenter;
import simple.server.core.engine.SimpleRPRuleProcessor;

@ServiceProvider(service = ActionProvider.class)
public class OutfitAction implements ActionProvider {

    private static final String VALUE = "value";
    private static final String OUTFIT = "outfit";

    @Override
    public void register() {
        CommandCenter.register(OUTFIT, new OutfitAction());
    }

    @Override
    public void onAction(RPObject rpo, RPAction action) {
        if (rpo instanceof ClientObjectInterface) {
            ClientObjectInterface player = (ClientObjectInterface) rpo;
            if (action.has(VALUE)) {
                ((SimpleRPRuleProcessor) Lookup.getDefault()
                        .lookup(IRPRuleProcessor.class))
                        .addGameEvent(player.getName(),
                                OUTFIT, action.get(VALUE));
                Outfit outfit = new Outfit(action.getInt(VALUE));
                if (outfit.isChoosableByPlayers()) {
                    OutfitExtension.setOutfit((RPObject) player, outfit, false);
                }
            }
        }
    }
}
