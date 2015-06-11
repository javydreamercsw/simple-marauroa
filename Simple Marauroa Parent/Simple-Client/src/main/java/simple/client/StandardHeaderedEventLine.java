package simple.client;

import simple.common.NotificationType;

public class StandardHeaderedEventLine extends EventLine {

    public StandardHeaderedEventLine(final String header, final String text) {
        super(header, text, NotificationType.NORMAL);
    }
}
