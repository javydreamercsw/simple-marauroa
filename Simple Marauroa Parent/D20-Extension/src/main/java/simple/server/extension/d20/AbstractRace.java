package simple.server.extension.d20;

import java.util.ArrayList;
import java.util.List;
import simple.server.extension.d20.race.D20Race;
import java.util.logging.Logger;
import org.openide.util.Lookup;
import simple.server.extension.d20.rpclass.D20Class;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public abstract class AbstractRace implements D20Race {

    protected List<Class<? extends D20Class>> prefferredCasses = new ArrayList<>();
    //Ability, Bonus
    private static final Logger LOG
            = Logger.getLogger(AbstractRace.class.getSimpleName());

    public AbstractRace() {
    }

    @Override
    public List<Class<? extends D20Class>> getFavoredClasses() {
        //Return all as default.
        if (prefferredCasses.isEmpty()) {
            Lookup.getDefault().lookupAll(D20Class.class).stream().forEach((clazz) -> {
                prefferredCasses.add(clazz.getClass());
            });
        }
        return prefferredCasses;
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getShortName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDescription() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
