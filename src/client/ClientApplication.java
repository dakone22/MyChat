package client;

import core.events.ExceptionOccurredEvent;
import core.network.listeners.ClientPacketListener;
import core.network.packets.s2c.chat.*;

import java.net.InetAddress;

public class ClientApplication {
    private final ClientOutput output;
    private final Client client;
    private final ClientPacketListener clientPacketHandler;

    private void setupListeners() {
        client.addMessageReceivedListener(output::newMessage);
        client.addExceptionOccurredListener(output::exceptionOccurred);
    }

    public ClientApplication(ClientOutput clientOutput) {
        output = clientOutput;
        client = new Client();

        clientPacketHandler = new ClientPacketListener() {
            @Override
            public void onPublicMessage(ChatMessageS2CPacket.Public packet) {

            }

            @Override
            public void onPrivateMessage(ChatMessageS2CPacket.Private packet) {

            }

            @Override
            public void onSystemMessage(SystemMessageS2CPacket packet) {

            }

            @Override
            public void onCustomSystemMessage(CustomSystemMessageS2CPacket packet) {

            }

            @Override
            public void onClientJoin(ClientJoinS2CPacket packet) {

            }

            @Override
            public void onClientLeave(ClientLeaveS2CPacket packet) {

            }
        };

        setupListeners();
    }

    public void connect(String host, int port) {
        try {
            client.connect(InetAddress.getByName(host), port);
        } catch (Exception e) {
            output.exceptionOccurred(new ExceptionOccurredEvent(this, e));
        }
    }

    public void send(String text) {
        try {
            client.sendPublicMessage(text);
        } catch (Exception e) {
            output.exceptionOccurred(new ExceptionOccurredEvent(this, e));
        }
    }
}
