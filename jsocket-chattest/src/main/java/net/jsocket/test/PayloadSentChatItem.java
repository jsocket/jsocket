package net.jsocket.test;

import net.jsocket.Message;

import java.util.Date;
import java.util.UUID;

public class PayloadSentChatItem implements Message {
    private final UUID senderID;
    private final int size;
    private final Date timestamp;
    private final boolean successful;

    public PayloadSentChatItem(UUID senderID, int size, boolean successful) {
        this.senderID = senderID;
        this.size = size;
        this.timestamp = new Date();
        this.successful = successful;
    }

    public UUID getSenderID() {
        return senderID;
    }

    public int getSize() {
        return size;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public boolean isSuccessful() {
        return successful;
    }

    @Override
    public String getDescription() {
        return "PayloadSent info";
    }
}
