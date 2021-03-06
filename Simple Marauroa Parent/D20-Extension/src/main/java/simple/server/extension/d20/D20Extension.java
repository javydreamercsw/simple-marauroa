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
import simple.server.extension.MarauroaServerExtension;
import simple.server.extension.SimpleServerExtension;
import simple.server.extension.d20.ability.D20Ability;
import simple.server.extension.d20.check.D20Check;
import simple.server.extension.d20.feat.D20Feat;
import simple.server.extension.d20.item.D20ItemAttribute;
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
    public void modifyCharacterRPClassDefinition(RPClass clazz) {
        clazz.addAttribute(TYPE, Definition.Type.STRING);
        clazz.addAttribute(CLASS, Definition.Type.STRING);
        clazz.addAttribute(SUBCLASS, Definition.Type.STRING);
        clazz.addAttribute(TITLE, Definition.Type.STRING);
        clazz.addAttribute(D20Level.MAX, Definition.Type.INT);
        Lookup.getDefault().lookupAll(D20Ability.class).stream().map((attr) -> {
            LOG.log(Level.FINE, "Adding attribute: {0}",
                    attr.getCharacteristicName());
            return attr;
        }).forEachOrdered((attr) -> {
            clazz.addAttribute(attr.getCharacteristicName(),
                    attr.getDefinitionType(),
                    attr.getDefinition());
        });
        //Maps
        Lookup.getDefault().lookupAll(D20Map.class).stream().map((map) -> {
            LOG.log(Level.FINE, "Adding map: {0}",
                    map.getCharacteristicName());
            return map;
        }).forEachOrdered((map) -> {
            clazz.addAttribute(map.getCharacteristicName(),
                    Definition.Type.MAP,
                    map.getDefinition());
        });
        //Misc fields
        Lookup.getDefault().lookupAll(D20Misc.class).stream().map((misc) -> {
            LOG.log(Level.FINE, "Adding miscellaneous field: {0}",
                    misc.getCharacteristicName());
            return misc;
        }).forEachOrdered((misc) -> {
            clazz.addAttribute(misc.getCharacteristicName(),
                    misc.getDefinitionType(),
                    misc.getDefinition());
        });
        //Other attributes
        Lookup.getDefault().lookupAll(D20List.class).stream().map((attr) -> {
            LOG.log(Level.FINE, "Adding slot attribute: {0}",
                    attr.getCharacteristicName());
            return attr;
        }).forEachOrdered((attr) -> {
            clazz.addRPSlot(attr.getCharacteristicName(),
                    attr.getSize(),
                    attr.getDefinition());
        });
        //Stats
        Lookup.getDefault().lookupAll(D20Stat.class).stream().map((stat) -> {
            LOG.log(Level.FINE, "Adding stat: {0}",
                    stat.getCharacteristicName());
            return stat;
        }).forEachOrdered((stat) -> {
            clazz.addAttribute(stat.getCharacteristicName(),
                    stat.getDefinitionType(),
                    stat.getDefinition());
        });
    }

    @Override
    public void modifyItemRPClassDefinition(RPClass item) {
        Lookup.getDefault()
                .lookupAll(D20ItemAttribute.class).stream().map((attr) -> {
            LOG.log(Level.FINE, "Adding item attribute: {0}",
                    attr.getCharacteristicName());
            return attr;
        }).forEachOrdered((attr) -> {
            item.addAttribute(attr.getCharacteristicName(),
                    attr.getDefinitionType(),
                    attr.getDefinition());
        });
    }

    @Override
    public void characterRPClassUpdate(RPObject entity) {
        if (!entity.has(D20Level.MAX)) {
            entity.put(D20Level.MAX, 0);
        }
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
                        LOG.log(java.util.logging.Level.FINE,
                                "Updating {0} from ''{1}'' to ''{2}''",
                                new Object[]{((Entity) feat).get(Entity.NAME),
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
                        LOG.log(java.util.logging.Level.FINE,
                                "Updating {0} from ''{1}'' to ''{2}''",
                                new Object[]{((Entity) skill).get(Entity.NAME),
                                    rpo.has(Entity.DESC) ? rpo.get(Entity.DESC) : "",
                                    skill.getDescription()});
                        rpo.put(Entity.DESC, skill.getDescription());
                    }
                }
            });
        }
    }

    @Override
    public void modifyRootRPClassDefinition(RPClass client) {
        client.addAttribute(D20Level.LEVEL,
                Definition.Type.INT,
                Definition.STANDARD);
    }

    @Override
    public void rootRPClassUpdate(RPObject entity) {
        if (!entity.has(D20Level.LEVEL)) {
            entity.put(D20Level.LEVEL, 0);
        }
    }

    @Override
    public void itemRPClassUpdate(RPObject item) {
        Lookup.getDefault()
                .lookupAll(D20ItemAttribute.class).stream().map((attr) -> {
            LOG.log(Level.FINE, "Updating item attribute: {0}",
                    attr.getCharacteristicName());
            return attr;
        }).filter((attr) -> (!item.has(attr.getCharacteristicName())))
                .forEachOrdered((attr) -> {
                    item.add(attr.getCharacteristicName(), 0);
                });
    }
}
