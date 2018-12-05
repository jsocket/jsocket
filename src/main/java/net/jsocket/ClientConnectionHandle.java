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
     */
    void handle(UUID clientID);
}
