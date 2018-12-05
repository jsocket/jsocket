package net.jsocket;

import java.security.PublicKey;

/**
 * Used to transport the server's public key and the client's new clientID from the server. This is the only message that should go thwough the socket unencrypted
 */
public class PublicKeyMessage implements Message {
    private final PublicKey publicKey;
    private final SocketPeerID newClientID;

    /**
     * Initialises this PublicKeyMessage for general use
     * @param publicKey The server's RSA public key, should be a new key for each client
     * @param newClientID The client's new clientID
     */
    public PublicKeyMessage(PublicKey publicKey, SocketPeerID newClientID){
        this.publicKey = publicKey;
        this.newClientID = newClientID;
    }

    /**
     * Gets the server's public key
     * @return PublicKey of the server's keypair
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * Gets the client's new clientID
     * @return UUID of the client that was assigned to him
     */
    public SocketPeerID getNewClientID() {
        return newClientID;
    }

    @Override
    public String getDescription() {
        return "Public key message";
    }
}
