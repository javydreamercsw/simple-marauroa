package simple.server.extension;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz <javier.ortiz.78@gmail.com>
 */
public interface IStartingHandDeck extends IDeck {

    /**
     * Hand contents
     */
    List<ICard> hand = new ArrayList<ICard>();

    /**
     * Set the starting hand size
     *
     * @param size
     */
    public void setStartingHandSize(int size);

    /**
     * Get the starting hand size
     *
     * @return size of starting hand
     */
    public int getStartingHandSize();

    /**
     * Get hand.
     *
     * @return Cards in hand
     */
    public List<ICard> getHand();
}
