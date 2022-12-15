package client;

import core.network.packets.c2s.ChatMessageC2SPacket;

import java.net.InetAddress;

public class ClientApplication {
    private final ClientClientInterface userInterface;
    private final ClientConnection networkHandler;

    public ClientApplication(ClientClientInterface clientClientInterface) {
        userInterface = clientClientInterface;

        networkHandler = new ClientConnection(userInterface);
        networkHandler.addExceptionOccurredListener(userInterface);
    }

    public void connect(String host, int port) {
        try {
            networkHandler.connect(InetAddress.getByName(host), port);
        } catch (Exception e) {
            userInterface.onExceptionOccurred(this, e);
        }
    }

    public void sendPublicMessage(String text) {
        try {
            networkHandler.sendPacket(new ChatMessageC2SPacket.Public(text));
        } catch (Exception e) {
            userInterface.onExceptionOccurred(this, e);
        }
    }


}
