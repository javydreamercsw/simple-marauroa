package simple.client;

import simple.common.NotificationType;

public class StandardEventLine extends EventLine {

    public StandardEventLine(final String text) {
        super("", text, NotificationType.NORMAL);
    }
}
