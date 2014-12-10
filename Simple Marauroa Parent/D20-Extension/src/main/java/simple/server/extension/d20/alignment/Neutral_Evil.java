package simple.server.extension.d20.alignment;

import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = D20Alignment.class)
public class Neutral_Evil extends AbstractAlignment {

    @Override
    public String getDescription() {
        return "A neutral evil villain does whatever she can get away with. "
                + "She is out for herself, pure and simple. She sheds no "
                + "tears for those she kills, whether for profit, sport, "
                + "or convenience. She has no love of order and holds no "
                + "illusion that following laws, traditions, or codes would"
                + " make her any better or more noble. On the other hand, "
                + "she doesn’t have the restless nature or love of conflict "
                + "that a chaotic evil villain has.\n"
                + "\n"
                + "Some neutral evil villains hold up evil as an ideal, "
                + "committing evil for its own sake. Most often, such "
                + "villains are devoted to evil deities or secret societies.\n"
                + "\n"
                + "Neutral evil is the most dangerous alignment because "
                + "it represents pure evil without honor and without "
                + "variation.";
    }
}
