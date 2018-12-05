package net.jsocket.server;

import net.jsocket.*;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * The socket server object
 */
public final class Server implements Runnable {
    private ArrayList<ServerThread> clients = new ArrayList<>();
    private ServerSocket server = null;
    private Thread thread = null;
    private HashMap<String, MessageHandle> messageHandles;
    private ArrayList<ClientConnectionHandle> newConnectionHandles;
    private ArrayList<ClientConnectionHandle> clientDisconnectedHandles;
    private Timer keepAliveTimer = new Timer();
    private volatile boolean shouldRun = false;
    private volatile boolean running = false;
    private final int port;

    /**
     * Initialises this Server as standard socket server
     *
     * @param port The port to listen on
     */
    public Server(int port) {
        messageHandles = new HashMap<>();
        this.port = port;
        this.newConnectionHandles = new ArrayList<>();
        this.clientDisconnectedHandles = new ArrayList<>();
        try {
            System.out.println("Binding to port " + port + ", please wait  ...");
            server = new ServerSocket(port);
            System.out.println("Server started: " + server);
            start();
        } catch (IOException e) {
            System.out.println("Can not bind to port " + port + ": " + e.getMessage());
        }
    }

    /**
     * Gets the port this server listens on
     * @return Integer of this port
     */
    public int getPort() {
        return port;
    }

    /**
     * Specifies whether the server is running
     * @return boolean if the server is running
     */
    public boolean isRunning() {
        return running;
    }

    public void run() {
        running = true;
        while (shouldRun) {
            try {
                System.out.println("Waiting for a desktop ...");
                addThread(server.accept());
            } catch (IOException e) {
                System.out.println("Server accept error: " + e);
                stop();
            }
        }
        running = false;
        thread = null;
    }

    private void start() {
        if (thread == null) {
            thread = new Thread(this);
            shouldRun = true;
            thread.start();
        }
    }

    private void stop() {
        if (thread != null && running) {
            shouldRun = false;
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
     *
     * @param name   The message name
     * @param messageHandle THe function to be caller
     */
    public void addHandle(String name, MessageHandle messageHandle) {
        messageHandles.put(name, messageHandle);
    }

    /**
     * Changes the current newConnectionHandle
     * @param newConnectionHandle The method to be called when new client connects
     */
    public void setNewConnectionHandle(ClientConnectionHandle newConnectionHandle) {
        this.newConnectionHandles.add(newConnectionHandle);
    }

    /**
     * Changes the current clientDisconnectedHandle
     * @param clientDisconnectedHandle The method to be called when a client disconnects
     */
    public void setClientDisconnectedHandle(ClientConnectionHandle clientDisconnectedHandle) {
        this.clientDisconnectedHandles.add(clientDisconnectedHandle);
    }

    synchronized void handle(DataCarrier data) {
        System.out.println("Handling message name " + data.getName());
        System.out.println(data.getData());
        if (messageHandles.containsKey(data.getName())) {
            messageHandles.get(data.getName()).handle(data);
        }
    }

    /**
     * Disconnects a client
     *
     * @param ID The clientID of the client to be disconnected
     * @param disconnectReason The {@link DisconnectReason DisconnectReason} why it happened
     */
    public synchronized void remove(UUID ID, DisconnectReason disconnectReason) {
        int pos = clientPos(ID);
        if (pos >= 0) {
            ServerThread toTerminate = clients.get(pos);
            System.out.println("Removing desktop thread " + ID + " at " + pos);
            if (pos < clients.size()) clients.remove(pos);
            toTerminate.close(disconnectReason);
        }
        for (ClientConnectionHandle handle : clientDisconnectedHandles) handle.handle(ID);
    }

    private void addThread(Socket socket) {
        System.out.println("Client accepted: " + socket);
        ServerThread thread = new ServerThread(this, socket);
        clients.add(thread);
        for (ClientConnectionHandle handle : newConnectionHandles) handle.handle(thread.getID());
    }

    /**
     * Sends a message to all connected clients
     *
     * @param name   The message name
     * @param sender The original message sender
     * @param data   The message data
     */
    public void broadcast(String name, SocketPeerID sender, Message data) {
        System.out.println("Broadcasting message "+data.getDescription());
        for (ServerThread client : clients)
            if (client.getID() != sender.getPeerID())
                client.send(new DataCarrier(name, Direction.ToClient, ConversationOrigin.ClientBroadcast, sender, new SocketPeerID(client.getID()), data));
    }
}
