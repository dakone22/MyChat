package server;

import core.Receiver;
import core.Sender;

import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.List;

class ClientHandler {
    private final Socket socket;

    private final ClientHandlerObserver observer;

    private final Sender sender;
    private final Receiver receiver;

    private final Collection<Thread> threads;


    ClientHandler(ClientHandlerObserver observer, Socket socket) throws IOException {
        this.observer = observer;
        this.socket = socket;

        receiver = new Receiver(socket.getInputStream(), message -> observer.onMessageReceived(this, message));
        sender = new Sender(socket.getOutputStream());

        threads = List.of(new Thread[]{
                new Thread(sender),
                new Thread(receiver),
        });

        for (var thread : threads)
            thread.start();
    }

    public void stop() {
        for (var thread : threads)
            thread.interrupt();

        if (socket == null) return;
        if (socket.isClosed()) return;

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String message) {
        sender.addToQueue(message);
    }
}
