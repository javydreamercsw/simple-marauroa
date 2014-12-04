package simple.server.extension.d20;

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
import simple.server.core.entity.RPEntity;
import simple.server.extension.d20.ability.D20Ability;
import simple.server.extension.d20.feat.D20Feat;
import simple.server.extension.d20.skill.D20Skill;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public abstract class AbstractRace extends RPEntity implements D20Race {

    private String RP_CLASS = "Abstract Race";
    public static final String FEAT_POINTS = "Feat Points";
    public static final String SKILL_POINTS = "Skill Points";
    protected int bonusSkillPoints = 0, bonusFeatPoints = 0;
    //Ability, Bonus
    protected Map<Class<? extends D20Ability>, Integer> bonuses
            = new HashMap<>();
    //Feat, level when is available.
    protected Map<Class<? extends D20Feat>, Integer> preferredFeats
            = new HashMap<>();
    protected List<Class<? extends D20Skill>> preferredSkills
            = new ArrayList<>();
    private static final Logger LOG
            = Logger.getLogger(AbstractRace.class.getSimpleName());

    public AbstractRace(RPObject object) {
        super(object);
    }

    public AbstractRace() {
    }

    @Override
    public void generateRPClass() {
        if (!RPClass.hasRPClass(RP_CLASS)) {
            try {
                RPClass clazz = new RPClass(RP_CLASS);
                clazz.isA(RPEntity.class.newInstance().getRPClassName());
                //Attributes
                Lookup.getDefault().lookupAll(D20Ability.class).stream().map((attr) -> {
                    LOG.log(Level.FINE, "Adding attribute: {0}", attr.getName());
                    return attr;
                }).forEach((attr) -> {
                    clazz.addAttribute(attr.getName(), attr.getDefinitionType());
                });
                //Stats
                Lookup.getDefault().lookupAll(D20Stat.class).stream().map((stat) -> {
                    LOG.log(Level.FINE, "Adding stat: {0}", stat.getName());
                    return stat;
                }).forEach((stat) -> {
                    clazz.addAttribute(stat.getName(), stat.getDefinitionType());
                });
                //Other attributes
                Lookup.getDefault().lookupAll(D20List.class).stream().map((attr) -> {
                    LOG.log(Level.FINE, "Adding slot attribute: {0}", attr.getName());
                    return attr;
                }).forEach((attr) -> {
                    clazz.addRPSlot(attr.getName(), attr.getSize());
                });
                clazz.addAttribute(FEAT_POINTS, Definition.Type.INT);
                clazz.addAttribute(SKILL_POINTS, Definition.Type.INT);
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
        if (!has(FEAT_POINTS)) {
            LOG.log(Level.FINE, "Updating attribute: {0}", FEAT_POINTS);
            put(FEAT_POINTS, 2);
        }
        if (!has(SKILL_POINTS)) {
            LOG.log(Level.FINE, "Updating attribute: {0}", SKILL_POINTS);
            put(SKILL_POINTS, 20);
        }
        Lookup.getDefault().lookupAll(D20Ability.class).stream().forEach((attr) -> {
            if (!has(attr.getName())) {
                LOG.log(Level.FINE, "Updating attribute: {0}", attr.getName());
                put(attr.getName(), attr.getDefaultValue());
            }
        });
        Lookup.getDefault().lookupAll(D20Stat.class).stream().forEach((stat) -> {
            if (!has(stat.getName())) {
                LOG.log(Level.FINE, "Updating stat: {0}", stat.getName());
                put(stat.getName(), stat.getDefaultValue());
            }
        });
        Lookup.getDefault().lookupAll(D20List.class).stream().forEach((stat) -> {
            if (!hasSlot(stat.getName())) {
                LOG.log(Level.FINE, "Updating slopt: {0}", stat.getName());
                addSlot(new RPSlot(stat.getName()));
            }
        });
    }

    @Override
    public Map<Class<? extends D20Ability>, Integer> getAttributeBonuses() {
        return bonuses;
    }

    @Override
    public Map<Class<? extends D20Feat>, Integer> getPrefferedFeats() {
        return preferredFeats;
    }

    @Override
    public List<Class<? extends D20Skill>> getPrefferedSkills() {
        return preferredSkills;
    }
    
    @Override
    public int getBonusSkillPoints(int level){
        return bonusSkillPoints;
    }
    
    @Override
    public int getBonusFeatPoints(int level){
        return bonusFeatPoints;
    }
}