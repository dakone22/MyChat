package core.network;

import core.network.listeners.PacketListener;
import core.network.packets.Packet;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Sender<T extends PacketListener> implements Runnable {
    private final BlockingQueue<Packet<? extends T>> packetQueue;
    private final ObjectOutputStream outputStream;
    private final PacketHandler<T> packetHandler;
    private boolean running;


    public Sender(OutputStream outputStream, PacketHandler<T> packetHandler) throws IOException {
        this.packetHandler = packetHandler;

        this.packetQueue = new LinkedBlockingQueue<>();
        this.outputStream = new ObjectOutputStream(outputStream);

        running = true;
    }

    private void sendImmediately(Packet<? extends T> packet) throws IOException {
        try {
            packetHandler.sendPacket(outputStream, packet);
            outputStream.flush();
        } catch (IOException e) {
            outputStream.close();
            stop();
            throw e;
        }
    }

    @Override
    public void run() {
        try {
            while (running) {
                var packet = packetQueue.take();
                System.out.println("Get new packet from queue: " + packet);
                sendImmediately(packet);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            stop();
        }
    }

    public void send(Packet<? extends T> packet) throws InterruptedException {
        System.out.println("Add to message queue " + packet);
        if (!packetQueue.add(packet))  // TODO: stress test
            throw new InterruptedException();
    }

    public void stop() {
        System.out.println("Stopping sender " + this + " " + Thread.currentThread());
        running = false;
    }

}