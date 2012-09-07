package com.reflexit.magiccards.core.model;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public abstract class CardFilterExpr {

    /**
     * Is translated?
     */
    private boolean translated = false;

    /**
     * Evaluate against filter.
     *
     * @param o Object to evaluate
     * @return true if acceptable
     */
    public abstract boolean evaluate(Object o);

    /**
     * Get field value
     * @param o field to get value from.
     * @return value or null if field not defined.
     */
    public Object getFieldValue(ICardField o) {
        return null;
    }

    /**
     * Is translated?
     *
     * @return the translated
     */
    public boolean isTranslated() {
        return translated;
    }

    /**
     * Set translated.
     *
     * @param translated the translated to set
     */
    public void setTranslated(boolean translated) {
        this.translated = translated;
    }
}
