package simple.server.extension.d20;

import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.server.core.entity.Entity;
import simple.server.core.entity.RPEntity;
import simple.server.extension.MarauroaServerExtension;
import simple.server.extension.SimpleServerExtension;
import simple.server.extension.d20.ability.D20Ability;
import simple.server.extension.d20.check.D20Check;
import simple.server.extension.d20.feat.D20Feat;
import simple.server.extension.d20.level.D20Level;
import simple.server.extension.d20.list.D20List;
import simple.server.extension.d20.list.FeatList;
import simple.server.extension.d20.list.SkillList;
import simple.server.extension.d20.map.D20Map;
import simple.server.extension.d20.misc.D20Misc;
import simple.server.extension.d20.skill.D20Skill;
import simple.server.extension.d20.stat.D20Stat;

/**
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = MarauroaServerExtension.class)
public class D20Extension extends SimpleServerExtension {

    public static final String TYPE = "type", CLASS = "class",
            SUBCLASS = "subclass", TITLE = "title";
    private static final Logger LOG
            = Logger.getLogger(D20Extension.class.getSimpleName());

    @Override
    public String getName() {
        return "D20 Extension";
    }

    @Override
    public void afterWorldInit() {
        //Load all Checks by default
        Lookup.getDefault().lookupAll(D20Check.class);
    }

    @Override
    public void modifyRootEntityRPClassDefinition(RPClass clazz) {
        clazz.addAttribute(TYPE, Definition.Type.STRING);
        clazz.addAttribute(CLASS, Definition.Type.STRING);
        clazz.addAttribute(SUBCLASS, Definition.Type.STRING);
        clazz.addAttribute(TITLE, Definition.Type.STRING);
        for (D20Ability attr : Lookup.getDefault().lookupAll(D20Ability.class)) {
            LOG.log(Level.FINE, "Adding attribute: {0}",
                    attr.getCharacteristicName());
            clazz.addAttribute(attr.getCharacteristicName(),
                    attr.getDefinitionType(),
                    attr.getDefinition());
        }
        //Stats
        for (D20Stat stat : Lookup.getDefault().lookupAll(D20Stat.class)) {
            LOG.log(Level.FINE, "Adding stat: {0}",
                    stat.getCharacteristicName());
            clazz.addAttribute(stat.getCharacteristicName(),
                    stat.getDefinitionType(),
                    stat.getDefinition());
        }
        //Maps
        for (D20Map map : Lookup.getDefault().lookupAll(D20Map.class)) {
            LOG.log(Level.FINE, "Adding map: {0}",
                    map.getCharacteristicName());
            clazz.addAttribute(map.getCharacteristicName(),
                    Definition.Type.MAP,
                    map.getDefinition());
        }
        //Misc fields
        for (D20Misc misc : Lookup.getDefault().lookupAll(D20Misc.class)) {
            LOG.log(Level.FINE, "Adding miscellaneous field: {0}",
                    misc.getCharacteristicName());
            clazz.addAttribute(misc.getCharacteristicName(),
                    misc.getDefinitionType(),
                    misc.getDefinition());
        }
        //Other attributes
        for (D20List attr : Lookup.getDefault().lookupAll(D20List.class)) {
            LOG.log(Level.FINE, "Adding slot attribute: {0}",
                    attr.getCharacteristicName());
            clazz.addRPSlot(attr.getCharacteristicName(),
                    attr.getSize(),
                    attr.getDefinition());
        }
    }

    @Override
    public void entityRPClassUpdate(RPObject entity) {
        Lookup.getDefault().lookupAll(D20Ability.class).stream()
                .forEach((attr) -> {
                    if (!entity.has(attr.getCharacteristicName())) {
                        LOG.log(Level.FINE, "Updating attribute: {0}",
                                attr.getCharacteristicName());
                        entity.put(attr.getCharacteristicName(),
                                attr.getDefaultValue());
                    }
                });
        Lookup.getDefault().lookupAll(D20Stat.class).stream()
                .forEach((stat) -> {
                    if (!entity.has(stat.getCharacteristicName())) {
                        LOG.log(Level.FINE, "Updating stat: {0}",
                                stat.getCharacteristicName());
                        entity.put(stat.getCharacteristicName(),
                                stat.getDefaultValue());
                    }
                });
        Lookup.getDefault().lookupAll(D20List.class).stream()
                .forEach((stat) -> {
                    if (!entity.hasSlot(stat.getCharacteristicName())) {
                        LOG.log(Level.FINE, "Updating slot: {0}",
                                stat.getCharacteristicName());
                        RPSlot slot = new RPSlot(stat.getCharacteristicName());
                        slot.setCapacity(stat.getSize());
                        entity.addSlot(slot);
                    }
                });
        Lookup.getDefault().lookupAll(D20Misc.class).stream().forEach((misc) -> {
            if (!entity.has(misc.getCharacteristicName())) {
                LOG.log(Level.FINE, "Updating misc field: {0}",
                        misc.getCharacteristicName());
                entity.put(misc.getCharacteristicName(), misc.getDefaultValue());
            }
        });
        //Update the Feat descriptions
        if (entity.hasSlot(FeatList.FEAT)) {
            RPSlot slot = entity.getSlot(FeatList.FEAT);
            Lookup.getDefault().lookupAll(D20Feat.class).stream().forEach((feat) -> {
                for (RPObject rpo : slot) {
                    if (rpo.get(Entity.NAME).equals(((Entity) feat).getRPClassName())) {
                        LOG.log(java.util.logging.Level.INFO,
                                "Updating {0} from ''{1}'' to ''{2}''",
                                new Object[]{((RPEntity) feat).get(Entity.NAME),
                                    rpo.has(Entity.DESC) ? rpo.get(Entity.DESC) : "",
                                    feat.getDescription()});
                        rpo.put(Entity.DESC, feat.getDescription());
                    }
                }
            });
        }
        //Update the Skill descriptions
        if (entity.hasSlot(SkillList.SKILL)) {
            RPSlot slot = entity.getSlot(SkillList.SKILL);
            Lookup.getDefault().lookupAll(D20Skill.class).stream().forEach((skill) -> {
                for (RPObject rpo : slot) {
                    if (rpo.get(Entity.NAME).equals(((Entity) skill).getRPClassName())) {
                        LOG.log(java.util.logging.Level.INFO,
                                "Updating {0} from ''{1}'' to ''{2}''",
                                new Object[]{((RPEntity) skill).get(Entity.NAME),
                                    rpo.has(Entity.DESC) ? rpo.get(Entity.DESC) : "",
                                    skill.getDescription()});
                        rpo.put(Entity.DESC, skill.getDescription());
                    }
                }
            });
        }
    }

    @Override
    public void rootRPClassUpdate(RPObject entity) {
        if (!entity.has(D20Level.LEVEL)) {
            entity.put(D20Level.LEVEL, 0);
        }
    }
}
