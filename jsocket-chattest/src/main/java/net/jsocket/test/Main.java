package net.jsocket.test;

import net.jsocket.*;
import net.jsocket.server.Server;
import org.apache.commons.cli.*;

import java.util.UUID;

public class Main {
    private static Server<ChatClientProperties> server;

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption(Option.builder("m").longOpt("mode").hasArg(true).argName("mode").required().desc("The operation mode, can be either client or server").build())
                .addOption(Option.builder("a").longOpt("address").hasArg(true).argName("host").desc("The server address. Only used when running in client mode").build())
                .addOption(Option.builder("p").longOpt("port").hasArg(true).required().argName("port").desc("The network port. Used both in server and client").build())
                .addOption(Option.builder().longOpt("nogui").hasArg(false).desc("Run server without GUI").build());
        CommandLine cmd = null;
        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            new HelpFormatter().printHelp("java -jar jsocketchat_test.jar", options);
        }
        if (cmd != null) {
            switch (cmd.getOptionValue("mode")) {
                case "client":
                    if (cmd.hasOption("address")) {
                        ClientWindow window = new ClientWindow(cmd.getOptionValue("address"), Integer.parseInt(cmd.getOptionValue("port")));
                        window.show();
                    }
                    break;
                case "server":
                    server = new Server<>(Integer.parseInt(cmd.getOptionValue("port")), Main::server_createClientProperties);
                    server.addHandle("chatMessage", Main::server_chatMessageHandle);
                    server.addHandle("payloadTest", Main::server_payloadTestHandle);
                    if (!cmd.hasOption("nogui")) {
                        ServerWindow serverWindow = new ServerWindow(server);
                        serverWindow.setVisible(true);
                    } else {
                        while (server.isRunning()) Thread.onSpinWait();
                    }
                    break;
            }
        }
    }

    private static ChatClientProperties server_createClientProperties(UUID uuid) {
        return new ChatClientProperties(uuid);
    }

    private static void server_chatMessageHandle(DataCarrier<TextMessage> data) {
        if (data.getConversationOrigin() == ConversationOrigin.ClientBroadcast) {
            server.broadcast("chatMessage", data.getSenderID(), data.getData(), true);
        }
    }

    private static void server_payloadTestHandle(DataCarrier<PayloadTest> data) {
        PayloadTest payloadTest = data.getData();
        server.broadcast("payloadSentMessage", data.getSenderID(), new PayloadSentChatItem(data.getSenderID().getPeerID(), data.getSenderID().getPeerID().toString(), payloadTest.getSize(), payloadTest.verify()), true);
    }
}
