package tcp;

import interfaces.ICallback;
import interfaces.ICallbackException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPServer {
    private Logger logger = Logger.getLogger(TCPServer.class.getName());
    private ServerSocket serverSocket;
    private final int port;

    public TCPServer(int port) throws IOException {
        this.port = port;
        this.serverSocket = new ServerSocket(port);
    }
    
    public void startListen(ICallback callback, ICallbackException callbackException) {
        this.logger.log(Level.INFO, "Server is listening on port " + this.port);
        
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                
                Thread clientThread = new ClientHandler(clientSocket);
                clientThread.start();
            } catch (IOException ex) {
                Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private class ClientHandler extends Thread {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }
        
        @Override
        public void run() {
            try {
                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();
                
                byte[] buffer = new byte[1024];
                int bytesRead;
                
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    String clientData = new String(buffer, 0, bytesRead);
                    System.out.println("Received from client: " + clientData);
                    
                    String response = "Hello, client!";
                    outputStream.write(response.getBytes());
                    outputStream.flush();
                }
                
                clientSocket.close();
                
            } catch (IOException ex) {
                Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
