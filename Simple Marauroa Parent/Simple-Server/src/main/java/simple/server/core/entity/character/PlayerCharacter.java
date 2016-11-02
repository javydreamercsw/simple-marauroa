package simple.server.core.entity.character;

import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.server.core.action.WellKnownActionConstant;
import simple.server.core.entity.RPEntity;
import simple.server.core.entity.RPEntityInterface;
import simple.server.core.entity.api.LevelEntity;
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
            RPClass entity = new RPClass(DEFAULT_RP_CLASSNAME);
            entity.isA(RPEntity.DEFAULT_RPCLASS);
            Lookup.getDefault().lookupAll(MarauroaServerExtension.class)
                    .forEach((ext) -> {
                ext.modifyCharacterRPClassDefinition(entity);
                    });
            entity.addAttribute(LevelEntity.LEVEL, Definition.Type.INT);
        } else if (!RPCLASS_NAME.isEmpty() && !RPClass.hasRPClass(RPCLASS_NAME)) {
            RPClass clazz = new RPClass(RPCLASS_NAME);
            clazz.isA(DEFAULT_RP_CLASSNAME);
        }
    }

    @Override
    public void update() {
        super.update();
        Lookup.getDefault().lookupAll(MarauroaServerExtension.class)
                .forEach((ext) -> {
            ext.characterRPClassUpdate(this);
                });
        if (!has(LevelEntity.LEVEL)) {
            put(LevelEntity.LEVEL, 0);
        }
    }
}
