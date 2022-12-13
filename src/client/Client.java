package client;

import core.Observable;
import core.Receiver;
import core.Sender;
import core.events.ExceptionOccurredEvent;
import core.listenables.ExceptionOccurredListenable;
import core.listenables.MessageReceivedListenable;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class Client extends Observable implements ExceptionOccurredListenable, MessageReceivedListenable {
    private Socket socket;

    private Sender sender;

    private Thread senderThread;
    private Thread receiverThread;

    public void connect(InetAddress host, int port) throws IOException {
        // TODO: if (connected) return;

        socket = new Socket(host, port);

        sender = new Sender(socket.getOutputStream());
        Receiver receiver = new Receiver(socket.getInputStream());

        receiver.addMessageReceivedListener(this::messageReceived);

        senderThread = new Thread(sender);
        receiverThread = new Thread(receiver);

        for (var thread : List.of(new Thread[]{senderThread, receiverThread})) {
            thread.setUncaughtExceptionHandler((t, e) -> exceptionOccurred(new ExceptionOccurredEvent(t, e)));
            thread.start();
        }
    }

    void stop() throws IOException {
        for (var t : List.of(new Thread[]{senderThread, receiverThread}))
            if (t != null && !t.isInterrupted())
                t.interrupt();

        if (socket == null) return;
        if (socket.isClosed()) return;

        socket.close();
    }

    void send(String message) throws InterruptedException {
        sender.send(message);
    }
}
