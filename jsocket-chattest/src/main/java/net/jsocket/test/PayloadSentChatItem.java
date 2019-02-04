package net.jsocket.test;

import net.jsocket.Message;

import java.util.Date;
import java.util.UUID;

public class PayloadSentChatItem implements Message, ChatItem {
    private final UUID senderID;
    private final String username;
    private final int size;
    private final Date sent;
    private final boolean successful;

    public PayloadSentChatItem(UUID senderID, String username, int size, boolean successful) {
        this.senderID = senderID;
        this.username = username;
        this.size = size;
        this.sent = new Date();
        this.successful = successful;
    }

    public UUID getSenderID() {
        return senderID;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public int getSize() {
        return size;
    }

    @Override
    public Date getSent() {
        return sent;
    }

    public boolean isSuccessful() {
        return successful;
    }

    @Override
    public String getDescription() {
        return "PayloadSent info";
    }
}
