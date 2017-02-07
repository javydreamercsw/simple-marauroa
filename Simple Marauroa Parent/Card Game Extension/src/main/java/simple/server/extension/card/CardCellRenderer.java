package simple.server.extension.card;

import java.awt.Component;
import java.awt.Font;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Javier A. Ortiz <javier.ortiz.78@gmail.com>
 */
public class CardCellRenderer extends JLabel implements ListCellRenderer {

    private Font uhOhFont;

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        IMarauroaCard card = (IMarauroaCard) value;

        setIcon(card.getImages().get(0));
        if (getIcon() != null) {
            if (index != list.getModel().getSize() - 1) {
                setIcon(new ImageIcon(createImage(
                        new FilteredImageSource(((ImageIcon) getIcon())
                                .getImage().getSource(),
                                new CropImageFilter(0, 0, getIcon().getIconWidth(),
                                        20)))));
            }
            setFont(list.getFont());
        } else {
            setUhOhText(MessageFormat.format(
                    ResourceBundle.getBundle("simple/marauroa/client/extension/"
                            + "cardgame/Bundle").getString("no.image.available"),
                    new Object[]{String.valueOf(card.toString())}),
                    list.getFont());
        }
        return this;
    }
    //Set the font and text when no image was found.

    /**
     *
     * @param uhOhText
     * @param normalFont
     */
    protected void setUhOhText(String uhOhText, Font normalFont) {
        if (uhOhFont == null) { //lazily create this font
            uhOhFont = normalFont.deriveFont(Font.ITALIC);
        }
        setFont(uhOhFont);
        setText(uhOhText);
    }
}
