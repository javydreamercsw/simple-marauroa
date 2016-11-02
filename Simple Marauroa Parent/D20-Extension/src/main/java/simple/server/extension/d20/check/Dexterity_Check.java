package simple.server.extension.d20.check;

import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.d20.ability.Dexterity;

@ServiceProvider(service = D20Check.class)
public class Dexterity_Check extends AbstractCheck {

    public Dexterity_Check() {
        getAbilities().add(Dexterity.class);
    }
}
