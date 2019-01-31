package net.jsocket;

import java.util.UUID;

@FunctionalInterface
public interface CreateClientProperties {
    ClientProperties create(UUID clientID);
}
