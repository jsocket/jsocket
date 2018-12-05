package net.jsocket.server;

import net.jsocket.*;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.*;
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

    /**
     * The default constructor
     *
     * @param _server The managing server
     * @param _socket The socket connected to the client
     */
    public ServerThread(Server _server, Socket _socket) {
        server = _server;
        socket = _socket;
        ID = UUID.randomUUID();
        Thread thread = new Thread(this);
        try {
            streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            streamOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            thread.start();
        } catch (IOException e) {
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
            System.out.println(ID + " ERROR sending: " + e.getMessage());
            if (running) {
                server.remove(ID, DisconnectReason.ServerError);
                close(DisconnectReason.ServerError);
            }
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
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println(ID + " ERROR reading: " + e.getMessage());
            server.remove(ID, DisconnectReason.ServerError);
            close(DisconnectReason.NetworkError);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            close(DisconnectReason.ServerError);
        }

        do {
            try {
                ObjectInputStream input = new ObjectInputStream(streamIn);
                DataCarrier data = ((EncryptedCarrier) input.readObject()).getDataCarrier(symmetricKey);
                server.handle(data);
            } catch (IOException e) {
                System.out.println(ID + " ERROR reading: " + e.getMessage());
                server.remove(ID, DisconnectReason.ServerError);
                close(DisconnectReason.NetworkError);
            } catch (ClassNotFoundException e) {
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
            e.printStackTrace();
        }
    }
}
