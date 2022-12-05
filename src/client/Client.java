package client;

import core.Receiver;
import core.Sender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.Collection;
import java.util.List;

public class Client {
    private Socket socket;

    private Collection<Thread> threads;

    public void connect(Inet4Address ip, int port) {
        try {
            socket = new Socket(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Sender sender;
        Receiver receiver;

        try {
            receiver = new Receiver(socket.getInputStream(), System.out::println);
            sender = new Sender(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        var console = new BufferedReader(new InputStreamReader(System.in));
        var consoleReader = new Runnable() {
            @Override
            public void run() {
                try {
                    while (true)
                        sender.addToQueue(console.readLine());
                } catch (IOException e) {
                    stop();
                }
            }
        };

        threads = List.of(new Thread[]{
            new Thread(sender),
            new Thread(receiver),
            new Thread(consoleReader),
        });

        for (var thread : threads)
            thread.start();
    }

    void stop() {
        for (var thread : threads)
            thread.interrupt();

        if (socket == null) return;
        if (socket.isClosed()) return;

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
