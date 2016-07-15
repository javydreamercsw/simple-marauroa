package simple.common;

import java.awt.Color;

/**
 * A logical notification type, which can be mapped to UI specific contexts.
 * This would be similar to logical styles vs. physical styles in HTML.
 */
public enum NotificationType {

    CLIENT("client") {

        @Override
        public Color getColor() {
            return COLOR_CLIENT;
        }
    },
    ERROR("error", true, false) {

        @Override
        public Color getColor() {
            return COLOR_ERROR;
        }
    },
    INFORMATION("information", false, true) {

        @Override
        public Color getColor() {
            return COLOR_INFORMATION;
        }
    },
    NEGATIVE("negative", true, false) {

        @Override
        public Color getColor() {
            return COLOR_NEGATIVE;
        }
    },
    NORMAL("normal") {

        @Override
        public Color getColor() {
            return COLOR_NORMAL;
        }
    },
    POSITIVE("positive", true, false) {

        @Override
        public Color getColor() {
            return COLOR_POSITIVE;
        }
    },
    PRIVMSG("privmsg", false, true) {

        @Override
        public Color getColor() {
            return COLOR_PRIVMSG;
        }
    },
    RESPONSE("response", true, true) {

        @Override
        public Color getColor() {
            return COLOR_RESPONSE;
        }
    },
    SIGNIFICANT_NEGATIVE("significant_negative", true, false) {

        @Override
        public Color getColor() {
            return COLOR_SIGNIFICANT_NEGATIVE;
        }
    },
    SIGNIFICANT_POSITIVE("significant_positive", true, false) {

        @Override
        public Color getColor() {
            return COLOR_SIGNIFICANT_POSITIVE;
        }
    },
    TUTORIAL("tutorial", false, true) {

        @Override
        public Color getColor() {
            return COLOR_TUTORIAL;
        }
    };
    /**
     * The mapping mnemonic.
     */
    protected String mnemonic;

    protected boolean bold = false, italic = false;

    /**
     * Create a notification type.
     *
     * @param mnemonic The mapping mnemonic.
     */
    private NotificationType(final String mnemonic) {
        this.mnemonic = mnemonic;
    }

    /**
     * Create a notification type.
     *
     * @param mnemonic The mapping mnemonic.
     * @param bold is the text bold
     * @param italic is the text italic
     */
    private NotificationType(final String mnemonic, boolean bold,
            boolean italic) {
        this.mnemonic = mnemonic;
        this.bold = bold;
        this.italic = italic;
    }

    /**
     * Get the mapping mnemonic (programatic name).
     *
     * @return The mapping mnemonic.
     */
    public String getMnemonic() {
        return mnemonic;
    }

    /**
     * Get the color that is tied to a notification type.
     *
     * @return The appropriate color.
     */
    public Color getColor() {
        return COLOR_NORMAL;
    }

    public boolean isBold() {
        return bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public static final Color COLOR_CLIENT = Color.gray;
    public static final Color COLOR_ERROR = Color.red;
    public static final Color COLOR_INFORMATION = Color.orange;
    public static final Color COLOR_NEGATIVE = Color.red;
    public static final Color COLOR_NORMAL = Color.black;
    public static final Color COLOR_POSITIVE = Color.green;
    public static final Color COLOR_PRIVMSG = Color.darkGray;
    public static final Color COLOR_RESPONSE = new Color(0x006400);
    public static final Color COLOR_SIGNIFICANT_NEGATIVE = Color.pink;
    public static final Color COLOR_SIGNIFICANT_POSITIVE = new Color(65,
            105, 225);
    public static final Color COLOR_TUTORIAL = new Color(172, 0, 172);
}
