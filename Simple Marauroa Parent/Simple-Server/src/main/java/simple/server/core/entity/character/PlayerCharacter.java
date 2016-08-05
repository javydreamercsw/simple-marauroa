package simple.server.core.entity.character;

import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.server.core.action.WellKnownActionConstant;
import simple.server.core.entity.RPEntity;
import simple.server.core.entity.RPEntityInterface;
import simple.server.extension.MarauroaServerExtension;

/**
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = RPEntityInterface.class, position = 10)
public class PlayerCharacter extends RPEntity {

    public static final String DEFAULT_RP_CLASSNAME = "character";

    public PlayerCharacter(RPObject object) {
        super(object);
        RPCLASS_NAME = DEFAULT_RP_CLASSNAME;
        setRPClass(RPCLASS_NAME);
        put(WellKnownActionConstant.TYPE, RPCLASS_NAME);
        update();
    }

    public PlayerCharacter() {
        RPCLASS_NAME = DEFAULT_RP_CLASSNAME;
    }

    @Override
    public void generateRPClass() {
        if (!RPClass.hasRPClass(DEFAULT_RP_CLASSNAME)) {
            try {
                RPClass entity = new RPClass(DEFAULT_RP_CLASSNAME);
                entity.isA(RPEntity.class.newInstance().getRPClassName());
                for (MarauroaServerExtension ext
                        : Lookup.getDefault().lookupAll(MarauroaServerExtension.class)) {
                    ext.modifyCharacterRPClassDefinition(entity);
                }
            }
            catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(Character.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (!RPCLASS_NAME.isEmpty() && !RPClass.hasRPClass(RPCLASS_NAME)) {
            RPClass clazz = new RPClass(RPCLASS_NAME);
            clazz.isA(DEFAULT_RP_CLASSNAME);
        }
    }

    @Override
    public void update() {
        super.update();
        for (MarauroaServerExtension ext
                : Lookup.getDefault().lookupAll(MarauroaServerExtension.class)) {
            ext.characterRPClassUpdate(this);
        }
    }
}
