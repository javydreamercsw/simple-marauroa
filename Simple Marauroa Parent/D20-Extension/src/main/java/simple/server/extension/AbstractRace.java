package simple.server.extension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import org.openide.util.Lookup;
import simple.server.core.entity.RPEntity;
import simple.server.extension.ability.D20Ability;
import simple.server.extension.feat.D20Feat;
import simple.server.extension.skill.D20Skill;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public abstract class AbstractRace extends RPEntity implements D20Race {

    protected String RP_CLASS = "Abstract Race";
    protected Map<String, Integer> bonuses = new HashMap<>();
    //Feat, level when is available.
    protected Map<Class<? extends D20Feat>, Integer> preferredFeats
            = new HashMap<>();
    protected List<Class<? extends D20Skill>> preferredSkills = new ArrayList<>();
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
                for (D20Ability attr : Lookup.getDefault().lookupAll(D20Ability.class)) {
                    LOG.log(Level.INFO, "Adding attribute: {0}", attr.getName());
                    clazz.addAttribute(attr.getName(), attr.getDefinitionType());
                }
                //Stats
                for (D20Stat stat : Lookup.getDefault().lookupAll(D20Stat.class)) {
                    LOG.log(Level.INFO, "Adding stat: {0}", stat.getName());
                    clazz.addAttribute(stat.getName(), stat.getDefinitionType());
                }
                //Other attributes
                for (D20List attr : Lookup.getDefault().lookupAll(D20List.class)) {
                    LOG.log(Level.INFO, "Adding slot attribute: {0}", attr.getName());
                    clazz.addRPSlot(attr.getName(), attr.getSize());
                }
            } catch (InstantiationException | IllegalAccessException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        if (!RPClass.hasRPClass(RPCLASS_NAME)) {
            RPClass clazz = new RPClass(RPCLASS_NAME);
            clazz.isA(RP_CLASS);
        }
    }

    @Override
    public void update() {
        super.update();
        Lookup.getDefault().lookupAll(D20Ability.class).stream().forEach((attr) -> {
            if (!has(attr.getName())) {
                LOG.log(Level.INFO, "Updating attribute: {0}", attr.getName());
                put(attr.getName(), attr.getDefaultValue());
            }
        });
        Lookup.getDefault().lookupAll(D20Stat.class).stream().forEach((stat) -> {
            if (!has(stat.getName())) {
                LOG.log(Level.INFO, "Updating stat: {0}", stat.getName());
                put(stat.getName(), stat.getDefaultValue());
            }
        });
        Lookup.getDefault().lookupAll(D20List.class).stream().forEach((stat) -> {
            if (!hasSlot(stat.getName())) {
                LOG.log(Level.INFO, "Updating slopt: {0}", stat.getName());
                addSlot(new RPSlot(stat.getName()));
            }
        });
    }

    @Override
    public Map<String, Integer> getAttributeBonuses() {
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
}
