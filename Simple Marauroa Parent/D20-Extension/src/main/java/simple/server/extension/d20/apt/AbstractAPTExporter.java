package simple.server.extension.d20.apt;

import simple.server.extension.d20.apt.IAPTExporter;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public abstract class AbstractAPTExporter implements IAPTExporter {

    public static final String BLOCK = " -----\n";
    public static final String INDENT = "  ";
    public static final String LIST = INDENT + INDENT + "* ";
    private String AUTHOR = "Javier A. Ortiz Bultrón";

    /**
     * @param AUTHOR the AUTHOR to set
     */
    public void setAuthor(String AUTHOR) {
        this.AUTHOR = AUTHOR;
    }

    /**
     * @return the AUTHOR
     */
    public String getAuthor() {
        return AUTHOR;
    }

    /**
     * THe name of the menu file without extension.
     *
     * @return
     */
    public abstract String getFileName();
}
