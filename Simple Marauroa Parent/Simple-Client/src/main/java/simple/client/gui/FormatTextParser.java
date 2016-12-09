package simple.client.gui;

/**
 * Parsing of formated text to show highlighted expressions.
 *
 * @author Martin Fuchs
 */
public abstract class FormatTextParser {

    /**
     *
     * @param text
     * @throws java.lang.Exception
     */
    public void format(String text) throws Exception {
        String[] parts = text.split("#");
        int i = 0;
        for (String pieces : parts) {
            if (i > 0) {
                char terminator = ' ';

                // color quoted compound words like "#'iron sword'"
                if (pieces.charAt(0) == '\'') {
                    terminator = '\'';
                    pieces = pieces.substring(1);
                }

                int index = pieces.indexOf(terminator);
                if (index == -1) {
                    index = pieces.length();
                }
                colorText(pieces.substring(0, index));
                if (terminator == '\'' && index < pieces.length()) {
                    ++index;
                }
                pieces = pieces.substring(index);
            }
            normalText(pieces);
            ++i;
        }
    }

    /**
     *
     * @param txt
     * @throws java.lang.Exception
     */
    public abstract void normalText(String txt) throws Exception;

    /**
     *
     * @param txt
     * @throws java.lang.Exception
     */
    public abstract void colorText(String txt) throws Exception;
}
