package server;

import core.Observable;
import core.Receiver;
import core.Sender;
import core.events.ExceptionOccurredEvent;
import core.listenables.ExceptionOccurredListenable;
import core.listenables.MessageReceivedListenable;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

class ClientHandler extends Observable implements ExceptionOccurredListenable, MessageReceivedListenable {
    private final Socket socket;
    private final Sender sender;

    private final Thread senderThread;
    private final Thread receiverThread;

    public ClientHandler(Socket clientSocket) throws IOException {  // TODO: Try out Client & ClientHandler Base class
        socket = clientSocket;

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

    void close() throws IOException {
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
