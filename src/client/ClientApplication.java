package client;

import core.events.ExceptionOccurredEvent;

import java.net.InetAddress;

public class ClientApplication {
    private final ClientOutput output;
    private final Client client;

    private void setupListeners() {
        client.addMessageReceivedListener(output::newMessage);
        client.addExceptionOccurredListener(output::exceptionOccurred);
    }

    public ClientApplication(ClientOutput clientOutput) {
        output = clientOutput;
        client = new Client();

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
            client.send(text);
        } catch (Exception e) {
            output.exceptionOccurred(new ExceptionOccurredEvent(this, e));
        }
    }
}
