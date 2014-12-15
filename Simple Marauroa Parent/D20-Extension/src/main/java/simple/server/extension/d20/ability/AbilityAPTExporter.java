package simple.server.extension.d20.ability;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.d20.apt.IAPTExporter;

/**
 * this generates the apt files for abilities.
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = IAPTExporter.class)
public class AbilityAPTExporter implements IAPTExporter {

    private static final Logger LOG
            = Logger.getLogger(AbilityAPTExporter.class.getName());
    private final String block = " -----\n";

    @Override
    public void export(File root) throws IOException {
        if (root == null) {
            root = new File("src/site/apt");
        }
        root.mkdirs();
        File file = new File(root.getAbsolutePath()
                + System.getProperty("file.separator")
                + "Abilities.apt");
        StringBuilder sb = new StringBuilder();
        sb.append(block);
        sb.append("Abilities").append("\n");
        sb.append(block);
        sb.append("Javier A. Ortiz Bultrón").append("\n");
        sb.append(block).append("\n");
        sb.append("  The following are the available abilities:").append("\n");
        //Create a separate file for each Ability
        for (D20Ability a : Lookup.getDefault().lookupAll(D20Ability.class)) {
            File temp = new File(root.getAbsolutePath()
                    + System.getProperty("file.separator")
                    + "abilities"
                    + System.getProperty("file.separator")
                    + a.getName() + ".apt");
            temp.getParentFile().mkdirs();
            StringBuilder sb2 = new StringBuilder();
            sb2.append(block);
            sb2.append(a.getName()).append("\n");
            sb2.append(block);
            sb2.append("Javier A. Ortiz Bultrón").append("\n");
            sb2.append(block).append("\n");
            sb2.append(a.getName()).append("\n").append("\n");
            sb2.append("  ").append(a.getDescription()).append("\n");
            try (BufferedWriter output
                    = new BufferedWriter((new OutputStreamWriter(
                                    new FileOutputStream(temp), "UTF-8")))) {
                                output.write(sb2.toString());
                            } catch (IOException ex) {
                                LOG.log(Level.SEVERE, null, ex);
                            }
                            //Add link to the main page
                            sb.append(" * ").append("{{{./abilities/")
                                    .append(a.getName())
                                    .append(".html}")
                                    .append(a.getName())
                                    .append("}}")
                                    .append("\n");
        }
        try (BufferedWriter output
                = new BufferedWriter((new OutputStreamWriter(
                                new FileOutputStream(file), "UTF-8")))) {
                            output.write(sb.toString());
                        } catch (IOException ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
    }
}
