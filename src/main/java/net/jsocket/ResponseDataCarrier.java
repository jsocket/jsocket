package net.jsocket;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ResponseDataCarrier<TData extends Message> extends DataCarrier<TData> {
    private final UUID responseFor;

    ResponseDataCarrier(@NotNull DataCarrier<? extends Message> dataCarrier, TData data) {
        super(dataCarrier.getName(), dataCarrier.getDirection().getOpposite(), dataCarrier.getConversationOrigin().getResponseOrigin(), dataCarrier.getRecipientID(), dataCarrier.getSenderID(), data);
        this.responseFor = dataCarrier.getRequestId();
    }

    public UUID getResponseFor() {
        return responseFor;
    }
}
