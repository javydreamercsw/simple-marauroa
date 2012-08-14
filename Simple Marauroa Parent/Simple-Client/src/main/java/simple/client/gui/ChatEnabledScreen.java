package simple.client.gui;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.Date;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import simple.client.EventLine;
import simple.common.NotificationType;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public abstract class ChatEnabledScreen extends javax.swing.JFrame {

    private JTextPane tp;
    protected static final int TEXT_SIZE = 11;
    protected static final Color HEADER_COLOR = Color.gray;
    /** the logger instance. */
    private static final Logger logger = Log4J.getLogger(ChatEnabledScreen.class);

    /**
     * The implemented method.
     *
     * @param header
     *            a string with the header name
     * @param line
     *            a string representing the line to be printed
     * @param type
     *            The logical format type.
     */
    public void addLine(String header, String line,
            NotificationType type) {
        // Determine whether the scrollbar is currently at the very bottom
        // position. We will only auto-scroll down if the user is not currently
        // reading old texts (like IRC clients do).
        final JScrollBar vbar = getChatScrollPane().getVerticalScrollBar();

        insertNewline();

        java.text.Format formatter = new java.text.SimpleDateFormat("[HH:mm] ");
        String dateString = formatter.format(new Date());
        insertTimestamp(dateString);

        insertHeader(header);
        insertText(line, type);

        if (getCurrentTextPane().getDocument().getLength() > 20000) {
            try {
                getCurrentTextPane().getDocument().remove(0, 100);
            } catch (BadLocationException e) {
                logger.info(e);
            }
        }
        getCurrentTextPane().setCaretPosition(getCurrentTextPane().getDocument().getLength());
    }

    public abstract JScrollPane getChatScrollPane();

    /**
     * This is used to switch where the messages will be written to
     * @param tp
     */
    public void setCurrentTextPane(JTextPane tp) {
        this.tp = tp;
        applyStyle();
    }

    /**
     * Get the current TextPane
     * @return JTextPane
     */
    public JTextPane getCurrentTextPane() {
        return tp;
    }

    /**
     * React to key presses
     * @param e
     */
    public abstract void onKeyPressed(KeyEvent e);

    /**
     * React to key release
     * @param e
     */
    public abstract void onKeyReleased(KeyEvent e);

    /**
     * Request game quit
     */
    public abstract void requestQuit();

    /**
     * Insert a header.
     * @param header
     */
    protected void insertHeader(String header) {
        Document doc = getCurrentTextPane().getDocument();
        try {
            if (header.length() > 0) {
                doc.insertString(doc.getLength(), "<" + header + "> ",
                        getCurrentTextPane().getStyle("header"));
            }
        } catch (BadLocationException ble) {
            logger.error("Couldn't insert initial text.", ble);
        } catch (Exception e) {
            logger.error("Couldn't insert initial text.", e);
        }
    }

    /**
     * Insert time stamp
     * @param header
     */
    protected void insertTimestamp(String header) {
        Document doc = getCurrentTextPane().getDocument();
        try {
            if (header.length() > 0) {
                doc.insertString(doc.getLength(), header,
                        getCurrentTextPane().getStyle("timestamp"));
            }
        } catch (BadLocationException ble) {
            logger.error("Couldn't insert initial text.");
        }
    }

    /**
     * Insert text
     * @param text
     * @param type
     */
    protected void insertText(String text, NotificationType type) {
        final Color color = type.getColor();
        final Document doc = getCurrentTextPane().getDocument();

        try {
            FormatTextParser parser = new FormatTextParser() {

                @Override
                public void normalText(String txt) throws BadLocationException {
                    if (!txt.contains(" ")) {
                        doc.insertString(doc.getLength(), txt, getColor(color));
                    } else {
                        String[] parts = txt.split(" ");
                        for (String pieces : parts) {
                            doc.insertString(doc.getLength(), pieces + " ", getColor(color));
                        }
                    }
                }

                @Override
                public void colorText(String txt) throws BadLocationException {
                    doc.insertString(doc.getLength(), txt, getCurrentTextPane().getStyle("bold"));
                }
            };
            parser.format(text);
        } catch (Exception ble) { // BadLocationException
            logger.error(ble);
        }
    }

    /**
     * Insert new line
     */
    protected void insertNewline() {
        Document doc = getCurrentTextPane().getDocument();
        try {
            doc.insertString(doc.getLength(), "\r\n", getColor(Color.black));
        } catch (BadLocationException ble) {
            logger.error("Couldn't insert initial text. " + ble);
        }
    }

    public void addLine(final EventLine line) {
        addLine(line.getHeader(), line.getText(), line.getType());
    }

    /**
     * Add a line of text
     * @param header
     * @param line
     */
    public void addLine(String header, String line) {
        addLine(header, line, NotificationType.NORMAL);
    }

    /**
     * Add a line of text
     * @param line
     * @param type
     */
    public void addLine(String line, NotificationType type) {
        addLine("", line, type);
    }

    private void scrollToBottom() {
        // This didn't scroll all the way down. :(
        // textPane.setCaretPosition(textPane.getDocument().getLength());

        final JScrollBar vbar = getChatScrollPane().getVerticalScrollBar();

        try {
            // We need to wait because we must not print further lines
            // before we have scrolled down.
            SwingUtilities.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    vbar.setValue(vbar.getMaximum());
                }
            });
        } catch (Exception e) {
            logger.error(e, e);
        }
    }

    private void applyStyle() {
        Style def = StyleContext.getDefaultStyleContext().getStyle(
                StyleContext.DEFAULT_STYLE);

        Style regular = getCurrentTextPane().addStyle("regular", def);
        StyleConstants.setFontFamily(def, "Dialog");
        StyleConstants.setFontSize(regular, TEXT_SIZE);

        Style s = getCurrentTextPane().addStyle("normal", regular);
        StyleConstants.setBold(s, true);
        StyleConstants.setForeground(s, HEADER_COLOR);

        s = getCurrentTextPane().addStyle("bold", regular);
        StyleConstants.setFontSize(regular, TEXT_SIZE + 1);
        StyleConstants.setItalic(s, true);
        StyleConstants.setBold(s, true);
        StyleConstants.setForeground(s, Color.blue);

        s = getCurrentTextPane().addStyle("header", regular);
        StyleConstants.setItalic(s, true);
        StyleConstants.setFontSize(s, TEXT_SIZE);
        StyleConstants.setForeground(s, HEADER_COLOR);

        s = getCurrentTextPane().addStyle("timestamp", regular);
        StyleConstants.setItalic(s, true);
        StyleConstants.setFontSize(s, TEXT_SIZE - 1);
        StyleConstants.setForeground(s, HEADER_COLOR);
    }

    /**
     * @param desiredColor
     *            the color with which the text must be colored
     * @return the colored style
     */
    public Style getColor(Color desiredColor) {
        Style s = getCurrentTextPane().getStyle("normal");
        StyleConstants.setForeground(s, desiredColor);
        return s;
    }
}
