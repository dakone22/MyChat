package core;

import core.events.MessageReceivedEvent;
import core.listenables.MessageReceivedListenable;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Receiver extends Observable implements Runnable, MessageReceivedListenable {
    private final DataInputStream dis;
    private boolean running = true;

    public Receiver(InputStream inputStream) {
        dis = new DataInputStream(inputStream);
    }

    public String receive() throws IOException {
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
