package net.jsocket.server;

import net.jsocket.DataCarrier;
import net.jsocket.Handle;
import net.jsocket.SocketPeerID;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * The socket server object
 */
public final class Server implements Runnable {
    private ArrayList<ServerThread> clients = new ArrayList<>();
    private ServerSocket server = null;
    private Thread thread = null;
    private HashMap<String, Handle> handles;

    /**
     * Initialises this Server as standard socket server
     * @param port The port to listen on
     */
    public Server(int port) {
        handles = new HashMap<>();
        try {
            System.out.println("Binding to port " + port + ", please wait  ...");
            server = new ServerSocket(port);
            System.out.println("Server started: " + server);
            start();
        } catch (IOException e) {
            System.out.println("Can not bind to port " + port + ": " + e.getMessage());
        }
    }

    public void run() {
        while (thread != null) {
            try {
                System.out.println("Waiting for a desktop ...");
                addThread(server.accept());
            } catch (IOException e) {
                System.out.println("Server accept error: " + e);
                stop();
            }
        }
    }

    /**
     * Asynchronously start the server
     */
    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    /**
     * Asynchronously stops the server
     */
    public void stop() {
        if (thread != null) {
            thread.stop();
            thread = null;
        }
    }

    private ServerThread findClient(UUID ID) {
        for (ServerThread client : clients)
            if (client.getID() == ID)
                return client;
        return null;
    }

    private int clientPos(UUID ID) {
        for (int i = 0; i < clients.size(); i++)
            if (clients.get(i).getID() == ID)
                return i;
        return -1;
    }

    /**
     * Adds a message handler function
     * @param name The message name
     * @param handle THe function to be caller
     */
    public void addHandle(String name, Handle handle) {
        handles.put(name, handle);
    }

    synchronized void handle(DataCarrier data) {
        if (handles.containsKey(data.getName())) {
            handles.get(data.getName()).handle(data);
        }
    }

    /**
     * Disconnects a client
     * @param ID The clientID of the client to be disconnected
     */
    public synchronized void remove(UUID ID) {
        int pos = clientPos(ID);
        if (pos >= 0) {
            ServerThread toTerminate = clients.get(pos);
            System.out.println("Removing desktop thread " + ID + " at " + pos);
            if (pos < clients.size()) clients.remove(pos);
            try {
                toTerminate.close();
            } catch (IOException e) {
                System.out.println("Error closing thread: " + e);
            }
            toTerminate.stop();
        }
    }

    private void addThread(Socket socket) {
        System.out.println("Client accepted: " + socket);
        ServerThread thread = new ServerThread(this, socket);
        clients.add(thread);
        try {
            thread.open();
            thread.start();
        } catch (IOException e) {
            System.out.println("Error opening thread: " + e);
        }
    }

    /**
     * Sends a message to all connected clients
     * @param name The message name
     * @param sender The original message sender
     * @param data The message data
     */
    public void broadcast(String name, SocketPeerID sender, Serializable data) {
        for (ServerThread client : clients) client.send(
                new DataCarrier(
                        name,
                        DataCarrier.Direction.ToClient,
                        DataCarrier.ConversationOrigin.ClientBroadcast,
                        sender,
                        new SocketPeerID(client.getID()),
                        data));
    }
}
