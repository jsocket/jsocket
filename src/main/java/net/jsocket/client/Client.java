package net.jsocket.client;

import net.jsocket.*;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.UUID;

/**
 * The socket client class
 */
public class Client implements Constants {
    private Socket socket = null;
    private DataOutputStream streamOut = null;
    private ClientThread client = null;
    private HashMap<String, MessageHandle> messageHandles;
    private ClientDisconnectedHandle clientClosedHandle;
    private SecretKey symmetricKey;

    /**
     * The default constructor
     *
     * @param serverName         The hostname of the socket server
     * @param serverPort         The port of the socket server
     * @param clientInitialised  Handle that fires when client finishes initialising and receives clientID
     * @param clientClosedHandle Handle that fires when client disconnects from the server for any reason
     */
    public Client(String serverName, int serverPort, ClientConnectionHandle clientInitialised, ClientDisconnectedHandle clientClosedHandle) {
        System.out.println("Establishing connection. Please wait ...");
        messageHandles = new HashMap<>();
        this.clientClosedHandle = clientClosedHandle;
        messageHandles.put("disconnect", this::disconnectHandle);
        try {
            socket = new Socket(serverName, serverPort);
            System.out.println("Connected: " + socket);
            start(clientInitialised);
        } catch (UnknownHostException e) {
            System.out.println("Host unknown: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Unexpected exception: " + e.getMessage());
            System.exit(1);
        }
    }

    private void disconnectHandle(DataCarrier dataCarrier) {
        try (DisconnectMessage message = (DisconnectMessage) dataCarrier.getData()) {
            stop(message.getDisconnectReason());
        }
    }

    /**
     * Send a message to the server
     *
     * @param data The data to be sent
     * @throws SecurityException Thrown when a messageName that is used by the library or is blacklisted for any other reason is passed to be sent
     * @see DataCarrier
     */
    public void send(DataCarrier data) throws SecurityException {
        if (isHandleBlacklisted(data.getName()))
            throw new SecurityException("This message name is not allowed: " + data.getName());
        try {
            ObjectOutputStream output = new ObjectOutputStream(streamOut);
            output.writeObject(new EncryptedCarrier(data, symmetricKey));
            output.flush();
        } catch (IOException e) {
            System.out.println("Sending error: " + e.getMessage());
            stop();
        }
    }

    void sendSymmetricKey(DataCarrier dataCarrier) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(streamOut);
            out.writeObject(dataCarrier);
            out.flush();
        } catch (IOException e) {
            System.out.println("Sending error: " + e.getMessage());
            stop();
        }
    }

    /**
     * Broadcast a message to all connected clients
     *
     * @param name The name of the message
     * @param data The data to be sent
     */
    public void broadcast(String name, Message data) {
        send(new DataCarrier(name, Direction.ToServer, ConversationOrigin.ClientBroadcast, client.getSocketPeerID(), SocketPeerID.Broadcast, data));
    }

    /**
     * Add a handle function to this client
     *
     * @param eventName     The message name
     * @param messageHandle The handling function
     */
    public void addHandle(String eventName, MessageHandle messageHandle) {
        messageHandles.put(eventName, messageHandle);
    }

    void handle(DataCarrier data) {
        if (messageHandles.containsKey(data.getName())) {
            System.out.println("Handle found for name " + data.getName());
            messageHandles.get(data.getName()).handle(data);
        }
    }

    private void start(ClientConnectionHandle clientInitialised) throws IOException {
        streamOut = new DataOutputStream(socket.getOutputStream());
        if (client == null) {
            client = new ClientThread(this, socket, clientInitialised);
        }
    }

    /**
     * Stop this client
     */
    @SuppressWarnings("WeakerAccess")
    public void stop() {
        stop(DisconnectReason.ClientShuttingDown);
    }

    void stop(DisconnectReason disconnectReason) {
        try {
            if (streamOut != null) streamOut.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.out.println("Error closing ...");
        }
        client.close();
        clientClosedHandle.handle(disconnectReason);
    }

    /**
     * Gets the current clientID
     * @return UUID of this client
     */
    public UUID getClientID() {
        return client.getID();
    }

    void setSymmetricKey(SecretKey symmetricKey) {
        this.symmetricKey = symmetricKey;
    }
}
