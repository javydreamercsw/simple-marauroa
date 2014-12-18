package simple.server.extension.d20.feat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Modifier;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.d20.D20Characteristic;
import simple.server.extension.d20.apt.IAPTExporter;

/**
 * This generates the apt files for Feats.
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = IAPTExporter.class)
public class FeatAPTExporter extends AbstractAPTExporter {

    private static final Logger LOG
            = Logger.getLogger(FeatAPTExporter.class.getName());

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
        //Create a separate file for each Feat
        for (D20Feat a : Lookup.getDefault().lookupAll(D20Feat.class)) {
            File temp = new File(root.getAbsolutePath()
                    + System.getProperty("file.separator")
                    + getFileName().toLowerCase().replaceAll(" ", "_")
                    + System.getProperty("file.separator")
                    + a.getName().replaceAll(" ", "_") + ".apt");
            temp.getParentFile().mkdirs();
            LOG.log(Level.INFO, "Processing: {0}", a.getName());
            StringBuilder sb2 = new StringBuilder();
            sb2.append(BLOCK);
            sb2.append(a.getName()).append("\n");
            sb2.append(BLOCK);
            sb2.append(getAuthor()).append("\n");
            sb2.append(BLOCK).append("\n");
            sb2.append(a.getName()).append("\n").append("\n");
            sb2.append(INDENT).append(a.getDescription()).append("\n");
            sb2.append("\n");
            if (a.getFocusCharacteristic() != null) {
                sb2.append(INDENT).append("Characteristic: ")
                        .append(a.getFocusCharacteristic().getName()).append("\n");
            }
            if (a.getFocusWeapon() != null) {
                sb2.append(INDENT).append("Focus: ")
                        .append(a.getFocusWeapon().getName()).append("\n");
            }
            if (a.getRequirements().size() > 0) {
                sb2.append("Requirements:").append("\n");
                a.getRequirements().stream().forEach((f) -> {
                    try {
                        D20Feat feat = f.newInstance();
                        sb2.append(INDENT + INDENT + "* ")
                                .append(feat.getName())
                                .append("\n");
                    } catch (InstantiationException | IllegalAccessException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                });
            }
            if (!a.getBonuses().isEmpty()) {
                sb2.append("\n").append("Bonuses:").append("\n").append("\n");
                for (Entry<Class<? extends D20Characteristic>, String> entry
                        : a.getBonuses().entrySet()) {
                    if (!Modifier.isAbstract(a.getClass().getModifiers())) {
                        try {
                            sb2.append(INDENT + INDENT + "* ")
                                    .append(entry.getKey().newInstance().getName())
                                    .append(": ")
                                    .append(entry.getValue())
                                    .append("\n")
                                    .append("\n");
                        } catch (InstantiationException | IllegalAccessException ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
            sb2.append("\n").append("Miscellaneous:").append("\n").append("\n");
            sb2.append(INDENT + "Multiple instances? ")
                    .append(a.isMultiple() ? "Yes" : "No").append("\n");
            try (BufferedWriter output
                    = new BufferedWriter((new OutputStreamWriter(
                                    new FileOutputStream(temp), "UTF-8")))) {
                                output.write(sb2.toString());
                            } catch (IOException ex) {
                                LOG.log(Level.SEVERE, null, ex);
                            }
                            //Add link to the main page
                            sb.append("\n").append(INDENT + INDENT + "* ")
                                    .append("{{{./")
                                    .append(getFileName().toLowerCase())
                                    .append("/")
                                    .append(a.getName().replaceAll(" ", "_"))
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

    @Override
    public String getFileName() {
        return "Feats";
    }
}
