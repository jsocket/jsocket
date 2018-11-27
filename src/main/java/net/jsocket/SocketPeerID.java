package net.jsocket;

import org.jetbrains.annotations.Contract;

import java.util.UUID;

/**
 * Uniquely identifies a member of the socket communication
 */
public final class SocketPeerID {
    /**
     * Identifies the socket server
     */
    public static final SocketPeerID Server = new SocketPeerID(PeerIDType.Server);
    /**
     * Identifies a new client who does not yet have a clientID. Used as the recipient of a message containing newly-connected client's clientID
     * @see net.jsocket.DataCarrier.ConversationReason#ClientIDMessage
     */
    public static final SocketPeerID NewClient = new SocketPeerID(PeerIDType.NewClient);
    /**
     * States that the recipient of this message is everyone. Used in a client-initiated broadcast message on its way to the server
     */
    public static final SocketPeerID Broadcast = new SocketPeerID(PeerIDType.Broadcast);
    private final UUID senderID;
    private final PeerIDType peerIDType;

    private SocketPeerID(PeerIDType peerIDType) {
        this.senderID = null;
        this.peerIDType = peerIDType;
    }

    /**
     * Initialises this object with a UUID of the client
     * @param senderID the unique identifier assigned by the server
     */
    public SocketPeerID(UUID senderID) {
        this.senderID = senderID;
        this.peerIDType = PeerIDType.IDSpecified;
    }

    /**
     * Gets this client's clientID
     * @return UUID of this client
     */
    @Contract(pure = true)
    public UUID getSenderID() {
        return senderID;
    }

    private enum PeerIDType {
        Server, NewClient, Broadcast, IDSpecified
    }
}
