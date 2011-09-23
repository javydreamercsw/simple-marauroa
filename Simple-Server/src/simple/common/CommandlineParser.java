/*
 * $Rev$
 * $LastChangedDate$
 * $LastChangedBy$
 */
package simple.common;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;
import java.util.Vector;

/**
 * Generic command line parser considering quoted strings.
 *
 * @author Martin Fuchs
 */
public class CommandlineParser {

    protected final CharacterIterator ci;

    public CommandlineParser(String text) {
        ci = new StringCharacterIterator(text);
    }

    /**
     * Skip leading spaces.
     */
    public void skipWhitespace() {
        while (Character.isWhitespace(ci.current())) {
            ci.next();
        }
    }

    /**
     * Read next command line parameter considering quoting.
     *
     * @param errors
     * @return parameter
     */
    public String getNextParameter(ErrorDrain errors) {
        skipWhitespace();

        char ch = ci.current();

        StringBuilder sbuf = null;
        char quote = CharacterIterator.DONE;

        while (ch != CharacterIterator.DONE) {
            if (sbuf == null) {
                sbuf = new StringBuilder();
            }

            if (ch == quote) {
                // End of quote
                quote = CharacterIterator.DONE;
            } else if (quote != CharacterIterator.DONE) {
                // Quoted character
                sbuf.append(ch);
            } else if ((ch == '"') || (ch == '\'')) {
                // Start of quote
                quote = ch;
            } else if (Character.isWhitespace(ch)) {
                // End of token
                break;
            } else {
                // Token character
                sbuf.append(ch);
            }

            ch = ci.next();
        }

        /*
         * Unterminated quote?
         */
        if (quote != CharacterIterator.DONE) {
            errors.setError("Unterminated quote");
        }

        if (sbuf != null) {
            return sbuf.toString();
        } else {
            return null;
        }
    }

    /**
     * Read all remaining parameters into a String list.
     *
     * @param errors
     * @return parameter list
     */
    public List<String> readAllParameters(ErrorDrain errors) {
        List<String> params = new Vector<String>();

        do {
            skipWhitespace();

            String param = getNextParameter(errors);

            if (param == null) {
                break;
            }

            params.add(param);
        } while (!errors.hasError());

        return params;
    }
}
