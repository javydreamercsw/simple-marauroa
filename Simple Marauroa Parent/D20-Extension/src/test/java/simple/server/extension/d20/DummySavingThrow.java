package simple.server.extension.d20;

import simple.server.extension.d20.ability.D20Ability;
import simple.server.extension.d20.saving_throw.AbstractSavingThrow;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class DummySavingThrow extends AbstractSavingThrow {

    @Override
    public Class<? extends D20Ability> getAbility() {
        return DummyAbility.class;
    }

    @Override
    public String getCharacteristicName() {
        return "Dummy Save";
    }

    @Override
    public String getShortName() {
        return "Dummy Save";
    }
    
    @Override
    public String getDescription() {
        return "Dummy";
    }
}
