package tn.esprit.rh.achat.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;

/**
 * OWASP A02:2021 – Cryptographic Failures
 * 
 * Utility class to hash passwords using PBKDF2 with HMAC-SHA256.
 * This satisfies SonarQube's strict security standard (Rating A)
 * by using a slow, secure, key-stretching hashing algorithm.
 * 
 * Format stored: salt:hash (both Base64-encoded)
 */
public final class PasswordHashUtil {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256; // 256 bits / 32 bytes
    private static final int SALT_LENGTH = 16;

    private PasswordHashUtil() {
        // Utility class – no instantiation
    }

    /**
     * Generates a salted PBKDF2 hash of the given plaintext password.
     *
     * @param plainPassword the raw password to hash
     * @return a string in the format "salt:hash" (both Base64-encoded)
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        try {
            // Generate random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            // Hash using PBKDF2
            PBEKeySpec spec = new PBEKeySpec(plainPassword.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hashedBytes = skf.generateSecret(spec).getEncoded();

            // Encode and return as "salt:hash"
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hashedBytes);
            return saltBase64 + ":" + hashBase64;

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Hashing failed: " + e.getMessage(), e);
        }
    }

    /**
     * Verifies a plaintext password against a stored "salt:hash" value.
     *
     * @param plainPassword the raw password to verify
     * @param storedHash    the stored hash in "salt:hash" format
     * @return true if the password matches
     */
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) {
            return false;
        }
        try {
            String[] parts = storedHash.split(":");
            if (parts.length != 2) return false;

            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] originalHash = Base64.getDecoder().decode(parts[1]);

            // Recompute the hash with the stored salt
            PBEKeySpec spec = new PBEKeySpec(plainPassword.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] newHash = skf.generateSecret(spec).getEncoded();

            // Constant-time comparison to prevent timing attacks
            return MessageDigest.isEqual(originalHash, newHash);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException e) {
            return false;
        }
    }
}
