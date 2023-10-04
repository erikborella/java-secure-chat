package tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPServer {
    private final Logger logger = Logger.getLogger(TCPServer.class.getName());
    private final ServerSocket serverSocket;
    private final int port;

    public TCPServer(int port) throws IOException {
        this.port = port;
        this.serverSocket = new ServerSocket(port);
    }
    
    public void startListen(Function<String, String> callback) {
        this.logger.log(Level.INFO, "Server is listening on port {0}", String.valueOf(this.port));
        
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                
                this.logger.log(Level.INFO, "Accepted connection from {0}:{1}", 
                        new Object[]{
                            clientSocket.getInetAddress(),
                            String.valueOf(clientSocket.getPort())
                        });
                
                Thread clientThread = new ClientHandler(clientSocket, callback);
                clientThread.start();
            } catch (IOException ex) {
                Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private class ClientHandler extends Thread {
        private final Socket clientSocket;
        private final Function<String, String> callback; 
        private final Logger logger = Logger.getLogger(ClientHandler.class.getName());
        
        public ClientHandler(Socket clientSocket, Function<String, String> callback) {
            this.clientSocket = clientSocket;
            this.callback = callback;
        }
        
        @Override
        public void run() {
            logger.log(Level.INFO, "Processing request for client {0}:{1}",
                    new Object[]{
                        clientSocket.getInetAddress(),
                        String.valueOf(clientSocket.getPort())
                    });
            
        
            try (clientSocket) {
                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();

                byte[] buffer = new byte[2048];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    String clientData = new String(buffer, 0, bytesRead);

                    String response = this.callback.apply(clientData);
                    outputStream.write(response.getBytes());
                    outputStream.flush();
                }
            } catch (IOException ex) {
                Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            logger.log(Level.INFO, "End processing request for client {0}:{1}",
                    new Object[]{
                        clientSocket.getInetAddress(),
                        String.valueOf(clientSocket.getPort())
                    });
        }
    }
}
