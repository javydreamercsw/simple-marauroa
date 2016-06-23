package simple.server.extension.d20.check;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.Attributes;
import simple.server.extension.d20.ability.D20Ability;
import simple.server.extension.d20.dice.DieRoll;
import simple.server.extension.d20.dice.RollResult;
import simple.server.extension.d20.rpclass.AbstractClass;
import simple.server.extension.d20.rpclass.D20Class;

public abstract class AbstractCheck implements D20Check {

    protected List<Class<? extends D20Ability>> abilities = new ArrayList<>();
    private final static Logger LOG
            = Logger.getLogger(AbstractCheck.class.getSimpleName());

    @Override
    public final List<Class<? extends D20Ability>> getAbilities() {
        return abilities;
    }

    @Override
    public RollResult getCheckRoll(D20Class clazz) {
        //By default just rool a d20 and add the abilities modifiers.
        int mod = 0;
        for (Class<? extends D20Ability> a : abilities) {
            try {
                D20Ability ability = a.newInstance();
                if (((Attributes) clazz).has(ability.getCharacteristicName())) {
                    int temp = ((AbstractClass) clazz).getAbilityModifier(a);
                    LOG.log(Level.INFO, "Adding modifier from: {0} = {1}",
                            new Object[]{ability.getCharacteristicName(), temp});
                    mod += temp;
                }
            } catch (InstantiationException | IllegalAccessException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        return new DieRoll(1, dieType(), mod).makeRoll();
    }

    @Override
    public int dieType() {
        return 20;
    }

    @Override
    public String getCharacteristicName() {
        return getClass().getSimpleName().replaceAll("_", " ");
    }

    @Override
    public String getShortName() {
        return getClass().getSimpleName().substring(0, 3).toUpperCase()
                + "Check";
    }

    @Override
    public String getDescription() {
        return getClass().getSimpleName().replaceAll("_", " ");
    }
}
