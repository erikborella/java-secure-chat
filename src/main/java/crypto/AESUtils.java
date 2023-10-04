package crypto;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.spec.KeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class AESUtils {
    private final static byte[] salt = new byte[]{
        86, -80, -100, -74, 30, -55, 91, 96, -76, -100, 16, 101, 82, -27, 85, -113
    };
    
    private static final String iv = "encryptionIntVec";
    
    public static byte[] createPassphrase(char[] password) {
        try {
            KeySpec spec = new PBEKeySpec(password, salt, 10000, 256);;
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            
            byte[] key = keyFactory.generateSecret(spec).getEncoded();
            
            return key;
        } catch (Exception ex) {
            Logger.getLogger(RSAUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;        
    }
    
    public static String encrypt(byte[] passphrase, String message) {
        try {
            Key aesKey = new SecretKeySpec(passphrase, "AES");
            
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            
            byte[] encryptedKeyBytes = cipher.doFinal(message.getBytes());
            
            String encryptedMessage = BinaryUtils.toString(encryptedKeyBytes);
            
            return encryptedMessage;
        } catch (Exception ex) {
            Logger.getLogger(AESUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    public static String decrypt(byte[] passphrase, String encryptedMessage) {
        try {
            Key aesKey = new SecretKeySpec(passphrase, "AES");
            
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            
            byte[] encryptedMessageBytes = BinaryUtils.toByteArray(encryptedMessage);
            
            byte[] encryptedKeyBytes = cipher.doFinal(encryptedMessageBytes);
            
            String message = BinaryUtils.toString(encryptedKeyBytes);
            
            return message;
        } catch (Exception ex) {
            Logger.getLogger(AESUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
}
