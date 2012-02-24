package dreamer.card.game;

import java.util.List;
import javax.swing.ImageIcon;

/**
 * Represents a card
 *
 * @author Javier A. Ortiz <javier.ortiz.78@gmail.com>
 */
public interface ICard extends Comparable {

    /**
     * Get image representation of card. First one in the list is front and last
     * is back, but provide for multiple sided cards.
     *
     * @return list of images related to this page
     */
    public List<ImageIcon> getImages();

    /**
     * Page set
     *
     * @return Set name
     */
    public String getSet();

    /**
     * Card name
     *
     * @return Card name
     */
    public String getName();

    /**
     * Get object by field
     *
     * @param field
     * @return Object matching criteria
     */
    Object getObjectByField(ICardField field);

    public int getCardId();
}
