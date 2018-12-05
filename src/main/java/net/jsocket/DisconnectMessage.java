package net.jsocket;

import java.util.Date;

/**
 * A message sent when a client disconnects.<br>
 * Can be sent by a client as a request to disconnect or by the server to announce that the client was disconnected on the server side and the socket was closed
 */
public class DisconnectMessage implements Message, AutoCloseable {
    private final DisconnectReason disconnectReason;
    private final String comment;
    private final Date timestamp;

    /**
     * Initialises this DisconnectMessage for common use
     * @param disconnectReason The reason of the client disconnecting
     * @param comment Any message to be transmitted (kick reason, client close reason...)
     */
    public DisconnectMessage(DisconnectReason disconnectReason, String comment) {
        this.disconnectReason = disconnectReason;
        this.comment = comment;
        this.timestamp = new Date();
    }

    /**
     * Gets the DisconnectReason of this message
     * @return DisconnectReason of this message
     */
    public DisconnectReason getDisconnectReason() {
        return disconnectReason;
    }

    /**
     * Gets the comment of the disconnect
     * @return String containing the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * Gets the timestamp of when this message was sent
     * @return Date of sending
     */
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public void close() {

    }

    @Override
    public String getDescription() {
        return "Client disconnected at " + timestamp.toString() + " due to " + disconnectReason.getDescription() + " (" + comment + ")";
    }
}
