package core.network;

import core.Observable;
import core.events.MessageReceivedEvent;
import core.listenables.MessageReceivedListenable;
import core.network.packets.Packet;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Receiver extends Observable implements Runnable, MessageReceivedListenable {
    private final DataInputStream dis;
    private final PacketReceiver packetHandler;
    private boolean running = true;

    public Receiver(InputStream inputStream, PacketReceiver packetHandler) {
        dis = new DataInputStream(inputStream);
        this.packetHandler = packetHandler;
    }

    public Packet<?> receive() throws IOException {  // TODO: MessagePayload
        if (!running) return "";
        String msg;
        try {
            msg = dis.readUTF();
        } catch (IOException e) {
            running = false;
            dis.close();
            throw e;
        }
        return msg;
    }

    @Override
    public void run() {
        try {
            while (running) {
                messageReceived(new MessageReceivedEvent(this, receive()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
