package net.jsocket.server;

import net.jsocket.*;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * The socket server object
 */
public final class Server<ClientProp extends ClientProperties> implements Runnable {
    private ArrayList<ServerThread<ClientProp>> clients = new ArrayList<>();
    private ServerSocket server = null;
    private Thread thread = null;
    private HashMap<String, ServerMessageHandle<ClientProp, ? extends Message>> messageHandles;
    private ClientConnectionHandle newConnectionHandle;
    private ClientDisconnectedHandle clientDisconnectedHandle;
    private CreateClientProperties<ClientProp> createClientProperties;
    private Timer keepAliveTimer = new Timer();
    private volatile boolean shouldRun = false;
    private volatile boolean running = false;
    private final int port;

    /**
     * Initialises this Server as standard socket server
     *
     * @param port The port to listen on
     */
    public Server(int port, CreateClientProperties<ClientProp> createClientProperties) {
        messageHandles = new HashMap<>();
        this.createClientProperties = createClientProperties;
        this.port = port;
        try {
            //TODO Exception handling
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
     *
     * @return Integer of this port
     */
    public int getPort() {
        return port;
    }

    /**
     * Specifies whether the server is running
     *
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
                //TODO Exception handling
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
     * @param name                The message name
     * @param clientMessageHandle THe function to be caller
     */
    public <TData extends Message> void addHandle(String name, ServerMessageHandle<ClientProp, TData> clientMessageHandle) {
        messageHandles.put(name, clientMessageHandle);
    }

    synchronized <TData extends Message> void handle(ServerThread<ClientProp> sender, DataCarrier<TData> data) {
        System.out.println("Handling message name " + data.getName());
        System.out.println(data.getData());
        ServerMessageHandle<ClientProp, ? extends Message> handle = messageHandles.get(data.getName());
        if (messageHandles.containsKey(data.getName())) {
            ((ServerMessageHandle<ClientProp, TData>) messageHandles.get(data.getName())).handle(sender, data);
        }
    }

    /**
     * Disconnects a client
     *
     * @param ID               The clientID of the client to be disconnected
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
        if (clientDisconnectedHandle != null) clientDisconnectedHandle.handle(ID, disconnectReason);
    }

    private void addThread(Socket socket) {
        System.out.println("Client accepted: " + socket);
        ServerThread<ClientProp> thread = new ServerThread<>(this, socket, createClientProperties);
        clients.add(thread);
        if (newConnectionHandle != null) newConnectionHandle.handle(thread.getID());
    }

    public void setNewConnectionHandle(ClientConnectionHandle newConnectionHandle) {
        this.newConnectionHandle = newConnectionHandle;
    }

    public void setClientDisconnectedHandle(ClientDisconnectedHandle clientDisconnectedHandle) {
        this.clientDisconnectedHandle = clientDisconnectedHandle;
    }

    /**
     * Sends a message to all connected clients
     *
     * @param name           The message name
     * @param sender         The original message sender
     * @param data           The message data
     * @param returnToSender Should the broadcast message be sent back to the broadcasting client?
     */
    public void broadcast(String name, SocketPeerID sender, Message data, boolean returnToSender) {
        System.out.println("Broadcasting message " + data.getDescription());
        for (ServerThread client : clients)
            if (!client.getID().equals(sender.getPeerID()) || returnToSender)
                client.send(new DataCarrier<>(name, Direction.ToClient, ConversationOrigin.ClientBroadcast, sender, new SocketPeerID(client.getID()), data));
    }

    public ClientProp getClientProperties(UUID clientId) {
        for (ServerThread<ClientProp> client : clients) {
            if (client.getID().equals(clientId)) return client.getClientProperties();
        }
        return null;
    }
}
