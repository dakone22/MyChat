package core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);

        while (!serverSocket.isClosed()) {
            Socket clientSocket = null;
            clientSocket = serverSocket.accept();
        }
    }
}
