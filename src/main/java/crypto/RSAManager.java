package crypto;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class RSAManager {
    private final static int KEY_SIZE = 2048;
    
    private final static String PUBLIC_PEM_FILE_NAME = "public.pem";
    private final static String PRIVATE_PEM_FILE_NAME = "private.pem";
    
    public static KeyPair createNewPEM(String id, char[] password) {
        KeyPair keyPair = RSAUtils.generateRSAKeyPair(KEY_SIZE);
        
        byte[] passphrase = RSAUtils.createPassphrase(password);
        
        String publicPEM = RSAUtils.createPublicKeyPEM(keyPair.getPublic());
        String privatePEM = RSAUtils.createPrivateKeyPEM(keyPair.getPrivate(), passphrase);
        
        boolean savePublicPEMStatus = RSAUtils.saveToFile(id, PUBLIC_PEM_FILE_NAME, publicPEM);
        
        if (!savePublicPEMStatus)
            return null;
        
        boolean savePrivatePEMStatus = RSAUtils.saveToFile(id, PRIVATE_PEM_FILE_NAME, privatePEM);
        
        if (!savePrivatePEMStatus)
            return null;
        
        return keyPair;
    }
    
    public static KeyPair loadPEM(String id, char[] password) {
        byte[] passphrase = RSAUtils.createPassphrase(password);
        
        PublicKey publicKey = RSAUtils.readPublicKeyFromFile(id, PUBLIC_PEM_FILE_NAME);
        PrivateKey privateKey = RSAUtils.readPrivateKeyFromFile(id, PRIVATE_PEM_FILE_NAME, passphrase);
        
        if (publicKey == null || privateKey == null) {
            return null;
        }
        
        KeyPair keyPair = new KeyPair(publicKey, privateKey);
        
        return keyPair;
    }
}
