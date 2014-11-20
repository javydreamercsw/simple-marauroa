package simple.server.extension;

import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import org.apache.log4j.Logger;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.attribute.iD20Attribute;
import simple.server.extension.attribute.iD20List;
import simple.server.extension.attribute.iD20Stat;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = MarauroaServerExtension.class)
public class D20Extension extends SimpleServerExtension {

    private static final Logger LOG
            = Logger.getLogger(D20Extension.class.getSimpleName());

    @Override
    public void modifyRootRPClassDefinition(RPClass entity) {
        //Attributes
        Lookup.getDefault().lookupAll(iD20Attribute.class).stream().forEach((attr) -> {
            LOG.info("Adding attribute: " + attr.getName());
            entity.addAttribute(attr.getName(), Definition.Type.INT);
        });
        //Stats
        Lookup.getDefault().lookupAll(iD20Stat.class).stream().forEach((stat) -> {
            LOG.info("Adding stat: " + stat.getName());
            entity.addAttribute(stat.getName(), Definition.Type.INT);
        });
        //Other attributes
        Lookup.getDefault().lookupAll(iD20List.class).stream().forEach((attr) -> {
            LOG.info("Adding list attribute: " + attr.getName());
            entity.addRPSlot(attr.getName(), attr.getSize());
        });
    }

    @Override
    public void rootRPClassUpdate(RPObject entity) {
        Lookup.getDefault().lookupAll(iD20Attribute.class).stream().forEach((attr) -> {
            if (!entity.has(attr.getName())) {
                LOG.info("Updating attribute: " + attr.getName());
                entity.put(attr.getName(), attr.getDefaultValue());
            }
        });
        Lookup.getDefault().lookupAll(iD20Stat.class).stream().forEach((stat) -> {
            if (!entity.has(stat.getName())) {
                LOG.info("Updating stat: " + stat.getName());
                entity.put(stat.getName(), stat.getDefaultValue());
            }
        });
    }

    @Override
    public String getName() {
        return "D20 Extension";
    }
}
