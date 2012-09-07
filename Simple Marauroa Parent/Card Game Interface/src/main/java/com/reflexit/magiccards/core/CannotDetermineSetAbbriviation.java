package com.reflexit.magiccards.core;

import com.reflexit.magiccards.core.model.Editions;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class CannotDetermineSetAbbriviation extends Exception {

    private static final long serialVersionUID = 5548480990926987096L;

    /**
     * Constructor
     * @param set Editions.Edition set
     */
    public CannotDetermineSetAbbriviation(final Editions.Edition set) {
        super("Cannot determine set abbreviation for " + set.getName());
    }
    
    /**
     * Constructor
     * @param set Set name
     */
    public CannotDetermineSetAbbriviation(final String set) {
        super("Cannot determine set abbreviation for " + set);
    }
}
