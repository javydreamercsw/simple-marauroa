package simple.server.extension.d20.alignment;

import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = D20Alignment.class)
public class Chaotic_Good extends AbstractAlignment {

    @Override
    public String getShortName() {
        return "CG";
    }

    @Override
    public String getDescription() {
        return "A chaotic good character acts as his conscience directs him "
                + "with little regard for what others expect of him. He makes "
                + "his own way, but he’s kind and benevolent. He believes in "
                + "goodness and right but has little use for laws and "
                + "regulations. He hates it when people try to intimidate "
                + "others and tell them what to do. He follows his own moral "
                + "compass, which, although good, may not agree with that of "
                + "society.\n"
                + "\n"
                + "Chaotic good is the best alignment you can be because "
                + "it combines a good heart with a free spirit.";
    }
}
