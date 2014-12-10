package simple.server.extension.d20.alignment;

import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = D20Alignment.class)
public class Lawful_Good extends AbstractAlignment {

    @Override
    public String getShortName() {
        return "LG";
    }

    @Override
    public String getDescription() {
        return "A lawful good character acts as a good person is expected or "
                + "required to act. She combines a commitment to oppose evil "
                + "with the discipline to fight relentlessly. She tells the "
                + "truth, keeps her word, helps those in need, and speaks out "
                + "against injustice. A lawful good character hates to see the "
                + "guilty go unpunished.\n"
                + "\n"
                + "Lawful good is the best alignment you can be because it "
                + "combines honor and compassion.";
    }
}
