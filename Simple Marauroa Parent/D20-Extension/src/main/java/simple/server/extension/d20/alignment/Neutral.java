package simple.server.extension.d20.alignment;

import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = D20Alignment.class)
public class Neutral extends AbstractAlignment {

    @Override
    public String getShortName() {
        return "N";
    }

    @Override
    public String getDescription() {
        return "A neutral character does what seems to be a good idea. "
                + "She doesn’t feel strongly one way or the other when it "
                + "comes to good vs. evil or law vs. chaos. Most neutral "
                + "characters exhibit a lack of conviction or bias rather "
                + "than a commitment to neutrality. Such a character thinks "
                + "of good as better than evil—after all, she would rather "
                + "have good neighbors and rulers than evil ones. Still, "
                + "she’s not personally committed to upholding good in any "
                + "abstract or universal way.\n"
                + "\n"
                + "Some neutral characters, on the other hand, commit "
                + "themselves philosophically to neutrality. They see good, "
                + "evil, law, and chaos as prejudices and dangerous extremes. "
                + "They advocate the middle way of neutrality as the best, "
                + "most balanced road in the long run.\n"
                + "\n"
                + "Neutral is the best alignment you can be because it means "
                + "you act naturally, without prejudice or compulsion.";
    }
}
