package simple.server.extension.d20.alignment;

import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = D20Alignment.class)
public class Chaotic_Evil extends AbstractAlignment {

    @Override
    public String getShortName() {
        return "CE";
    }

    @Override
    public String getDescription() {
        return "A chaotic evil character does whatever his greed, hatred, "
                + "and lust for destruction drive him to do. He is "
                + "hot-tempered, vicious, arbitrarily violent, and "
                + "unpredictable. If he is simply out for whatever he can "
                + "get, he is ruthless and brutal. If he is committed to "
                + "the spread of evil and chaos, he is even worse. "
                + "Thankfully, his plans are haphazard, and any groups "
                + "he joins or forms are poorly organized. Typically, "
                + "chaotic evil people can be made to work together only "
                + "by force, and their leader lasts only as long as he can "
                + "thwart attempts to topple or assassinate him.\n"
                + "\n"
                + "Chaotic evil is sometimes called \"demonic\" because "
                + "demons are the epitome of chaotic evil.\n"
                + "\n"
                + "Chaotic evil is the most dangerous alignment because "
                + "it represents the destruction not only of beauty and "
                + "life but also of the order on which beauty and life depend.";
    }
}
