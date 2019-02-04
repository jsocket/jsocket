package net.jsocket.client;

import net.jsocket.*;

import javax.crypto.BadPaddingException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * This thread is responsible for listening to any messages coming in from the server
 */
@SuppressWarnings("WeakerAccess")
public class ClientThread implements Runnable {
    private Socket socket;
    private Client client;
    private DataInputStream streamIn = null;
    private SocketPeerID ID;
    private volatile boolean running = true;
    private ClientConnectionHandle clientInitialised;
    private SecretKey symmetricKey;

    /**
     * The default constructor
     *
     * @param _client           The managing client
     * @param _socket           The socket to the server
     * @param clientInitialised Fires when the client establishes connection to the server and receives a clientID
     */
    public ClientThread(Client _client, Socket _socket, ClientConnectionHandle clientInitialised) {
        client = _client;
        socket = _socket;
        open();
        Thread thread = new Thread(this);
        thread.start();
        this.clientInitialised = clientInitialised;
    }

    private void open() {
        try {
            streamIn = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            //TODO Exception handling
            System.out.println("Error getting input stream: " + e);
            client.stop(DisconnectReason.ClientError);
        }
    }

    void close() {
        running = false;
        try {
            if (streamIn != null) streamIn.close();
        } catch (IOException e) {
            // we don't care
        }
    }

    /**
     * Get the clientID of this client
     *
     * @return UUID of this client
     */
    public UUID getID() {
        return ID.getPeerID();
    }

    /**
     * Gets the clientID of this client
     * @return SocketPeerID of this client
     */
    public SocketPeerID getSocketPeerID() {
        return ID;
    }

    public void run() {
        boolean hasKey = false;
        do {
            try {
                ObjectInputStream input = new ObjectInputStream(streamIn);
                DataCarrier data = (DataCarrier) input.readObject();
                if (data.getData() instanceof PublicKeyMessage) {
                    System.out.println("Got a publicKey");
                    symmetricKey = KeyGenerator.getInstance("AES").generateKey();
                    client.setSymmetricKey(symmetricKey);
                    PublicKeyMessage pkm = (PublicKeyMessage) data.getData();
                    ID = pkm.getNewClientID();
                    client.sendSymmetricKey(new DataCarrier<>("symmetricKey", Direction.ToServer, ConversationReason.SymmetricKey, ID, new KeyExchangeMessage(symmetricKey, pkm.getPublicKey())));
                    hasKey = true;
                }
            } catch (IOException e) {
                //TODO Exception handling
                System.out.println("Client Listening error: " + e.getMessage());
                client.stop(DisconnectReason.NetworkError);
            } catch (ClassNotFoundException | ClassCastException | NoSuchAlgorithmException | BadPaddingException e) {
                e.printStackTrace();
            }
        } while (!hasKey);

        clientInitialised.handle(ID.getPeerID());

        do {
            try {
                ObjectInputStream input = new ObjectInputStream(streamIn);
                DataCarrier data = ((EncryptedCarrier) input.readObject()).getDataCarrier(symmetricKey);
                System.out.println("Just got a new object: " + data.getData().getDescription());
                client.handle(data);
            } catch (IOException e) {
                //TODO Exception handling
                System.out.println("Listening error: " + e.getMessage());
                client.stop(DisconnectReason.NetworkError);
            } catch (ClassNotFoundException | ClassCastException | BadPaddingException | InvalidKeyException e) {
                e.printStackTrace();
            }
        } while (running);
    }
}
