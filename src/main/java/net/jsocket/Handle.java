package net.jsocket;

/**
 * Functional interface used for handling incoming messages
 */
@FunctionalInterface
public interface Handle {
    /**
     * The actual message handling method
     * @param data The data that the endpoint receives
     */
    void handle(DataCarrier data);
}