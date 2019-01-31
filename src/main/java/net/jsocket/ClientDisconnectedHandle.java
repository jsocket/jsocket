package net.jsocket;

import java.util.UUID;

/**
 * Handles disconnected clients
 */
@FunctionalInterface
public interface ClientDisconnectedHandle {
    /**
     * The handle
     * @param disconnectReason Why did the client disconnect
     * @see DisconnectReason
     */
    void handle(UUID clientID, DisconnectReason disconnectReason);
}
