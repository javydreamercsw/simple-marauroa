package simple.server.extension.d20.check;

import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.d20.ability.Dexterity;

@ServiceProvider(service = D20Check.class)
public class Initiative_Check extends AbstractCheck {

    public Initiative_Check() {
        getAbilities().add(Dexterity.class);
    }

    @Override
    public String getDescription() {
        return "Chance of starting combat in control.";
    }
}
