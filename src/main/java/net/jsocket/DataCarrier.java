package net.jsocket;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.UUID;

/**
 * Encapsulates the data, that is being sent between the server and its clients
 */
public class DataCarrier<TData extends Message> implements Serializable {
    private final UUID requestId;
    private final String name;
    private final Direction direction;
    private final ConversationOrigin conversationOrigin;
    private final ConversationReason conversationReason;
    private final SocketPeerID senderID;
    private final SocketPeerID recipientID;
    private final TData data;

    /**
     * User-invoked communication DataCarrier constructor
     *
     * @param name               The message name, should be the same for all communication of the same type.
     * @param direction          Indicates, which way will this data go.
     * @param conversationOrigin Describes the whole way this message should follow.
     * @param senderID           Specifies the clientID of the sender of this message.
     * @param recipientID        Specifies the clientID of the recipient of this message.<br> Can also be {@link net.jsocket.SocketPeerID#Server SocketPeerID.Server} or {@link net.jsocket.SocketPeerID#Broadcast SocketPeerID.Broadcast} in case of a broadcast message on its way from a client to the server.
     * @param data               The actual data of this message
     */
    public DataCarrier(String name, @NotNull Direction direction, ConversationOrigin conversationOrigin, SocketPeerID senderID, SocketPeerID recipientID, TData data) {
        this.requestId = UUID.randomUUID();
        this.name = name;
        this.direction = direction;
        this.conversationOrigin = conversationOrigin;
        this.conversationReason = ConversationReason.UserInvoked;
        this.senderID = senderID;
        this.recipientID = recipientID;
        this.data = data;
    }

    /**
     * Initialises this DataCarrier for other than common-use messages, always client and server communication, usually during the handshake
     *
     * @param name               The message name, should be the same for all communication of the same type.
     * @param direction          Indicates, which way will this data go.
     * @param conversationReason Indicates, why is this conversation happening
     * @param peerID             clientID of either the sending or receiving client, depending on direction
     * @param data               The actual data of this message
     */
    public DataCarrier(String name, @NotNull Direction direction, ConversationReason conversationReason, SocketPeerID peerID, TData data) {
        this.requestId = UUID.randomUUID();
        this.name = name;
        this.direction = direction;
        this.conversationOrigin = (direction == Direction.ToClient) ? ConversationOrigin.ServerToClient : ConversationOrigin.ClientToServer;
        this.conversationReason = conversationReason;
        switch (direction) {
            case ToClient:
                this.senderID = SocketPeerID.Server;
                this.recipientID = peerID;
                break;
            case ToServer:
                this.senderID = peerID;
                this.recipientID = SocketPeerID.Server;
                break;
            default:
                this.senderID = SocketPeerID.Default;
                this.recipientID = SocketPeerID.Default;
                break;
        }
        this.data = data;
    }

    /**
     * The message name.
     * It should be the same for all messages of the same type. It is used for finding the appropriate handle upon receiving the message.
     *
     * @return name of this message
     */
    public String getName() {
        return name;
    }

    /**
     * Indicates the direction this particular message should travel in.
     *
     * @return Direction of this message
     * @see Direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Describes the whole way this message should follow.
     *
     * @return ConversationOrigin of this message
     * @see ConversationOrigin
     */
    public ConversationOrigin getConversationOrigin() {
        return conversationOrigin;
    }

    /**
     * Indicates the reason this message was created.
     *
     * @return ConversationReason of this message
     * @see ConversationReason
     */
    public ConversationReason getConversationReason() {
        return conversationReason;
    }

    /**
     * Identifies the sender of this message.
     * If a client is the sender (ClientToServer, ClientToClient, ClientBroadcast) it should be the original sender clientID.
     * If a server is the sender (ServerToClient, ServerBroadcast) it should be the {@link SocketPeerID#Server SocketPeerID.Server} object.
     *
     * @return clientID of the message sender
     * @see SocketPeerID
     */
    public SocketPeerID getSenderID() {
        return senderID;
    }

    /**
     * Identifies the recipient of this message.
     * If this message is a broadcast going from a client to the socket server it should be S{@link SocketPeerID#Broadcast SocketPeerID.Broadcast}.
     * If this is a message meant just for the server (such as the greeting) it should be {@link SocketPeerID#Server SocketPeerID.Server}.
     * If this is a message going from the server (with any origin) to any client it should be the recipient's clientID.
     *
     * @return clientID of the message recipient
     * @see SocketPeerID
     */
    public SocketPeerID getRecipientID() {
        return recipientID;
    }

    /**
     * The actual data in this message.
     *
     * @return Object this message is carrying
     */
    public TData getData() {
        return data;
    }

    public Class<? extends Message> getDataType() {
        return data.getClass();
    }

    public UUID getRequestId() {
        return requestId;
    }

    public <T extends Message> ResponseDataCarrier<T> createResponse(T data){
        return new ResponseDataCarrier<>(this, data);
    }
}
