package simple.server.extension.d20;

import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.MarauroaServerExtension;
import simple.server.extension.SimpleServerExtension;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = MarauroaServerExtension.class)
public class D20Extension extends SimpleServerExtension {

    @Override
    public String getName() {
        return "D20 Extension";
    }
}
