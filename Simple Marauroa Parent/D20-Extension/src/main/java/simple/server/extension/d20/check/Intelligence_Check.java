package simple.server.extension.d20.check;

import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.d20.ability.Intelligence;

@ServiceProvider(service = D20Check.class)
public class Intelligence_Check extends AbstractCheck {

    public Intelligence_Check() {
        getAbilities().add(Intelligence.class);
    }
}
