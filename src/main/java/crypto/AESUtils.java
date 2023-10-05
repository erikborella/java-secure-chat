package crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class AESUtils {
    private static final String AES_ALGORITHM = "AES";
    private static final String AES_TRANSFORMATION = "AES/CBC/PKCS5Padding";    
    private static final String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA1";
    
    private final static byte[] IV = new byte[] {
        10, -84, -100, -72, 40, 56, 91, 96, 36, -100, 56, 101, 82, 27, 85, -105
    };
    
    private final static byte[] SALT = new byte[]{
        86, -80, -100, -74, 30, -55, 91, 96, -76, -100, 16, 101, 82, -27, 85, -113
    };
    
    public static byte[] createKey(char[] password) {
        try {
            KeySpec spec = new PBEKeySpec(password, SALT, 10000, 256);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
            
            byte[] key = keyFactory.generateSecret(spec).getEncoded();
            
            return key;
        } catch (Exception ex) {
            Logger.getLogger(RSAUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;        
    }

    public static String encrypt(String message, byte[] key) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key, AES_ALGORITHM);
            
            AlgorithmParameterSpec ivParameterSpec = new IvParameterSpec(IV);
            
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            
            byte[] encryptedBytes = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
            
            return BinaryUtils.toString(encryptedBytes);
        } catch (Exception ex) {
            Logger.getLogger(AESUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    public static String decrypt(String ciphertext, byte[] key) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key, AES_ALGORITHM);
            
            AlgorithmParameterSpec ivParameterSpec = new IvParameterSpec(IV);
            
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
            
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            Logger.getLogger(AESUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
}