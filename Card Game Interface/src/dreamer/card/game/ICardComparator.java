package dreamer.card.game;

import java.util.Comparator;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public interface ICardComparator extends Comparator {

    int compare(ICard c1, ICard c2);

    ICardField getField();

    boolean isAccending();
}
