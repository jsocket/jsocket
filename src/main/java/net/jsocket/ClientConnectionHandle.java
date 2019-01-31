package net.jsocket;

import java.util.UUID;

/**
 * Handles new client connections
 */
@FunctionalInterface
public interface ClientConnectionHandle {
    /**
     * The handle
     * @param clientID New client's ID
     * @return Must return the new Client's ClientProperties to be set to the client object
     */
    void handle(UUID clientID);
}
