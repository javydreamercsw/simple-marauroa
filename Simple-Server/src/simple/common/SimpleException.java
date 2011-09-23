/*
 * $Rev$
 * $LastChangedDate$
 * $LastChangedBy$
 */
package simple.common;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class SimpleException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of <code>SimpleException</code> without detail message.
     */
    public SimpleException() {
    }

    /**
     * Constructs an instance of <code>SimpleException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public SimpleException(String msg) {
        super(msg);
    }
}
