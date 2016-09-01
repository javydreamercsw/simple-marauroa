package simple.server.extension.d20.check;

import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.d20.ability.Charisma;

@ServiceProvider(service = D20Check.class)
public class Charisma_Check extends AbstractCheck {

    public Charisma_Check() {
        getAbilities().add(Charisma.class);
    }
}
