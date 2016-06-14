package simple.server.extension.d20.check;

import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.d20.ability.Wisdom;

@ServiceProvider(service = D20Check.class)
public class Wisdom_Check extends AbstractCheck {

    public Wisdom_Check() {
        getAbilities().add(Wisdom.class);
    }
}
