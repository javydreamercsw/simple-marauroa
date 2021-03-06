package com.reflexit.magiccards.core.storage.database.controller.exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class IllegalOrphanException extends Exception {
    private List<String> messages;
    public IllegalOrphanException(List<String> messages) {
        super((messages != null && messages.size() > 0 ? messages.get(0) : null));
        if (messages == null) {
            this.messages = new ArrayList<String>();
        }
        else {
            this.messages = messages;
        }
    }
    public List<String> getMessages() {
        return messages;
    }
    private static final Logger LOG = Logger.getLogger(IllegalOrphanException.class.getName());
}
