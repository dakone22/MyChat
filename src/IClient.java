import java.net.InetAddress;

public interface IClient {
    void connect(InetAddress ip, int port);
}
