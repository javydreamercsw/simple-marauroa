package simple.server.extension.d20.alignment;

import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = D20Alignment.class)
public class Chaotic_Neutral extends AbstractAlignment {

    @Override
    public String getDescription() {
        return "A chaotic neutral character follows his whims. He is an "
                + "individualist first and last. He values his own liberty "
                + "but doesn’t strive to protect others’ freedom. He avoids "
                + "authority, resents restrictions, and challenges traditions. "
                + "A chaotic neutral character does not intentionally disrupt "
                + "organizations as part of a campaign of anarchy. To do so, "
                + "he would have to be motivated either by good (and a desire "
                + "to liberate others) or evil (and a desire to make those "
                + "different from himself suffer). A chaotic neutral character"
                + " may be unpredictable, but his behavior is not totally "
                + "random. He is not as likely to jump off a bridge as to "
                + "cross it.\n"
                + "\n"
                + "Chaotic neutral is the best alignment you can be because "
                + "it represents true freedom from both society’s restrictions "
                + "and a do-gooder’s zeal.";
    }
}
