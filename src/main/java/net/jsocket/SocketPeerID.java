package net.jsocket;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;
import java.util.UUID;

/**
 * Uniquely identifies a member of the socket communication
 */
public final class SocketPeerID implements Serializable {
    /**
     * Identifies the socket server
     */
    public static final SocketPeerID Server = new SocketPeerID(PeerIDType.Server);
    /**
     * Identifies a new client who does not yet have a clientID. Used as the recipient of a message containing newly-connected client's clientID
     *
     * @see ConversationReason#ServerPublicKey
     */
    public static final SocketPeerID NewClient = new SocketPeerID(PeerIDType.NewClient);
    /**
     * States that the recipient of this message is everyone. Used in a client-initiated broadcast message on its way to the server
     */
    public static final SocketPeerID Broadcast = new SocketPeerID(PeerIDType.Broadcast);
    /**
     * Used as a default value in DataCarrier
     */
    @SuppressWarnings("WeakerAccess")
    public static final SocketPeerID Default = new SocketPeerID(PeerIDType.Default);
    private final UUID peerID;
    private final PeerIDType peerIDType;

    private SocketPeerID(PeerIDType peerIDType) {
        this.peerID = null;
        this.peerIDType = peerIDType;
    }

    /**
     * Initialises this object with a UUID of the client
     *
     * @param peerID the unique identifier assigned by the server
     */
    public SocketPeerID(UUID peerID) {
        this.peerID = peerID;
        this.peerIDType = PeerIDType.IDSpecified;
    }

    /**
     * Gets this client's clientID
     *
     * @return UUID of this client
     */
    @Contract(pure = true)
    public UUID getPeerID() {
        return peerID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SocketPeerID socketPeerID = (SocketPeerID) o;
        return ((peerID == null) ? socketPeerID.peerID == null : peerID.equals(socketPeerID.peerID)) && peerIDType == ((SocketPeerID) o).peerIDType;
    }
}
