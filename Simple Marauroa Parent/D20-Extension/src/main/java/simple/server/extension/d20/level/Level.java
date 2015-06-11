package simple.server.extension.d20.level;

import marauroa.common.game.Definition;
import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.d20.stat.AbstractStat;
import simple.server.extension.d20.stat.D20Stat;

@ServiceProvider(service = D20Stat.class)
public class Level extends AbstractStat implements D20Level {

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
        return getInt(D20Level.MAX);
    }

    @Override
    public void setMaxLevel(int max) {
        put(D20Level.MAX, max);
    }

    @Override
    public String getCharacteristicName() {
        return "Level";
    }

    @Override
    public String getShortName() {
        return "LVL";
    }

    @Override
    public String getDescription() {
        return "Level";
    }

    @Override
    public Definition.Type getDefinitionType() {
        return Definition.Type.INT;
    }
}
