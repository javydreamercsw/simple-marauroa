package simple.server.extension;

import simple.server.extension.ability.D20Ability;
import simple.server.extension.saving_throw.AbstractSavingThrow;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class DummySavingThrow extends AbstractSavingThrow {

    @Override
    public Class<? extends D20Ability> getAbility() {
        return DummyAbility.class;
    }

    @Override
    public String getName() {
        return "Dummy Save";
    }

    @Override
    public String getShortName() {
        return "Dummy Save";
    }
}
