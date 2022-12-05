package core;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.Queue;

public class Sender implements Runnable {
    private final DataOutputStream dos;
    private final Queue<String> messageQueue;
    private boolean running;


    public Sender(OutputStream outputStream) {
        messageQueue = new ArrayDeque<>();
        running = true;

        dos = new DataOutputStream(outputStream);
    }

    private void send(String msg) {
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
        synchronized (messageQueue) {
            while (running) {
                try {
                    while (messageQueue.isEmpty())
                        messageQueue.wait();
                } catch (InterruptedException e) {
                    running = false;
                    return;
                }
                send(messageQueue.poll());
            }
        }
    }

    public void addToQueue(String msg) {
        synchronized (messageQueue) {
            messageQueue.offer(msg);
            messageQueue.notify();
        }
    }

}