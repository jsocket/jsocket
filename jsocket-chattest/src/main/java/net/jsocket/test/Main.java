package net.jsocket.test;

import net.jsocket.ConversationOrigin;
import net.jsocket.DataCarrier;
import net.jsocket.server.Server;
import org.apache.commons.cli.*;

public class Main {
    private static Server server;

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
        switch (cmd.getOptionValue("mode")) {
            case "client":
                if (cmd.hasOption("address")) {
                    ClientWindow window = new ClientWindow(cmd.getOptionValue("address"), Integer.parseInt(cmd.getOptionValue("port")));
                    window.show();
                }
                break;
            case "server":
                server = new Server(Integer.parseInt(cmd.getOptionValue("port")));
                server.addHandle("chatMessage", Main::server_chatMessageHandle);
                server.addHandle("payloadTest", Main::server_payloadTestHandle);
                if (!cmd.hasOption("nogui")) {
                    ServerWindow serverWindow = new ServerWindow(server);
                    serverWindow.show();
                } else {
                    while (server.isRunning()) Thread.onSpinWait();
                }
                break;
        }
    }

    private static void server_chatMessageHandle(DataCarrier dataCarrier) {
        if (dataCarrier.getConversationOrigin() == ConversationOrigin.ClientBroadcast) {
            server.broadcast("chatMessage", dataCarrier.getSenderID(), dataCarrier.getData(), true);
        }
    }

    private static void server_payloadTestHandle(DataCarrier dataCarrier) {
        PayloadTest payloadTest = (PayloadTest) dataCarrier.getData();
        server.broadcast("chatMessage", dataCarrier.getSenderID(), new PayloadSentChatItem(dataCarrier.getSenderID().getPeerID(), payloadTest.getSize(), payloadTest.verify()), true);
    }
}
