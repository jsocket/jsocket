package net.jsocket.client;

import net.jsocket.DataCarrier;
import net.jsocket.Handle;
import net.jsocket.SocketPeerID;

import java.io.*;
import java.net.*;
import java.util.HashMap;

/**
 * The socket client class
 */
public class Client {
    private Socket socket = null;
    private DataOutputStream streamOut = null;
    private ClientThread client = null;
    private HashMap<String, Handle> handles;

    /**
     * The default constructor
     * @param serverName The hostname of the socket server
     * @param serverPort The port of the socket server
     */
    public Client(String serverName, int serverPort) {
        System.out.println("Establishing connection. Please wait ...");
        handles = new HashMap<>();
        try {
            socket = new Socket(serverName, serverPort);
            System.out.println("Connected: " + socket);
            start();
        } catch (UnknownHostException e) {
            System.out.println("Host unknown: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Unexpected exception: " + e.getMessage());
        }
    }

    /**
     * Send a message to the server
     * @param data The data to be sent
     * @see DataCarrier
     */
    public void send(DataCarrier data) {
        try {
            ObjectOutputStream output = new ObjectOutputStream(streamOut);
            output.writeObject(data);
            output.flush();
        } catch (IOException e) {
            System.out.println("Sending error: " + e.getMessage());
            stop();
        }
    }

    /**
     * Broadcast a message to all connected clients
     * @param name The name of the message
     * @param data The data to be sent
     */
    public void broadcast(String name, Serializable data) {
        send(new DataCarrier(name, DataCarrier.Direction.ToServer, DataCarrier.ConversationOrigin.ClientBroadcast, client.getSocketPeerID(), SocketPeerID.Broadcast, data));
    }

    /**
     * Add a handle function to this client
     * @param eventName The message name
     * @param handle The handling function
     */
    public void addHandle(String eventName, Handle handle) {
        handles.put(eventName, handle);
    }

    void handle(DataCarrier data) {
        if (handles.containsKey(data.getName())) {
            handles.get(data.getName()).handle(data);
        }
    }

    private void start() throws IOException {
        streamOut = new DataOutputStream(socket.getOutputStream());
        if (client == null) {
            client = new ClientThread(this, socket);
        }
    }

    /**
     * Stop this client
     */
    public void stop() {
        try {
            if (streamOut != null) streamOut.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.out.println("Error closing ...");
        }
        client.close();
        client.stop();
    }
}
