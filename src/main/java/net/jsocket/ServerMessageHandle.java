package net.jsocket;

import net.jsocket.server.ServerThread;

public interface ServerMessageHandle<ClientProp extends ClientProperties, TData extends Message> {
    void handle(ServerThread<ClientProp> sender, DataCarrier<TData> dataCarrier);
}
