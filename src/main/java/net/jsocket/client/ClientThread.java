package net.jsocket.client;

import net.jsocket.DataCarrier;
import net.jsocket.SocketPeerID;

import java.io.*;
import java.net.*;
import java.util.UUID;

/**
 * This thread is responsible for listening to any messages coming in from the server
 */
public class ClientThread extends Thread {
    private Socket socket;
    private Client client;
    private DataInputStream streamIn = null;
    private SocketPeerID ID;

    /**
     * The default constructor
     * @param _client The managing client
     * @param _socket The socket to the server
     */
    public ClientThread(Client _client, Socket _socket) {
        client = _client;
        socket = _socket;
        open();
        start();
    }

    /**
     * Open the socket
     */
    public void open() {
        try {
            streamIn = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error getting input stream: " + e);
            client.stop();
        }
    }

    /**
     * Close the socket
     */
    public void close() {
        try {
            if (streamIn != null) streamIn.close();
        } catch (IOException e) {
            System.out.println("Error closing input stream: " + e);
        }
    }

    /**
     * Get the clientID of this client
     * @return UUID of this client
     */
    public UUID getID() {
        return ID.getSenderID();
    }

    SocketPeerID getSocketPeerID() {
        return ID;
    }

    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                ObjectInputStream input = new ObjectInputStream(streamIn);
                DataCarrier data = (DataCarrier) input.readObject();
                if (data.getRecipientID() == SocketPeerID.NewClient && data.getConversationReason() == DataCarrier.ConversationReason.ClientIDMessage) {
                    ID = new SocketPeerID((UUID) data.getData());
                } else {
                    client.handle(data);
                }
            } catch (IOException e) {
                System.out.println("Listening error: " + e.getMessage());
                client.stop();
            } catch (ClassNotFoundException | ClassCastException e) {
                e.printStackTrace();
            }
        }
    }
}
