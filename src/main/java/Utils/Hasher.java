package Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 *  it is used to hash a string using the SHA-256 algorithm
 */
public class Hasher {
    /**
     * @param pass the string that must be hashed
     * @return it returns the string hashed using SHA-256
     * @throws NoSuchAlgorithmException if the SHA-256 algorithm cannot be used
     */
    public static String hash(String pass) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(pass.getBytes());
        return new String(messageDigest.digest());
    }

    /**
     * @param hash is the hash that we want to check
     * @param pass is the string that must be equal to hash after hashing
     * @return true if hash(pass) is equal to hash, false otherwise
     * @throws NoSuchAlgorithmException if the SHA-256 algorithm cannot be used
     */
    public static boolean isHashedCorrectly(String hash, String pass) throws NoSuchAlgorithmException {
        return Objects.equals(hash, hash(pass));
    }
}
