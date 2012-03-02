package com.reflexit.magiccards.core;

import com.reflexit.magiccards.core.model.Editions;

public class CannotDetermineSetAbbriviation extends Exception {

    private static final long serialVersionUID = 5548480990926987096L;

    public CannotDetermineSetAbbriviation(Editions.Edition set) {
        super("Cannot determine set abbreviation for " + set.getName());
    }
}
