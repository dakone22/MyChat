import core.Server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Server s = new Server();

        try {
            s.start(6957);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}