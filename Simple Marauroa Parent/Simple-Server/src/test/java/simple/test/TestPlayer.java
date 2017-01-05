package simple.test;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.server.game.db.AccountDAO;
import marauroa.server.game.db.DAORegister;
import static org.junit.Assert.*;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;
import simple.common.game.ClientObjectInterface;
import simple.server.core.engine.IRPWorld;
import simple.server.core.entity.RPEntityInterface;
import simple.server.core.entity.api.RPEventListener;
import simple.server.core.entity.clientobject.ClientObject;

/**
 *
 * @author Javier A. Ortiz BultrÃ³n javier.ortiz.78@gmail.com
 */
@ServiceProviders({
    @ServiceProvider(service = ClientObjectInterface.class)
    ,@ServiceProvider(service = RPEntityInterface.class)})
public class TestPlayer extends ClientObject {

    private static final Logger LOG
            = Logger.getLogger(TestPlayer.class.getName());
    private static final long serialVersionUID = -9018744674003715688L;
    private final IRPWorld world = Lookup.getDefault().lookup(IRPWorld.class);
    public static final String DEFAULT_RP_CLASSNAME = "test_player";

    public TestPlayer() {
    }

    @Override
    public void generateRPClass() {
        if (!RPClass.hasRPClass(DEFAULT_RP_CLASSNAME)) {
            RPClass player = new RPClass(DEFAULT_RP_CLASSNAME);
            player.isA(ClientObject.DEFAULT_RP_CLASSNAME);
        }
    }

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public TestPlayer(RPObject object, Map<String, RPEventListener> listeners) {
        super(object, listeners);
        RPCLASS_NAME = DEFAULT_RP_CLASSNAME;
        setRPClass(RPCLASS_NAME);
        addToWorld();
    }

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public TestPlayer(RPObject obj) {
        super(obj);
        RPCLASS_NAME = DEFAULT_RP_CLASSNAME;
        setRPClass(RPCLASS_NAME);
        addToWorld();
    }

    private void addToWorld() {
        try {
            //Create account
            if (getName() == null || getName().trim().isEmpty()) {
                //Assign a random name
                setName(UUID.randomUUID().toString());
            }
            DAORegister.get().get(AccountDAO.class).addPlayer(getName(),
                    "password".getBytes("UTF-8"), "dummy@email.com");
            //Add it to the world so it has an ID
            world.add(TestPlayer.this);
        } catch (SQLException | UnsupportedEncodingException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }
}
