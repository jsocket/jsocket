package net.jsocket;

/**
 * Handles responses to client requests if they were created using {@link DataCarrier#createResponse(Message)}
 * @param <TData> Type of data this interface handles
 */
@FunctionalInterface
public interface ClientResponseHandle<TData extends Message> {
    /**
     * @param data The data received from socket
     * @return boolean if the handle should be removed, should usually be true. It might be useful to keep it there since responses to broadcasts will all respond to the same request.
     */
    boolean handle(DataCarrier<TData> data);
}
