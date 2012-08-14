package pcgen.system;

import gmgen.pluginmgr.PluginManager;
import java.io.File;
import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import org.apache.commons.lang.SystemUtils;
import org.openide.util.lookup.ServiceProvider;
import pcgen.core.prereq.PrerequisiteTestFactory;
import pcgen.gui.converter.TokenConverter;
import pcgen.io.ExportHandler;
import pcgen.persistence.CampaignFileLoader;
import pcgen.persistence.GameModeFileLoader;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.TokenStore;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterFactory;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.persistence.TokenLibrary;
import pcgen.util.Logging;
import pcgen.util.PJEP;
import simple.server.extension.MarauroaServerExtension;
import simple.server.extension.SimpleServerExtension;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = MarauroaServerExtension.class)
public class D20Extension extends SimpleServerExtension {

    public static final String TYPE = "Type", CLASS = "Class",
            SUBCLASS = "Subclass", TITLE = "Title", EQUIPMENT = "Equipment",
            EXP = "Experience", AC = "AC", INT = "Intelligence",
            CON = "Constitution", DEX = "Dexterity", STR = "Strength",
            WIS = "Wisdom", CHA = "Charisma", RACE = "Race", SKILLS = "Skills",
            FEATS = "Feats", ABILITIES = "Abilities", SPELLS = "Spells",
            DEITIES = "Desties", DOMAIN = "Domain",
            KNOWN_SPELLS = "Known Spells", PREP_SPELLS = "Prepared Spells";
    private static String pluginsDir = System.getProperty("user.dir") + "/plugins";
    private static PropertyContextFactory configFactory;

    public D20Extension() {
        configFactory = new PropertyContextFactory(SystemUtils.USER_DIR);
        configFactory.registerAndLoadPropertyContext(ConfigurationSettings.getInstance());
        PCGenTaskExecutor executor = new PCGenTaskExecutor();
        executor.addPCGenTask(createLoadPluginTask());
        executor.addPCGenTask(new GameModeFileLoader());
        executor.addPCGenTask(new CampaignFileLoader());
        executor.execute();
    }

    public static void main(String[] args) {
        D20Extension test = new D20Extension();
    }

    private static PCGenTask createLoadPluginTask() {
        File pluginDir = new File(pluginsDir);
        if (pluginDir.exists()) {
            PluginClassLoader loader = new PluginClassLoader(pluginDir);
            loader.addPluginLoader(TokenLibrary.getInstance());
            loader.addPluginLoader(TokenStore.inst());
            try {
                loader.addPluginLoader(PreParserFactory.getInstance());
            } catch (PersistenceLayerException ex) {
                Logging.errorPrint("createLoadPluginTask failed", ex);
            }
            loader.addPluginLoader(PrerequisiteTestFactory.getInstance());
            loader.addPluginLoader(PrerequisiteWriterFactory.getInstance());
            loader.addPluginLoader(PJEP.getJepPluginLoader());
            loader.addPluginLoader(ExportHandler.getPluginLoader());
            loader.addPluginLoader(TokenConverter.getPluginLoader());
            loader.addPluginLoader(PluginManager.getInstance());
            return loader;
        } else {
            return null;
        }
    }

    @Override
    public void modifyRootRPClassDefinition(RPClass entity) {
        entity.addAttribute(TYPE, Definition.Type.STRING);
        entity.addAttribute(CLASS, Definition.Type.STRING);
        entity.addAttribute(TITLE, Definition.Type.STRING);
        entity.addAttribute(RACE, Definition.Type.STRING);
        //Stats
        entity.addAttribute(EXP, Definition.Type.INT);
        entity.addAttribute(AC, Definition.Type.INT);
        entity.addAttribute(INT, Definition.Type.INT);
        entity.addAttribute(CON, Definition.Type.INT);
        entity.addAttribute(DEX, Definition.Type.INT);
        entity.addAttribute(STR, Definition.Type.INT);
        entity.addAttribute(WIS, Definition.Type.INT);
        entity.addAttribute(CHA, Definition.Type.INT);
        //Other attributes
        entity.addRPSlot(SUBCLASS, -1);
        entity.addRPSlot(EQUIPMENT, -1);
        entity.addRPSlot(SKILLS, -1);
        entity.addRPSlot(FEATS, -1);
        entity.addRPSlot(ABILITIES, -1);
        entity.addRPSlot(SPELLS, -1);
        entity.addRPSlot(DEITIES, -1);
        entity.addRPSlot(DOMAIN, -1);
        entity.addRPSlot(KNOWN_SPELLS, -1);
        entity.addRPSlot(PREP_SPELLS, -1);
    }

    @Override
    public void rootRPClassUpdate(RPObject client) {
        if (!client.has(EXP)) {
            client.put(EXP, 0);
        }
        if (!client.has(INT)) {
            client.put(INT, 0);
        }
        if (!client.has(CON)) {
            client.put(CON, 0);
        }
        if (!client.has(DEX)) {
            client.put(DEX, 0);
        }
        if (!client.has(STR)) {
            client.put(STR, 0);
        }
        if (!client.has(WIS)) {
            client.put(WIS, 0);
        }
        if (!client.has(CHA)) {
            client.put(CHA, 0);
        }
        if (!client.has(AC)) {
            client.put(AC, 0);
        }
    }

    public static void shutdown() {
        configFactory.savePropertyContexts();
        PropertyContextFactory.getDefaultFactory().savePropertyContexts();
    }

    public String getName() {
        return "D20 Extension";
    }
}
