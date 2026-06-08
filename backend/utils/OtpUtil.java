package utils;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Email-OTP support for the forgot/reset password flow.
 *
 * OTPs are kept in an in-memory store keyed by "{role}:{email}", valid for
 * {@link #TTL_MS}. {@link #send} prints the code to the server log; wire a real
 * SMTP / transactional-email provider where indicated to actually deliver it.
 */
public final class OtpUtil {

    public static final long TTL_MS = 10 * 60 * 1000L; // 10 minutes

    private static final SecureRandom RNG = new SecureRandom();
    private static final Map<String, Entry> STORE = new ConcurrentHashMap<>();

    private OtpUtil() { }

    private static final class Entry {
        final String code; final long expiresAt;
        Entry(String code, long expiresAt) { this.code = code; this.expiresAt = expiresAt; }
    }

    private static String key(String role, String email) {
        return (role == null ? "" : role.toUpperCase()) + ":" + (email == null ? "" : email.toLowerCase());
    }

    /** Generates, stores and "sends" a 6-digit OTP. Returns the code (for logging/testing). */
    public static String generateAndSend(String role, String email) {
        String code = String.format("%06d", RNG.nextInt(1_000_000));
        STORE.put(key(role, email), new Entry(code, System.currentTimeMillis() + TTL_MS));
        send(email, code);
        return code;
    }

    public static boolean verify(String role, String email, String otp) {
        Entry e = STORE.get(key(role, email));
        if (e == null) return false;
        if (System.currentTimeMillis() > e.expiresAt) { STORE.remove(key(role, email)); return false; }
        boolean ok = e.code.equals(otp);
        if (ok) STORE.remove(key(role, email)); // single-use
        return ok;
    }

    /**
     * Delivery hook. Replace the body with a real mailer, e.g. Jakarta Mail:
     *
     *   Session s = Session.getInstance(smtpProps, auth);
     *   MimeMessage m = new MimeMessage(s);
     *   m.setSubject("Your dorm_LINK reset code");
     *   m.setText("Your OTP is " + code + " (valid 10 minutes).");
     *   Transport.send(m);
     */
    private static void send(String email, String code) {
        System.out.println("[OTP] -> " + email + " : " + code + " (valid 10 min)");
    }
}
