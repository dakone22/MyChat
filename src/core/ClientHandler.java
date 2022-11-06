package core;

import java.net.Socket;

class ClientHandler implements Runnable {
    private Server server;
    private Socket clientSocket;

    ClientHandler(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {

    }

    public void send(String msg) {

    }
}
