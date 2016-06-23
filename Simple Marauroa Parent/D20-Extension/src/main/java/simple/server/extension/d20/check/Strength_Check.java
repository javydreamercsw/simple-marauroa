package simple.server.extension.d20.check;

import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.d20.ability.Strength;

@ServiceProvider(service = D20Check.class)
public class Strength_Check extends AbstractCheck {

    public Strength_Check() {
        getAbilities().add(Strength.class);
    }
}
