package dreamer.card.game;

import java.util.logging.Logger;

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
    private static final Logger LOG = Logger.getLogger(CardFilterExpr.class.getName());
}
