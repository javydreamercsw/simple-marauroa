package simple.server.extension.d20.rpclass;

import simple.server.extension.d20.list.D20List;
import simple.server.extension.d20.stat.D20Stat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import org.openide.util.Lookup;
import simple.server.core.entity.Entity;
import simple.server.core.entity.RPEntity;
import simple.server.extension.d20.ability.D20Ability;
import simple.server.extension.d20.feat.D20Feat;
import simple.server.extension.d20.level.D20Level;
import simple.server.extension.d20.list.FeatList;
import simple.server.extension.d20.list.SkillList;
import simple.server.extension.d20.map.D20Map;
import simple.server.extension.d20.misc.D20Misc;
import simple.server.extension.d20.skill.D20Skill;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public abstract class AbstractClass extends RPEntity implements D20Class {

    public final static String RP_CLASS = "Abstract Class";
    protected int bonusSkillPoints = 0, bonusFeatPoints = 0;
    //Ability, Bonus
    private Map<Class<? extends D20Ability>, Integer> bonuses
            = new HashMap<>();
    //Feat, level when is available.
    private List<Class<? extends D20Feat>> preferredFeats = new ArrayList<>();
    //Feat, level when is gained.
    private Map<Class<? extends D20Feat>, Integer> bonusFeats
            = new HashMap<>();
    private List<Class<? extends D20Skill>> preferredSkills
            = new ArrayList<>();
    private static final Logger LOG
            = Logger.getLogger(AbstractClass.class.getSimpleName());
    private Map<Class<? extends D20Skill>, Integer> bonusSkills
            = new HashMap<>();

    public AbstractClass(RPObject object) {
        super(object);
        update();
    }

    public AbstractClass() {
    }

    @Override
    public void generateRPClass() {
        if (!RPClass.hasRPClass(RP_CLASS)) {
            try {
                RPClass clazz = new RPClass(RP_CLASS);
                clazz.isA(RPEntity.class.newInstance().getRPClassName());
                //Level attribute
                clazz.addAttribute(D20Level.LEVEL, Definition.Type.INT);
                //Attributes
                Lookup.getDefault().lookupAll(D20Ability.class).stream()
                        .map((attr) -> {
                            LOG.log(Level.FINE, "Adding attribute: {0}",
                                    attr.getCharacteristicName());
                            return attr;
                        }).forEach((attr) -> {
                            clazz.addAttribute(attr.getCharacteristicName(),
                                    attr.getDefinitionType(),
                                    attr.getDefinition());
                        });
                //Stats
                Lookup.getDefault().lookupAll(D20Stat.class).stream()
                        .map((stat) -> {
                            LOG.log(Level.FINE, "Adding stat: {0}",
                                    stat.getCharacteristicName());
                            return stat;
                        }).forEach((stat) -> {
                            clazz.addAttribute(stat.getCharacteristicName(),
                                    stat.getDefinitionType(),
                                    stat.getDefinition());
                        });
                //Maps
                Lookup.getDefault().lookupAll(D20Map.class).stream()
                        .map((map) -> {
                            LOG.log(Level.FINE, "Adding map: {0}",
                                    map.getCharacteristicName());
                            return map;
                        }).forEach((map) -> {
                            clazz.addAttribute(map.getCharacteristicName(),
                                    Definition.Type.MAP,
                                    map.getDefinition());
                        });
                //Misc fields
                Lookup.getDefault().lookupAll(D20Misc.class).stream().map((misc) -> {
                    LOG.log(Level.FINE, "Adding miscellaneous field: {0}",
                            misc.getCharacteristicName());
                    return misc;
                }).forEach((misc) -> {
                    clazz.addAttribute(misc.getCharacteristicName(),
                            misc.getDefinitionType(),
                            misc.getDefinition());
                });
                //Other attributes
                Lookup.getDefault().lookupAll(D20List.class).stream()
                        .map((attr) -> {
                            LOG.log(Level.FINE, "Adding slot attribute: {0}",
                                    attr.getCharacteristicName());
                            return attr;
                        }).forEach((attr) -> {
                            clazz.addRPSlot(attr.getCharacteristicName(),
                                    attr.getSize(),
                                    attr.getDefinition());
                        });
            } catch (InstantiationException | IllegalAccessException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        if (!RPCLASS_NAME.isEmpty() && !RPClass.hasRPClass(RPCLASS_NAME)) {
            RPClass clazz = new RPClass(RPCLASS_NAME);
            clazz.isA(RP_CLASS);
        }
    }

    @Override
    public void update() {
        super.update();
        if (!has(D20Level.LEVEL)) {
            put(D20Level.LEVEL, 0);
        }
        Lookup.getDefault().lookupAll(D20Ability.class).stream()
                .forEach((attr) -> {
                    if (!has(attr.getCharacteristicName())) {
                        LOG.log(Level.FINE, "Updating attribute: {0}",
                                attr.getCharacteristicName());
                        put(attr.getCharacteristicName(),
                                attr.getDefaultValue());
                    }
                });
        Lookup.getDefault().lookupAll(D20Stat.class).stream()
                .forEach((stat) -> {
                    if (!has(stat.getCharacteristicName())) {
                        LOG.log(Level.FINE, "Updating stat: {0}",
                                stat.getCharacteristicName());
                        put(stat.getCharacteristicName(),
                                stat.getDefaultValue());
                    }
                });
        Lookup.getDefault().lookupAll(D20List.class).stream()
                .forEach((stat) -> {
                    if (!hasSlot(stat.getCharacteristicName())) {
                        LOG.log(Level.FINE, "Updating slot: {0}",
                                stat.getCharacteristicName());
                        RPSlot slot = new RPSlot(stat.getCharacteristicName());
                        slot.setCapacity(stat.getSize());
                        addSlot(slot);
                    }
                });
        Lookup.getDefault().lookupAll(D20Misc.class).stream().forEach((misc) -> {
            if (!has(misc.getCharacteristicName())) {
                LOG.log(Level.FINE, "Updating misc field: {0}",
                        misc.getCharacteristicName());
                put(misc.getCharacteristicName(), misc.getDefaultValue());
            }
        });
        //Update the Feat descriptions
        if (hasSlot(FeatList.FEAT)) {
            RPSlot slot = getSlot(FeatList.FEAT);
            Lookup.getDefault().lookupAll(D20Feat.class).stream().forEach((feat) -> {
                for (RPObject rpo : slot) {
                    if (rpo.get(Entity.NAME).equals(((RPEntity) feat).getRPClassName())
                            && !rpo.get(Entity.DESC).equals(feat.getDescription())) {
                        LOG.log(java.util.logging.Level.INFO,
                                "Updating {0} from ''{1}'' to ''{2}''",
                                new Object[]{((RPEntity) feat).get(Entity.NAME),
                                    rpo.get(Entity.DESC),
                                    feat.getDescription()});
                        rpo.put(Entity.DESC, feat.getDescription());
                    }
                }
            });
        }
        //Update the Skill descriptions
        if (hasSlot(SkillList.SKILL)) {
            RPSlot slot = getSlot(SkillList.SKILL);
            Lookup.getDefault().lookupAll(D20Skill.class).stream().forEach((skill) -> {
                for (RPObject rpo : slot) {
                    if (rpo.get(Entity.NAME).equals(((RPEntity) skill).getRPClassName())
                            && !rpo.get(Entity.DESC).equals(skill.getDescription())) {
                        LOG.log(java.util.logging.Level.INFO,
                                "Updating {0} from ''{1}'' to ''{2}''",
                                new Object[]{((RPEntity) skill).get(Entity.NAME),
                                    rpo.get(Entity.DESC),
                                    skill.getDescription()});
                        rpo.put(Entity.DESC, skill.getDescription());
                    }
                }
            });
        }
    }

    @Override
    public Map<Class<? extends D20Ability>, Integer> getAttributeBonuses() {
        return bonuses;
    }

    @Override
    public Map<Class<? extends D20Feat>, Integer> getBonusFeats() {
        return bonusFeats;
    }

    @Override
    public List<Class<? extends D20Feat>> getPrefferedFeats() {
        return preferredFeats;
    }

    @Override
    public List<Class<? extends D20Skill>> getPrefferedSkills() {
        return preferredSkills;
    }

    @Override
    public int getBonusSkillPoints(int level) {
        return bonusSkillPoints;
    }

    @Override
    public int getBonusFeatPoints(int level) {
        return bonusFeatPoints;
    }

    @Override
    public Map<Class<? extends D20Skill>, Integer> getBonusSkills() {
        return bonusSkills;
    }

    @Override
    public int getLevel() {
        return getInt(D20Level.LEVEL);
    }

    @Override
    public void setLevel(int level) {
        put(D20Level.LEVEL, level);
    }

    @Override
    public int getMaxLevel() {
        return -1;
    }

    @Override
    public void setMaxLevel(int max) {
        //Do nothing
    }
}
