package crypto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class RSAUtils {
    
    private final static byte[] salt = new byte[]{
        86, -80, -100, -74, 30, -55, 91, 96, -76, -100, 16, 101, 82, -27, 85, -113
    };
    
    public static KeyPair generateRSAKeyPair(int keySize) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(keySize);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(RSAUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    
    public static byte[] createPasspharese(char[] password) {
        try {
            KeySpec spec = new PBEKeySpec(password, salt, 1000000, 256);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            
            byte[] key = keyFactory.generateSecret(spec).getEncoded();
            
            return key;
        } catch (Exception ex) {
            Logger.getLogger(RSAUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;        
    }
    
    public static String createPrivateKeyPEM(PrivateKey privateKey, byte[] passphrase) {
        try {
            // Convert the private key to PKCS#8 format
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
            
            // Create a PEM-encoded private key with encryption
            Key aesKey = new SecretKeySpec(passphrase, "AES");
            
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encryptedKeyBytes = cipher.doFinal(pkcs8EncodedKeySpec.getEncoded());

            String base64EncryptedKey = Base64.encodeBase64String(encryptedKeyBytes);
            return "-----BEGIN ENCRYPTED PRIVATE KEY-----\n" +
                    base64EncryptedKey + "\n" +
                    "-----END ENCRYPTED PRIVATE KEY-----\n";
        } catch (Exception ex) {
            Logger.getLogger(RSAUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    public static String createPublicKeyPEM(PublicKey publicKey) {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
        
        String base64PublicKey = Base64.encodeBase64String(x509EncodedKeySpec.getEncoded());
        return "-----BEGIN PUBLIC KEY-----\n" +
                base64PublicKey + "\n" +
                "-----END PUBLIC KEY-----\n";
    }
    
    public static PrivateKey readPrivateKeyFromPEM(String folderPath, String fileName, byte[] passphrase) {
        File filePath = new File(folderPath, fileName);

        try (BufferedReader privateKeyReader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder privateKeyPEM = new StringBuilder();
            String line;
            while ((line = privateKeyReader.readLine()) != null) {
                if (line.contains("BEGIN ENCRYPTED PRIVATE KEY") || line.contains("END ENCRYPTED PRIVATE KEY")) {
                    continue;
                }
                privateKeyPEM.append(line);
            }

            byte[] privateKeyBytes = Base64.decodeBase64(privateKeyPEM.toString());

            Key aesKey = new SecretKeySpec(passphrase, "AES");
            
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            
            byte[] decryptedPrivateKey = cipher.doFinal(privateKeyBytes);

            try {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(decryptedPrivateKey);
                return keyFactory.generatePrivate(privateKeySpec);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } catch (Exception ex) {
            Logger.getLogger(RSAUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    public static PublicKey readPublicKeyFromPEM(String folderPath, String fileName) {
        File filePath = new File(folderPath, fileName);
        
        try (BufferedReader publicKeyReader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder publicKeyPEM = new StringBuilder();
            String line;
            while ((line = publicKeyReader.readLine()) != null) {
                if (line.contains("BEGIN PUBLIC KEY") || line.contains("END PUBLIC KEY")) {
                    continue;
                }
                publicKeyPEM.append(line);
            }

            byte[] publicKeyBytes = Base64.decodeBase64(publicKeyPEM.toString());

            try {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
                return keyFactory.generatePublic(publicKeySpec);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } catch (Exception ex) {
            Logger.getLogger(RSAUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    public static boolean saveToFile(String folderPath, String fileName, String content) {
        try {
            File folderFile = new File(folderPath);
            
            if (!folderFile.exists()) {
                folderFile.mkdirs();
            }
            
            File file = new File(folderPath, fileName);
            
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
            
            return true;
        } catch (Exception ex) {
            Logger.getLogger(RSAUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        return false;
    };
}
