package net.jsocket;

import org.jetbrains.annotations.Contract;

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
    ServerBroadcast;

    @Contract(pure = true)
    public ConversationOrigin getResponseOrigin() {
        switch (this) {
            case ClientToClient:
                return ClientToClient;
            case ClientToServer:
                return ServerToClient;
            case ServerToClient:
                return ClientToServer;
            case ClientBroadcast:
                return ClientToClient;
            case ServerBroadcast:
                return ClientToServer;
            default:
                assert false;
                return valueOf("false");
        }
    }
}
