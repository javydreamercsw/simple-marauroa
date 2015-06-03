package com.reflexit.magiccards.core.model;

import javax.swing.JLabel;

/**
 * This provides a factory of images for the game
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public interface IGameCellRendererImageFactory {

    /**
     * Column the value is in
     *
     * @param column Column the value is in
     * @param value Value to convert
     * @return JLabel with the rendering
     */
    public JLabel getRendering(String column, Object value);
}
