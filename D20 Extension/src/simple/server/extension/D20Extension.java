package simple.server.extension;

import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import org.openide.util.lookup.ServiceProvider;

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
}
