package simple.server.extension.d20.skill;

import simple.server.extension.d20.apt.AbstractAPTExporter;
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
 * This generates the apt files for Skills.
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
                        + a.getCharacteristicName().replaceAll(" ", "_") + ".apt");
                temp.getParentFile().mkdirs();
                LOG.log(Level.FINE, "Processing: {0}", a.getCharacteristicName());
                StringBuilder sb2 = new StringBuilder();
                sb2.append(BLOCK);
                sb2.append(a.getCharacteristicName()).append("\n");
                sb2.append(BLOCK);
                sb2.append(getAuthor()).append("\n");
                sb2.append(BLOCK).append("\n");
                sb2.append(a.getCharacteristicName()).append("\n").append("\n");
                sb2.append(INDENT).append(a.getDescription()).append("\n");
                sb2.append("\n");
                sb2.append(INDENT).append("Related ability: ")
                        .append(a.getAbility().newInstance().getCharacteristicName())
                        .append("\n").append("\n");
                sb2.append(INDENT).append("Modified Abilities: ")
                        .append("\n").append("\n");
                int count = 0;
                count = Lookup.getDefault().lookupAll(D20Ability.class).stream().filter((ability) -> (a.getModifier(ability.getClass()) > 0)).map((ability) -> {
                    sb2.append(INDENT + INDENT + "* ")
                            .append(ability.getCharacteristicName()).append(": ")
                            .append(a.getModifier(ability.getClass()))
                            .append("\n").append("\n");
                    return ability;
                }).map((_item) -> 1).reduce(count, Integer::sum);
                if (count == 0) {
                    sb2.append(INDENT + INDENT + "* ").append("None")
                            .append("\n").append("\n");
                }
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
                                        .append(a.getCharacteristicName().replaceAll(" ", "_"))
                                        .append(".html}")
                                        .append(a.getCharacteristicName())
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
