package utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Password hashing for dorm_LINK.
 *
 * Stored format:  {salt}:{hex(sha256(salt + password))}
 *
 * This exact scheme is reproduced in database/seed_data.sql so the seeded
 * demo accounts validate against this code. For production prefer a slow,
 * memory-hard KDF (bcrypt / Argon2); the storage format here is intentionally
 * simple and dependency-free.
 */
public final class PasswordUtil {

    private static final SecureRandom RNG = new SecureRandom();

    private PasswordUtil() { }

    public static String hash(String password) {
        byte[] saltBytes = new byte[12];
        RNG.nextBytes(saltBytes);
        String salt = toHex(saltBytes);
        return salt + ":" + sha256Hex(salt + password);
    }

    /** Hash with a known salt — used by the seed-hash generator so values match the SQL. */
    public static String hashWithSalt(String salt, String password) {
        return salt + ":" + sha256Hex(salt + password);
    }

    public static boolean verify(String password, String stored) {
        if (stored == null || !stored.contains(":")) return false;
        String[] parts = stored.split(":", 2);
        String salt = parts[0];
        String expected = parts[1];
        String actual = sha256Hex(salt + password);
        return constantTimeEquals(expected, actual);
    }

    private static String sha256Hex(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return toHex(md.digest(s.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) return false;
        int r = 0;
        for (int i = 0; i < a.length(); i++) r |= a.charAt(i) ^ b.charAt(i);
        return r == 0;
    }

    /** Utility entry point to print seed hashes (used while authoring seed_data.sql). */
    public static void main(String[] args) {
        // Salts here match those committed in seed_data.sql.
        System.out.println("admin    / admin123   -> " + hashWithSalt("a1b2c3d4", "admin123"));
        System.out.println("warden   / warden123  -> " + hashWithSalt("e5f6a7b8", "warden123"));
        System.out.println("CS001    / student123 -> " + hashWithSalt("s001salt", "student123"));
    }
}
