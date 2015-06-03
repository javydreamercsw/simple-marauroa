package com.reflexit.magiccards.core.model;

import java.util.List;
import javax.swing.ImageIcon;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public interface ICardWithImages extends ICard {

    /**
     * Get image representation of card. First one in the list is front and last
     * is back, but provide for multiple sided cards.
     *
     * @return list of images related to this page
     */
    public List<ImageIcon> getImages();
}
