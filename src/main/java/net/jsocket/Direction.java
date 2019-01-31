package net.jsocket;

/**
 * Used for message direction indication.
 */
public enum Direction {
    /**
     * Message that goes from client to socket server.
     */
    ToClient,
    /**
     * Message that goes from socket server to client.
     */
    ToServer
}
