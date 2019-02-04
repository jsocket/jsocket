package net.jsocket;

import java.util.UUID;

@FunctionalInterface
public interface CreateClientProperties<ClientProp extends ClientProperties> {
    ClientProp create(UUID clientID);
}
