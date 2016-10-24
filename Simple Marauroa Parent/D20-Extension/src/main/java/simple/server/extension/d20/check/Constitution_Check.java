package simple.server.extension.d20.check;

import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.d20.ability.Constitution;

@ServiceProvider(service = D20Check.class)
public class Constitution_Check extends AbstractCheck {

    public Constitution_Check() {
        getAbilities().add(Constitution.class);
    }
}
