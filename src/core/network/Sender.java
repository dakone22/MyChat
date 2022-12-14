package core.network;

import core.network.packets.Packet;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Sender implements Runnable {
    private final DataOutputStream dos;
    private final BlockingQueue<Packet<?>> messageQueue;
    private boolean running;


    public Sender(OutputStream outputStream) {
        messageQueue = new LinkedBlockingQueue<>();
        running = true;

        dos = new DataOutputStream(outputStream);
    }

    private void sendImmediately(Packet<?> msg) {
        if (!running) return;
        try {

            var buffer = ByteBuffer.allocate(2 << 14);  // TODO: rework
            buffer.put()
            msg.write(buffer);


            System.out.println("Buffer written: " + buffer.position() + " / " + buffer.limit());

            int written = buffer.position();
            buffer.rewind();
            buffer.limit(written);

            System.out.println("Buffer written: " + buffer.position() + " / " + buffer.limit());

            WritableByteChannel channel = Channels.newChannel(dos);
            System.out.println("To OutputStream: " + channel.write(buffer));

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

    public void send(Packet<?> msg) throws InterruptedException {
        if (!messageQueue.add(msg))  // TODO: stress test
            throw new InterruptedException();
    }

}