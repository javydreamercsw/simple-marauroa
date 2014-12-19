package simple.server.extension.d20.skill;

import simple.server.extension.d20.AbstractAPTExporter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.d20.ability.D20Ability;
import simple.server.extension.d20.apt.IAPTExporter;

/**
 * This generates the apt files for Feats.
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = IAPTExporter.class)
public class SkillAPTExporter extends AbstractAPTExporter {

    private static final Logger LOG
            = Logger.getLogger(SkillAPTExporter.class.getName());

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
        sb.append(INDENT + "The following are the available ")
                .append(getFileName().toLowerCase()).append(":")
                .append("\n").append("\n");
        //Create a separate file for each Feat
        for (D20Skill a : Lookup.getDefault().lookupAll(D20Skill.class)) {
            try {
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
                sb2.append(INDENT).append("Related ability: ")
                        .append(a.getAbility().newInstance().getName())
                        .append("\n").append("\n");
                sb2.append("Modified Abilities: ").append("\n");
                Lookup.getDefault()
                        .lookupAll(D20Ability.class).stream().forEach((ability) -> {
                            sb2.append(INDENT + INDENT + "* ")
                            .append(ability.getName()).append(": ")
                            .append(a.getModifier(ability.getClass()))
                            .append("\n").append("\n");
                        });
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
            } catch (InstantiationException | IllegalAccessException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
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
        return "Skills";
    }
}
