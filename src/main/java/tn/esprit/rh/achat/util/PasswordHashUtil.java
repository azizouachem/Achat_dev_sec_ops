package tn.esprit.rh.achat.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * OWASP A02:2021 – Cryptographic Failures
 * 
 * Utility class to hash passwords using SHA-256 with a random salt.
 * This prevents plaintext password storage in the database.
 * 
 * Format stored: salt:hash (both Base64-encoded)
 */
public final class PasswordHashUtil {

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;

    private PasswordHashUtil() {
        // Utility class – no instantiation
    }

    /**
     * Generates a salted SHA-256 hash of the given plaintext password.
     *
     * @param plainPassword the raw password to hash
     * @return a string in the format "salt:hash" (both Base64-encoded)
     */
    public static String hashPassword(String plainPassword) {
        try {
            // Generate random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            // Hash: SHA-256(salt + password)
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedBytes = md.digest(plainPassword.getBytes(StandardCharsets.UTF_8));

            // Encode and return as "salt:hash"
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hashedBytes);
            return saltBase64 + ":" + hashBase64;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not available", e);
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
        try {
            String[] parts = storedHash.split(":");
            if (parts.length != 2) return false;

            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] originalHash = Base64.getDecoder().decode(parts[1]);

            // Recompute hash with the same salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] newHash = md.digest(plainPassword.getBytes(StandardCharsets.UTF_8));

            // Constant-time comparison to prevent timing attacks
            return MessageDigest.isEqual(originalHash, newHash);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not available", e);
        }
    }
}
