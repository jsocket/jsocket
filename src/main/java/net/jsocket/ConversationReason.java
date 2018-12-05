package net.jsocket;

/**
 * Indicates the usage of a message
 */
public enum ConversationReason {
    /**
     * Used for any general message
     */
    UserInvoked,
    /**
     * Used by the library to tell a newly-connected client its clientID and to send the one-time server public key
     */
    ServerPublicKey,
    /**
     * Used by the library when the client generates a symmetric key used to encrypt the message payload and wants to share it securely with the server
     */
    SymmetricKey
}
