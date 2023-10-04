package keyserver;

import crypto.AESUtils;
import crypto.BinaryUtils;
import crypto.RSAUtils;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PublicKey;
import tcp.TCPServer;

public class KeyServer {
    public static void main(String[] args) throws IOException {
        TCPServer tcpServer = new TCPServer(6969);
        
        byte[] passphrase = AESUtils.createPassphrase(new char[] {'t', 'e', 's', 't' });
        
        String e = AESUtils.encrypt(passphrase, "teste");
        System.out.println(e);
        
        String b = AESUtils.decrypt(passphrase, e);
        System.out.println(b);
        
//        tcpServer.startListen((String message) -> processKeyRequest(message));
    }
    
    private static String processKeyRequest(String message) {
        PublicKey publicKey = RSAUtils.getPublicKeyFromPEM(message);
        
        String multicastAddress = "127.0.0.1";
        int multicastPort = 1234;
        String symKey = "sjkdlajdjkaldsd";
        
        String response = multicastAddress + "\n" + multicastPort + "\n" + symKey;
        
        String encRes = RSAUtils.encrypt(publicKey, response);
        
        return encRes;
    }
}
