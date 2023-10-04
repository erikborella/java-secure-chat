package keyserver;

import java.io.IOException;
import tcp.TCPServer;

public class KeyServer {
    public static void main(String[] args) throws IOException {
        TCPServer tcpServer = new TCPServer(6969);
        
        tcpServer.startListen((String m) -> {}, (Exception e) -> {});
    }
}
