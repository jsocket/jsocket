package net.jsocket;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Used for message direction indication.
 */
public enum Direction {
    /**
     * Message that goes from client to socket server.
     */
    ToClient,
    /**
     * Message that goes from socket server to client.
     */
    ToServer;

    /**
     * @return Opposite Direction
     */
    @NotNull
    @Contract(pure = true)
    public Direction getOpposite() {
        switch (this){
            case ToClient:
                return ToServer;
            case ToServer:
                return ToClient;
            default:
                assert false;
                return valueOf("false");
        }
    }
}
