package simple.server.extension.d20.alignment;

import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = D20Alignment.class)
public class Lawful_Neutral extends AbstractAlignment {

    @Override
    public String getShortName() {
        return "LN";
    }

    @Override
    public String getDescription() {
        return "A lawful neutral character acts as law, tradition, or a "
                + "personal code directs her. Order and organization are "
                + "paramount to her. She may believe in personal order and "
                + "live by a code or standard, or she may believe in order "
                + "for all and favor a strong, organized government.\n"
                + "\n"
                + "Lawful neutral is the best alignment you can be because "
                + "it means you are reliable and honorable without being a "
                + "zealot.";
    }
}
