package simple.server.extension;

import java.util.List;
import javax.swing.ImageIcon;
import org.openide.util.Lookup;

/**
 * Represents a card
 *
 * @author Javier A. Ortiz <javier.ortiz.78@gmail.com>
 */
public interface ICard extends Lookup.Provider, Comparable {

    /**
     * Get image representation of card. First one in the list is front and last
     * is back, but provide for multiple sided cards.
     *
     * @return list of images related to this page
     */
    public List<ImageIcon> getImages();
}
