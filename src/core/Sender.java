package core;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Sender implements Runnable {
    private final DataOutputStream dos;
    private final BlockingQueue<String> messageQueue;
    private boolean running;


    public Sender(OutputStream outputStream) {
        messageQueue = new LinkedBlockingQueue<>();
        running = true;

        dos = new DataOutputStream(outputStream);
    }

    private void sendImmediately(String msg) {
        if (!running) return;
        try {
            dos.writeUTF(msg);
            dos.flush();
        } catch (IOException e) {
            running = false;
            try {
                dos.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (running) {
                var msg = messageQueue.take();
                sendImmediately(msg);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(String msg) throws InterruptedException {
        if (!messageQueue.add(msg))  // TODO: stress test
            throw new InterruptedException();
    }

}