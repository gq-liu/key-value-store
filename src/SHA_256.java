import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA_256 {

    public static String hash(byte[] blockData) {
        String hashvalue = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(blockData);
            BigInteger number = new BigInteger(1, hash);
            StringBuilder hexString = new StringBuilder(number.toString(16));
            while (hexString.length() < 32) {
                hexString.insert(0, '0');
            }
            hashvalue = hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Hash block data: " + e);
        }
        return hashvalue;
    }
}
