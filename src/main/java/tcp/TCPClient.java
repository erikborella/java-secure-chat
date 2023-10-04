package tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPClient {
    private final String serverAddres;
    private final int port;
    private final int bufferSize;

    public TCPClient(String serverAddres, int port) {
        this.serverAddres = serverAddres;
        this.port = port;
        this.bufferSize = 2048;
    }

    public TCPClient(String serverAddres, int port, int bufferSize) {
        this.serverAddres = serverAddres;
        this.port = port;
        this.bufferSize = bufferSize;
    }
    
    public String send(String message) {
        String response = null;
        
        try (Socket socket = new Socket(this.serverAddres, this.port)) {
            
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            
            this.sendMessage(outputStream, message);
            
            response = this.getResponse(inputStream);
        } 
        catch (IOException ex) {
            Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return response;
    }

    private String getResponse(InputStream inputStream) throws IOException {
        String response;
        byte[] buffer = new byte[this.bufferSize];
        int bytesRead = inputStream.read(buffer);
        response = new String(buffer, 0, bytesRead);
        return response;
    }

    private void sendMessage(OutputStream outputStream, String message) throws IOException {
        outputStream.write(message.getBytes());
        outputStream.flush();
    }
}
