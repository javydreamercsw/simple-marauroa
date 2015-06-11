package com.reflexit.magiccards.core;

import java.io.IOException;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@SuppressWarnings("serial")
public class CachedImageNotFoundException extends IOException {

    /**
     * Constructor
     * @param s message
     */
    public CachedImageNotFoundException(final String s) {
        super(s);
    }
}
