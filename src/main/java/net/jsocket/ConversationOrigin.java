package net.jsocket;

/**
 * Indicates the direction of a message
 */
public enum ConversationOrigin {
    /**
     * Conversation from a client to another client
     */
    ClientToClient,
    /**
     * Conversation from a client to the server
     */
    ClientToServer,
    /**
     * Server-initiated conversation to a client
     */
    ServerToClient,
    /**
     * Broadcast message initiated by a client
     */
    ClientBroadcast,
    /**
     * Broadcast message initiated by the server
     */
    ServerBroadcast
}
