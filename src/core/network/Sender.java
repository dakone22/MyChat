package core.network;

import core.network.listeners.PacketListener;
import core.network.packets.Packet;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Sender <T extends PacketListener> implements Runnable {
    private final BlockingQueue<Packet<? extends T>> messageQueue;
    private final ObjectOutputStream outputStream;
    private final PacketHandler<T> packetHandler;
    private boolean running;


    public Sender(OutputStream outputStream, PacketHandler<T> packetHandler) throws IOException {
        this.packetHandler = packetHandler;

        this.messageQueue = new LinkedBlockingQueue<>();
        this.outputStream = new ObjectOutputStream(outputStream);

        running = true;
    }

    private void sendImmediately(Packet<? extends T> packet) throws IOException {
        if (!running) return;

        try {
            packetHandler.sendPacket(outputStream, packet);
            outputStream.flush();
        } catch (IOException e) {
            running = false;
            outputStream.close();
            throw e;
        }
    }

    @Override
    public void run() {
        try {
            while (running) {
                var msg = messageQueue.take();
                sendImmediately(msg);
            }
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(Packet<? extends T> msg) throws InterruptedException {
        if (!messageQueue.add(msg))  // TODO: stress test
            throw new InterruptedException();
    }

}