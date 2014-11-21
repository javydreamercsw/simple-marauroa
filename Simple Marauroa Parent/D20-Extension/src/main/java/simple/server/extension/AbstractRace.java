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

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public abstract class AbstractRace extends RPEntity implements D20Race {

    protected String RP_CLASS = "Abstract Race";
    private Map<String, Integer> bonuses = new HashMap<>();
    private List<D20Class> preferred = new ArrayList<>();
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
                    LOG.log(Level.INFO, "Adding attribute: {0}", attr.getName());
                    return attr;
                }).forEach((attr) -> {
                    clazz.addAttribute(attr.getName(), attr.getDefinitionType());
                });
                //Stats
                Lookup.getDefault().lookupAll(D20Stat.class).stream().map((stat) -> {
                    LOG.log(Level.INFO, "Adding stat: {0}", stat.getName());
                    return stat;
                }).forEach((stat) -> {
                    clazz.addAttribute(stat.getName(), stat.getDefinitionType());
                });
                //Other attributes
                Lookup.getDefault().lookupAll(D20List.class).stream().map((attr) -> {
                    LOG.log(Level.INFO, "Adding slot attribute: {0}", attr.getName());
                    return attr;
                }).forEach((attr) -> {
                    clazz.addRPSlot(attr.getName(), attr.getSize());
                });
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
    public List<D20Class> getPrefferedClasses() {
        return preferred;
    }
}
