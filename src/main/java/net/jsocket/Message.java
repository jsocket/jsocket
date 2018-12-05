package net.jsocket;

import java.io.Serializable;

/**
 * General interface used by all messages sent via a DataCarrier
 */
public interface Message extends Serializable {
    /**
     * Gets a description of the message being transported. It can be a textual representation of the data being sent or just a general use description
     * @return String of the description
     */
    String getDescription();
}
