package net.jsocket.server;

import net.jsocket.*;

import javax.crypto.BadPaddingException;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * A thread that listens to one client
 */
@SuppressWarnings("WeakerAccess")
public class ServerThread implements Runnable, Constants {
    private Server server;
    private Socket socket;
    private UUID ID;
    private DataInputStream streamIn = null;
    private DataOutputStream streamOut = null;
    private volatile boolean running = true;
    private SecretKey symmetricKey;
    private ClientProperties clientProperties;
    private CreateClientProperties createClientProperties;

    /**
     * The default constructor
     *
     * @param _server The managing server
     * @param _socket The socket connected to the client
     */
    public ServerThread(Server _server, Socket _socket, CreateClientProperties createClientProperties) {
        server = _server;
        socket = _socket;
        ID = UUID.randomUUID();
        this.createClientProperties = createClientProperties;
        Thread thread = new Thread(this);
        try {
            streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            streamOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            thread.start();
        } catch (IOException e) {
            //TODO Exception handling
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to the client
     *
     * @param data The message to be sent
     * @throws SecurityException Thrown when a message wih an illegal name is sent
     */
    public void send(DataCarrier data) throws SecurityException {
        if (isHandleBlacklisted(data.getName()) && !(data.getName().equals("publicKey") && data.getConversationReason() == ConversationReason.ServerPublicKey))
            throw new SecurityException("This message name is not allowed: " + data.getName());
        try {
            ObjectOutputStream output = new ObjectOutputStream(streamOut);
            output.writeObject(new EncryptedCarrier(data, symmetricKey));
            output.flush();
        } catch (IOException e) {
            //TODO Exception handling
            System.out.println(ID + " ERROR sending: " + e.getMessage());
            if (running) {
                server.remove(ID, DisconnectReason.ServerError);
                close(DisconnectReason.ServerError);
            }
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the ID of the client
     *
     * @return UUID of the connected client
     */
    public UUID getID() {
        return ID;
    }

    public void run() {
        System.out.println("Server Thread " + ID + " running.");
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair keyPair = kpg.generateKeyPair();
            ObjectOutputStream output = new ObjectOutputStream(streamOut);
            output.writeObject(new DataCarrier("publicKey", Direction.ToClient, ConversationReason.ServerPublicKey, SocketPeerID.NewClient, new PublicKeyMessage(keyPair.getPublic(), new SocketPeerID(ID))));
            output.flush();
            boolean hasKey = false;
            do {
                ObjectInputStream input = new ObjectInputStream(streamIn);
                DataCarrier data = (DataCarrier) input.readObject();
                if (data.getData() instanceof KeyExchangeMessage) {
                    System.out.println("Got symmetric key");
                    symmetricKey = ((KeyExchangeMessage) data.getData()).getSymmetricKey(keyPair.getPrivate());
                    hasKey = true;
                }
            } while (!hasKey);
        } catch (NoSuchAlgorithmException e) {
            //TODO Exception handling
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println(ID + " ERROR reading: " + e.getMessage());
            server.remove(ID, DisconnectReason.ServerError);
            close(DisconnectReason.NetworkError);
        } catch (ClassNotFoundException | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
            close(DisconnectReason.ServerError);
        }

        clientProperties = createClientProperties.create(ID);

        do {
            try {
                ObjectInputStream input = new ObjectInputStream(streamIn);
                DataCarrier data = ((EncryptedCarrier) input.readObject()).getDataCarrier(symmetricKey);
                server.handle(data);
            } catch (IOException e) {
                //TODO Exception handling
                System.out.println(ID + " ERROR reading: " + e.getMessage());
                server.remove(ID, DisconnectReason.ServerError);
                close(DisconnectReason.NetworkError);
            } catch (ClassNotFoundException | BadPaddingException | InvalidKeyException e) {
                e.printStackTrace();
                close(DisconnectReason.ServerError);
            }
        } while (running);
    }

    /**
     * Closes the socket to the client
     * @param disconnectReason The {@link DisconnectReason DisconnectReason} why it happened
     */
    public void close(DisconnectReason disconnectReason) {
        running = false;
        send(new DataCarrier("disconnect", Direction.ToClient, ConversationOrigin.ServerToClient, SocketPeerID.Server, new SocketPeerID(ID), new DisconnectMessage(disconnectReason, "")));
        try {
            if (socket != null) socket.close();
            if (streamIn != null) streamIn.close();
            if (streamOut != null) streamOut.close();
        } catch (IOException e) {
            //TODO Exception handling
            e.printStackTrace();
        }
    }

    public ClientProperties getClientProperties() {
        return clientProperties;
    }

    public void setClientProperties(ClientProperties clientProperties) {
        this.clientProperties = clientProperties;
    }
}
