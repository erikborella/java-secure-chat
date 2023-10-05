package keyserver;

import crypto.AESUtils;
import crypto.BinaryUtils;
import crypto.RSAUtils;
import java.io.IOException;
import java.security.PublicKey;
import tcp.TCPServer;

public class KeyServer {
    private static final String MULTICAST_ADDRESS = "230.0.0.0";
    private static final int MULTICAST_PORT = 50000;
    
    private static final String PASSWORD = "puK4ok0t2EdpX0HT666tVG5Eg2oB9G1x" +
                                            "Xl2JUM88xnEDBtHOoZ2RxuNFTy7T91Is" +
                                            "SWuGmeFHS1NRFgygL42nKkavaoBAw0cA" +
                                            "DEtJT3IkhtEeELGhMAeqWV1Q1TgTq0mC" +
                                            "IoO8eFDizVNfLa4co8uOUg1K90ijpobx" +
                                            "QWU4rgbADUr9TYX9RlzUsPxVDRVkHlPr" +
                                            "dTrpbwsmo1nskwWM8sMJucHsAwUan8Ba" +
                                            "ePfK8qi0VvmgW8vOCWpSzp7lWVplYuNB" +
                                            "c2fTQKZkxea9T5Q8k2Fd8eKHZBPAwqng" +
                                            "WsCJCjkx79RMvC2lAx4QpsiLHjicjt5M";
    
    private static final String AESKey = BinaryUtils.toString(
            AESUtils.createKey(PASSWORD.toCharArray()));
    
    private static final String RESPONSE = MULTICAST_ADDRESS + "\n" 
                                            + MULTICAST_PORT + "\n"
                                            + AESKey;
    
    public static void main(String[] args) throws IOException {
        TCPServer tcpServer = new TCPServer(6969);

        tcpServer.startListen((String message) -> processKeyRequest(message));
    }
    
    private static String processKeyRequest(String message) {
        PublicKey publicKey = RSAUtils.getPublicKeyFromPEM(message);
        
        String encRes = RSAUtils.encrypt(publicKey, RESPONSE);
        
        return encRes;
    }
}
