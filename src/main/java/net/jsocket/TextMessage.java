package net.jsocket;

import java.util.Date;

/**
 * Used as generic Text message with a timestamp of its creation
 *
 * @see DataCarrier
 */
public class TextMessage implements Message {
    private final String message;
    private final Date timestamp;

    /**
     * Initialises this object as a generic text-based message
     *
     * @param message The text of this message
     */
    public TextMessage(String message) {
        this.message = message;
        this.timestamp = new Date();
    }

    /**
     * Gets the message
     *
     * @return String of this message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the timestamp at which this message was created
     *
     * @return Date of creation of this message
     */
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String getDescription() {
        return "A text message saying: " + message + " sent at " + timestamp.toString();
    }
}
