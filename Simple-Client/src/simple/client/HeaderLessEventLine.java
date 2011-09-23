package simple.client;

import simple.common.NotificationType;

public class HeaderLessEventLine extends EventLine {

    public HeaderLessEventLine(final String text, final NotificationType type) {
        super("", text, type);
    }
}
