package dreamer.card.game;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class CardFilterExpr {

    private boolean translated = false;

    public boolean evaluate(Object o) {
        return false;
    }

    public Object getFieldValue(Object o) {
        return null;
    }

    /**
     * @return the translated
     */
    public boolean isTranslated() {
        return translated;
    }

    /**
     * @param translated the translated to set
     */
    public void setTranslated(boolean translated) {
        this.translated = translated;
    }
}
