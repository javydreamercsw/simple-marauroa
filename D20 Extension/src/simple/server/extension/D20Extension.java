package simple.server.extension;

import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import org.openide.util.lookup.ServiceProvider;
import simple.common.game.ClientObjectInterface;

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
    public void modifyClientObjectDefinition(RPClass player) {
        player.addAttribute(TYPE, Definition.Type.STRING);
        player.addAttribute(CLASS, Definition.Type.STRING);
        player.addAttribute(TITLE, Definition.Type.STRING);
        player.addAttribute(RACE, Definition.Type.STRING);
        //Stats
        player.addAttribute(EXP, Definition.Type.INT);
        player.addAttribute(AC, Definition.Type.INT);
        player.addAttribute(INT, Definition.Type.INT);
        player.addAttribute(CON, Definition.Type.INT);
        player.addAttribute(DEX, Definition.Type.INT);
        player.addAttribute(STR, Definition.Type.INT);
        player.addAttribute(WIS, Definition.Type.INT);
        player.addAttribute(CHA, Definition.Type.INT);
        //Other attributes
        player.addRPSlot(SUBCLASS, -1);
        player.addRPSlot(EQUIPMENT, -1);
        player.addRPSlot(SKILLS, -1);
        player.addRPSlot(FEATS, -1);
        player.addRPSlot(ABILITIES, -1);
        player.addRPSlot(SPELLS, -1);
        player.addRPSlot(DEITIES, -1);
        player.addRPSlot(DOMAIN, -1);
        player.addRPSlot(KNOWN_SPELLS, -1);
        player.addRPSlot(PREP_SPELLS, -1);
    }

    @Override
    public void clientObjectUpdate(ClientObjectInterface client) {
        if (!((RPObject) client).has(EXP)) {
            ((RPObject) client).put(EXP, 0);
        }
        if (!((RPObject) client).has(INT)) {
            ((RPObject) client).put(INT, 0);
        }
        if (!((RPObject) client).has(CON)) {
            ((RPObject) client).put(CON, 0);
        }
        if (!((RPObject) client).has(DEX)) {
            ((RPObject) client).put(DEX, 0);
        }
        if (!((RPObject) client).has(STR)) {
            ((RPObject) client).put(STR, 0);
        }
        if (!((RPObject) client).has(WIS)) {
            ((RPObject) client).put(WIS, 0);
        }
        if (!((RPObject) client).has(CHA)) {
            ((RPObject) client).put(CHA, 0);
        }
        if (!((RPObject) client).has(AC)) {
            ((RPObject) client).put(AC, 0);
        }
    }
}
