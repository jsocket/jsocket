package net.jsocket;

import org.jetbrains.annotations.Contract;

/**
 * Functional interface used for handling incoming messages
 */
@FunctionalInterface
public interface MessageHandle<TData extends Message> {
    /**
     * The actual message handling method
     * @param data The data that the endpoint receives
     */
    void handle(DataCarrier<TData> data);
}
