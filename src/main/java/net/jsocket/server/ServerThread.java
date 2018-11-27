package net.jsocket.server;

import net.jsocket.DataCarrier;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.*;
import java.util.UUID;

/**
 * A thread that listens to one client
 */
public class ServerThread extends Thread {
    private Server server;
    private Socket socket;
    private UUID ID;
    private DataInputStream streamIn = null;
    private DataOutputStream streamOut = null;

    /**
     * The default constructor
     * @param _server The managing server
     * @param _socket The socket connected to the client
     */
    public ServerThread(Server _server, Socket _socket) {
        super();
        server = _server;
        socket = _socket;
        ID = UUID.randomUUID();
    }

    /**
     * Sends a message to the client
     * @param data The message to be sent
     */
    public void send(DataCarrier data) {
        try {
            ObjectOutputStream output = new ObjectOutputStream(streamOut);
            output.writeObject(data);
            output.flush();
        } catch (IOException e) {
            System.out.println(ID + " ERROR sending: " + e.getMessage());
            server.remove(ID);
            stop();
        }
    }

    /**
     * Gets the ID of the client
     * @return UUID of the connected client
     */
    public UUID getID() {
        return ID;
    }

    public void run() {
        System.out.println("Server Thread " + ID + " running.");
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                ObjectInputStream input = new ObjectInputStream(streamIn);
                DataCarrier data = (DataCarrier) input.readObject();
                server.handle(data);
            } catch (IOException e) {
                System.out.println(ID + " ERROR reading: " + e.getMessage());
                server.remove(ID);
                stop();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                stop();
            }
        }
    }

    /**
     * Opens the socket to the client
     * @throws IOException From {@link Socket#getInputStream() getInputStream()} or {@link Socket#getOutputStream() getOutputStream()}
     */
    public void open() throws IOException {
        streamIn = new DataInputStream(new
                BufferedInputStream(socket.getInputStream()));
        streamOut = new DataOutputStream(new
                BufferedOutputStream(socket.getOutputStream()));
    }

    /**
     * Closes the socket to the client
     * @throws IOException From {@link Socket#getInputStream() getInputStream()} or {@link Socket#getOutputStream() getOutputStream()}
     */
    public void close() throws IOException {
        if (socket != null) socket.close();
        if (streamIn != null) streamIn.close();
        if (streamOut != null) streamOut.close();
    }
}
