package core;

import core.network.PacketHandler;
import core.network.Receiver;
import core.network.Sender;
import core.network.listeners.PacketListener;
import core.network.packets.Packet;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Connection<R extends PacketListener, S extends PacketListener>  {  // R - receivable (получаемые)
                                                                                        // S - sendable (отправляемые)
    protected Socket socket;
    protected Thread senderThread;
    protected Sender<S> sender;
    protected Thread receiverThread;

    protected ExceptionOccurredListener exceptionOccurredListener = null;

    public void addExceptionOccurredListener(ExceptionOccurredListener listener) {
        exceptionOccurredListener = listener;
    }

    protected void throwException(Object sender, Throwable e) {
        if (exceptionOccurredListener == null)
            throw new RuntimeException(e);
        exceptionOccurredListener.onExceptionOccurred(sender, e);
    }

    protected void startConnection(Socket socket, PacketHandler<S> packetSenderHandler, PacketHandler<R> packetReceiverHandler, R listener) throws IOException {
        sender = new Sender<>(socket.getOutputStream(), packetSenderHandler);
        var receiver = new Receiver<>(socket.getInputStream(), packetReceiverHandler, listener);

        senderThread = new Thread(sender);
        receiverThread = new Thread(receiver);

        for (var thread : List.of(new Thread[]{senderThread, receiverThread})) {
            thread.setUncaughtExceptionHandler(this::throwException);
            thread.start();
        }
    }

    public void stop() throws IOException {
        for (var t : List.of(new Thread[]{senderThread, receiverThread}))
            if (t != null && !t.isInterrupted())
                t.interrupt();

        if (socket == null) return;
        if (socket.isClosed()) return;

        socket.close();
    }

    public void sendPacket(Packet<? extends S> packet) throws InterruptedException {
        sender.send(packet);
    }
}
