package crypto;

import org.apache.commons.codec.binary.Base64;

public class BinaryUtils {
    
    public static String toString(byte[] bytes) {
        return new String(Base64.encodeBase64(bytes));
    }
    
    public static byte[] toByteArray(String str) {
        return Base64.decodeBase64(str);
    }
    
}
