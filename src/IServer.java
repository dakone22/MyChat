import java.net.InetAddress;

public interface IServer {
    void start(InetAddress ip, int port);
    void stop();
}
