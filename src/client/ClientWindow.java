package client;

import java.net.Inet4Address;
import java.net.InetAddress;

public class ClientWindow {
    public ClientWindow() {
        Client c = new Client();

        try {
            var port = 6957;
            var host = (Inet4Address) InetAddress.getLocalHost();

            c.connect(host, port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
