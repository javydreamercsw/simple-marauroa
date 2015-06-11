package simple.server.extension.d20.alignment;

import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = D20Alignment.class)
public class Neutral_Good extends AbstractAlignment {

    @Override
    public String getShortName() {
        return "NG";
    }

    @Override
    public String getDescription() {
        return "A neutral good character does the best that a good person can "
                + "do. He is devoted to helping others. He works with kings "
                + "and magistrates but does not feel beholden to them.\n"
                + "\n"
                + "Neutral good is the best alignment you can be because it "
                + "means doing what "
                + "is good without bias for or against order.";
    }
}
