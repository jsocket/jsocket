package net.jsocket;

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
    void handle(DisconnectReason disconnectReason);
}
