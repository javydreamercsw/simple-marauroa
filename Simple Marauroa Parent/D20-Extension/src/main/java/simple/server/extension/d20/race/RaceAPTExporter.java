package simple.server.extension.d20.race;

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
import simple.server.extension.d20.apt.AbstractAPTExporter;

/**
 * this generates the apt files for abilities.
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = IAPTExporter.class)
public class RaceAPTExporter extends AbstractAPTExporter {

    private static final Logger LOG
            = Logger.getLogger(RaceAPTExporter.class.getName());

    @Override
    public void export(File root) throws IOException {
        if (root == null) {
            root = new File("src/site/apt");
        }
        root.mkdirs();
        File file = new File(root.getAbsolutePath()
                + System.getProperty("file.separator")
                + getFileName() + ".apt");
        StringBuilder sb = new StringBuilder();
        sb.append(BLOCK);
        sb.append(getFileName()).append("\n");
        sb.append(BLOCK);
        sb.append(getAuthor()).append("\n");
        sb.append(BLOCK).append("\n");
        sb.append("  The following are the available ")
                .append(getFileName().toLowerCase()).append(":")
                .append("\n").append("\n");
        //Create a separate file for each Ability
        for (D20Race a : Lookup.getDefault().lookupAll(D20Race.class)) {
            File temp = new File(root.getAbsolutePath()
                    + System.getProperty("file.separator")
                    + getFileName().toLowerCase()
                    + System.getProperty("file.separator")
                    + a.getCharacteristicName() + ".apt");
            temp.getParentFile().mkdirs();
            StringBuilder sb2 = new StringBuilder();
            sb2.append(BLOCK);
            sb2.append(a.getCharacteristicName()).append("\n");
            sb2.append(BLOCK);
            sb2.append(getAuthor()).append("\n");
            sb2.append(BLOCK).append("\n");
            sb2.append(a.getCharacteristicName()).append("\n").append("\n");
            sb2.append(INDENT).append(a.getDescription()).append("\n");
            StringBuilder sb3 = new StringBuilder();
            for (int i = 0; i <= 20; i++) {
                int bonus = a.getBonusFeatPoints(i);
                if (bonus > 0) {
                    sb3.append("Level ").append(i).append(": ").append(bonus);
                }
            }
            if (!sb3.toString().trim().isEmpty()) {
                sb2.append("Bonus Feat Points:").append("\n");
                sb2.append(sb3.toString()).append("\n");
            }
            sb3.setLength(0);
            for (int i = 0; i <= 20; i++) {
                int bonus = a.getBonusSkillPoints(i);
                if (bonus > 0) {
                    sb3.append("Level ").append(i).append(": ").append(bonus);
                }
            }
            if (!sb3.toString().trim().isEmpty()) {
                sb2.append("Bonus Skill Points:").append("\n");
                sb2.append(sb3.toString()).append("\n");
            }
            sb2.append("\n").append("Aging Effects:").append("\n").append("\n");
            sb2.append(LIST + "Middle Age: ")
                    .append(a.getMiddleAge()).append("\n").append("\n");
            sb2.append(LIST + "Old Age: ")
                    .append(a.getOldAge()).append("\n").append("\n");
            sb2.append(LIST + "Venerable Age: ")
                    .append(a.getVenerableAge()).append("\n").append("\n");
            sb2.append(LIST + "Maximum Age: ")
                    .append(a.getMaximumAge()).append("\n").append("\n");
            try (BufferedWriter output
                    = new BufferedWriter((new OutputStreamWriter(
                                    new FileOutputStream(temp), "UTF-8")))) {
                                output.write(sb2.toString());
                            } catch (IOException ex) {
                                LOG.log(Level.SEVERE, null, ex);
                            }
                            //Add link to the main page
                            sb.append("\n").append(LIST)
                                    .append("{{{./")
                                    .append(getFileName().toLowerCase())
                                    .append("/")
                                    .append(a.getCharacteristicName())
                                    .append(".html}")
                                    .append(a.getCharacteristicName())
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

    @Override
    public String getFileName() {
        return "Races";
    }
}
