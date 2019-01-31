package net.jsocket;

/**
 * The reason of this disconnect
 */
public enum DisconnectReason {
    /**
     * An error on the server caused the socket to be closed either for security reasons or because it became unstable
     */
    ServerError("A server error"),
    /**
     * Sent to all clients while the server prepares to stop
     */
    ServerShuttingDown("Server shutting down"),
    /**
     * An error on the client side caused the socket to be clised either for security reasons or because it became unstable
     */
    ClientError("A client error"),
    /**
     * Sent to the server before the client exits
     */
    ClientShuttingDown("Client shutting down"),
    /**
     * Sent to a client that was kicked from the server by the server-side software
     */
    ClientKicked("Client getting kicked"),
    /**
     * Used in logging when the socket is broken without being properly closed
     */
    NetworkError("A network error");

    private final String description;

    DisconnectReason(String description) {
        this.description = description;
    }

    /**
     * Gets the disconnect description
     * @return String specifying a description of the disconnect event
     */
    public String getDescription() {
        return description;
    }
}