package core;

import core.events.MessageSendEvent;
import core.listeners.MessageSendListener;

import javax.swing.event.EventListenerList;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private EventListenerList listenerList = new EventListenerList();

    private ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
    private ServerSocket serverSocket;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);

        while (!serverSocket.isClosed()) {
            Socket clientSocket = null;
            clientSocket = serverSocket.accept();

            ClientHandler client = new ClientHandler(this, clientSocket);
            clients.add(client);
        }
    }

    public void sendToAll(String msg) {
        for (ClientHandler client : clients) {
            sendMessage(client, msg);
        }
    }

    public void sendMessage(ClientHandler c, String msg) {
        c.send(msg);
    }

    // region listeners

    public void addMessageSendListener(MessageSendListener listener) {
        listenerList.add(MessageSendListener.class, listener);
    }

    public void removeMessageSendListener(MessageSendListener listener) {
        listenerList.remove(MessageSendListener.class, listener);
    }

    // endregion
}
