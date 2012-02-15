package simple.server.extension.card;

import java.util.Comparator;
import simple.server.extension.card.ICard;
import simple.server.extension.card.ICardField;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public interface ICardComparator extends Comparator {

    int compare(ICard c1, ICard c2);

    ICardField getField();

    boolean isAccending();
}
