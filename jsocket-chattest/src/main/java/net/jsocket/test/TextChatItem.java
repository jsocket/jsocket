package net.jsocket.test;

import java.util.Date;

public class TextChatItem implements ChatItem {
    private final String username;
    private final String text;
    private final Date sent;

    public TextChatItem(String username, String text, Date sent) {
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
