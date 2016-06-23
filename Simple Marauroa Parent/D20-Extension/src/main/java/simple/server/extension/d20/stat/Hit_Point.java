package simple.server.extension.d20.stat;

import marauroa.common.game.Definition;
import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.d20.apt.AbstractAPTExporter;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = D20Stat.class)
public class Hit_Point extends AbstractStat {

    public static final String HP
            = Hit_Point.class.getSimpleName().replaceAll("_", " ");

    @Override
    public int getStatMod() {
        return 0;
    }

    @Override
    public int getDefaultValue() {
        return 0;
    }

    @Override
    public String getCharacteristicName() {
        return HP;
    }

    @Override
    public String getShortName() {
        return "HP";
    }

    @Override
    public Definition.Type getDefinitionType() {
        return Definition.Type.INT;
    }

    @Override
    public String getDescription() {
        return "Hit points mean two things in the game world: the ability to "
                + "take physical punishment and keep going, and the ability "
                + "to turn a serious blow into a less serious one.\n\n"
                + AbstractAPTExporter.INDENT
                + "When your current hit points drop to exactly 0, "
                + "you're disabled.\n\n"
                + AbstractAPTExporter.INDENT
                + "You can only take a single move or standard action each "
                + "turn (but not both, nor can you take full-round actions).";
    }

    @Override
    public Byte getDefinition() {
        return Definition.STANDARD;
    }
}
