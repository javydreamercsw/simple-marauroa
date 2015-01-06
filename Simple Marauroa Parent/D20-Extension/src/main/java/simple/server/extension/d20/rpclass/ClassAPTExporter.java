package simple.server.extension.d20.rpclass;

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
import simple.server.extension.d20.feat.D20Feat;
import simple.server.extension.d20.skill.D20Skill;

/**
 * This generates the apt files for Feats.
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = IAPTExporter.class)
public class ClassAPTExporter extends AbstractAPTExporter {

    private static final Logger LOG
            = Logger.getLogger(ClassAPTExporter.class.getName());

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
        for (D20Class a : Lookup.getDefault().lookupAll(D20Class.class)) {
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
            sb2.append(INDENT).append(a.getDescription()).append("\n")
                    .append("\n");
            sb2.append(INDENT).append("HP Dice: ").append(a.getHPDice())
                    .append("\n");
            sb2.append("\n");
            if (!a.getAttributeBonuses().isEmpty()) {
                sb2.append("Attribute Bonuses:").append("\n");
                sb2.append("\n");
                a.getAttributeBonuses().entrySet().stream().forEach((entry) -> {
                    try {
                        D20Ability ability = entry.getKey().newInstance();
                        sb2.append("\n").append(INDENT + INDENT + "* ")
                                .append(ability.getName()).append(": ")
                                .append(entry.getValue()).append("\n");
                    } catch (InstantiationException | IllegalAccessException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                });
            }
            sb2.append("\n");
            if (!a.getBonusFeats().isEmpty()) {
                sb2.append("Bonus Feats:").append("\n");
                sb2.append("\n");
                a.getBonusFeats().entrySet().stream().forEach((entry) -> {
                    try {
                        D20Feat feat = entry.getKey().newInstance();
                        sb2.append("\n").append(INDENT + INDENT + "* ")
                                .append("{{{../feats/")
                                .append(feat.getName().replaceAll(" ", "_"))
                                .append(".html}")
                                .append(feat.getName())
                                .append("}}").append(": ")
                                .append(" at Level ")
                                .append(entry.getValue()).append("\n");
                    } catch (InstantiationException | IllegalAccessException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                });
            }
            sb2.append("\n");
            if (!a.getBonusSkills().isEmpty()) {
                sb2.append("Bonus Skills:").append("\n");
                sb2.append("\n");
                a.getBonusSkills().entrySet().stream().forEach((entry) -> {
                    try {
                        D20Skill skill = entry.getKey().newInstance();
                        sb2.append("\n").append(INDENT + INDENT + "* ")
                                .append("{{{../skills/")
                                .append(skill.getName().replaceAll(" ", "_"))
                                .append(".html}")
                                .append(skill.getName())
                                .append("}}").append(": ")
                                .append(" at Level ")
                                .append(entry.getValue()).append("\n");
                    } catch (InstantiationException | IllegalAccessException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                });
            }
            sb2.append("\n");
            if (!a.getPrefferedFeats().isEmpty()) {
                sb2.append("Preferred Feats:").append("\n");
                sb2.append("\n");
                a.getPrefferedFeats().stream().forEach((f) -> {
                    try {
                        D20Feat feat = f.newInstance();
                        sb2.append("\n").append(INDENT + INDENT + "* ")
                                .append("{{{../feats/")
                                .append(feat.getName().replaceAll(" ", "_"))
                                .append(".html}")
                                .append(feat.getName())
                                .append("}}").append("\n");
                    } catch (InstantiationException | IllegalAccessException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                });
            }
            sb2.append("\n");
            if (!a.getPrefferedSkills().isEmpty()) {
                sb2.append("Preferred Skills:").append("\n");
                sb2.append("\n");
                a.getPrefferedSkills().stream().forEach((f) -> {
                    try {
                        D20Skill skill = f.newInstance();
                        sb2.append("\n").append(INDENT + INDENT + "* ")
                                .append("{{{../skills/")
                                .append(skill.getName().replaceAll(" ", "_"))
                                .append(".html}")
                                .append(skill.getName())
                                .append("}}").append("\n");
                    } catch (InstantiationException | IllegalAccessException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                });
            }
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
        return "Classes";
    }
}
