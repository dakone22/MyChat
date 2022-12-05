package core;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Receiver implements Runnable {
    private final DataInputStream dis;
    private final Receivable receivable;
    private boolean running = true;

    public Receiver(InputStream inputStream, Receivable target) {
        dis = new DataInputStream(inputStream);
        receivable = target;
    }

    public String receive() {
        if (!running) return "";
        String msg = "";
        try {
            msg = dis.readUTF();
        } catch (IOException e) {
            running = false;
            try {
                dis.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return msg;
    }

    @Override
    public void run() {
        while (running) {
            receivable.receive(receive());
        }
    }
}
