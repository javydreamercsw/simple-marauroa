package simple.client.extension;

import marauroa.client.extension.MarauroaClientExtension;
import marauroa.common.game.RPEvent;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.client.entity.IUserContext;
import simple.client.event.listener.RPEventListener;
import simple.server.extension.ZoneEvent;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = MarauroaClientExtension.class)
public class ZoneClientExtension implements MarauroaClientExtension, RPEventListener {

    public ZoneClientExtension() {
        Lookup.getDefault().lookup(IUserContext.class).registerRPEventListener(new ZoneEvent(), ZoneClientExtension.this);
    }

    @Override
    public void onRPEventReceived(RPEvent event) throws Exception {
        System.out.println(ZoneClientExtension.class.getSimpleName() + ": Got event: " + event);
    }

    @Override
    public RPEvent processEvent(RPEvent event) {
        if (event.getName().equals(ZoneEvent.RPCLASS_NAME)) {
            ZoneEvent zoneEvent = new ZoneEvent();
            zoneEvent.fill(event);
            return zoneEvent;
        } else {
            return null;
        }
    }
}
