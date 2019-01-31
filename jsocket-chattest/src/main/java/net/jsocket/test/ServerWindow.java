package net.jsocket.test;

import net.jsocket.*;
import net.jsocket.client.*;
import net.jsocket.server.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.net.IDN;
import java.util.UUID;

public class ServerWindow extends JFrame {
    private JPanel root;
    private JButton sendButton;
    private JList chatList;
    private JList clientsList;
    private JButton kickButton;
    private JScrollPane chatPane;
    private JTextArea chatArea;
    private DefaultListModel chatListModel;
    private DefaultListModel clientsListModel;
    private Server server;
    private Client console;

    public ServerWindow(Server server) {
        super("JSocket Demo App Server");
        chatListModel = new DefaultListModel();
        chatList.setModel(chatListModel);
        clientsListModel = new DefaultListModel();
        clientsList.setModel(clientsListModel);
        this.server = server;
        this.server.setNewConnectionHandle(this::newConnection);
        this.server.setClientDisconnectedHandle(this::clientDisconnected);
        console = new Client("localhost", server.getPort(), clientID -> {
            chatList.setCellRenderer(new ChatItemRenderer(clientID.toString(), clientID));
            clientsList.setCellRenderer(new UUIDTextRenderer(clientID));
        }, (ID, disconnectReason) -> hide());
        setContentPane(root);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        kickButton.addActionListener(this::kickButton_click);
        sendButton.addActionListener(this::sendButton_click);
        // server.addHandle("chatMessage", this::server_chatMessageHandle);
        console.addHandle("chatMessage", this::console_chatMessageHandle);
        InputMap input = chatArea.getInputMap();
        KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
        KeyStroke shiftEnter = KeyStroke.getKeyStroke("shift ENTER");
        input.put(shiftEnter, "insert-break");  // input.get(enter)) = "insert-break"
        input.put(enter, "text-submit");

        ActionMap actions = chatArea.getActionMap();
        actions.put("text-submit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send();
            }
        });
        clientsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        clientsList.addListSelectionListener(listSelectionEvent -> {
            if (clientsListModel.get(clientsList.getSelectedIndex()).equals(console.getClientID()))
                kickButton.disable();
            else kickButton.enable();
        });
    }

    private void kickButton_click(ActionEvent e) {
        server.remove((UUID) clientsListModel.getElementAt(clientsList.getSelectedIndex()), DisconnectReason.ClientKicked);
    }

    private void sendButton_click(ActionEvent e) {
        send();
    }

    private void send() {
        if (chatArea.getText() != "") {
            DataCarrier carrier = new DataCarrier("chatMessage", Direction.ToServer, ConversationOrigin.ClientBroadcast, new SocketPeerID(console.getClientID()), SocketPeerID.Broadcast, new TextMessage(chatArea.getText()));
            console.send(carrier);
        }
        chatArea.setText("");
    }

    private void console_chatMessageHandle(DataCarrier dataCarrier) {
        if (dataCarrier.getData() instanceof TextMessage) {
            TextMessage message = (TextMessage) dataCarrier.getData();
            chatListModel.addElement(new ChatItem(dataCarrier.getSenderID().getPeerID().toString(), message.getMessage(), message.getTimestamp()));
        } else if (dataCarrier.getData() instanceof PayloadSentChatItem) {
            chatListModel.addElement(dataCarrier.getData());
        }

        chatPane.getVerticalScrollBar().setValue(chatPane.getHorizontalScrollBar().getMaximum());
    }

    public void newConnection(UUID clientID) {
        clientsListModel.addElement(clientID);
    }

    public void clientDisconnected(UUID clientID, DisconnectReason disconnectReason) {
        clientsListModel.removeElement(clientID);
    }
}
