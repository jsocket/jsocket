package net.jsocket.test;

import net.jsocket.ClientProperties;

import java.util.Date;
import java.util.UUID;

public class ChatClientProperties extends ClientProperties {
    private String nickname;

    public ChatClientProperties(UUID clientID) {
        super(clientID);
        nickname = clientID.toString();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
