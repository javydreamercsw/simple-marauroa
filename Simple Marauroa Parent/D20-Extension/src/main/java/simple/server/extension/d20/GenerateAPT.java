package simple.server.extension.d20;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Lookup;
import simple.server.extension.d20.apt.IAPTExporter;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class GenerateAPT {

    private static final Logger LOG
            = Logger.getLogger(GenerateAPT.class.getName());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Lookup.getDefault().lookupAll(IAPTExporter.class).stream()
                .forEach((e) -> {
                    try {
                        LOG.log(Level.INFO, "Processing: {0}",
                                e.getClass().getSimpleName());
                        e.export(null);
                    } catch (IOException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                });
    }
}
