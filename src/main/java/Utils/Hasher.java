package Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class Hasher {
    public static String hash(String pass) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(pass.getBytes());
        return new String(messageDigest.digest());
    }

    public static boolean isHashedCorrectly(String hash, String pass) throws NoSuchAlgorithmException {
        return Objects.equals(hash, hash(pass));
    }
}
