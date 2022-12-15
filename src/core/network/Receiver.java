package core.network;

import core.network.listeners.PacketListener;
import core.network.packets.Packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class Receiver<T extends PacketListener> implements Runnable {
    private final ObjectInputStream inputStream;
    private final PacketHandler<T> packetHandler;
    private final T listener;
    private boolean running = true;

    public Receiver(InputStream inputStream, PacketHandler<T> packetHandler, T listener) throws IOException {
        this.inputStream = new ObjectInputStream(inputStream);
        this.packetHandler = packetHandler;
        this.listener = listener;
    }

    private void receive() throws IOException {
        if (!running) return;

        try {
            Packet<T> packet = packetHandler.receivePacket(inputStream);
            if (packet == null) throw new IOException("Empty packet!");

            PacketHandler.handlePacket(packet, listener);
        } catch (IOException e) {
            running = false;
            inputStream.close();
            throw e;
        }
    }

    @Override
    public void run() {
        try {
            while (running) {
                receive();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
