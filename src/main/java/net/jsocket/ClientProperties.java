package net.jsocket;

import java.util.Date;
import java.util.UUID;

public class ClientProperties implements Message {
    private final Date connectionStarted;
    private final UUID clientID;

    public ClientProperties(UUID clientID) {
        this.connectionStarted = new Date();
        this.clientID = clientID;
    }

    public Date getConnectionStarted() {
        return connectionStarted;
    }

    public UUID getClientID() {
        return clientID;
    }

    @Override
    public String getDescription() {
        return "Client " + clientID + "'s properties (connected at " + connectionStarted.toString() + ")";
    }
}
