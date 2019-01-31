package net.jsocket.test;

import java.util.Date;

public class ChatItem {
    private final String username;
    private final String text;
    private final Date sent;

    public ChatItem(String username, String text, Date sent) {
        this.username = username;
        this.text = text;
        this.sent = sent;
    }

    public String getUsername() {
        return username;
    }

    public String getText() {
        return text;
    }

    public Date getSent() {
        return sent;
    }
}
