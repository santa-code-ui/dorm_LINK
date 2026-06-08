package utils;

import config.DatabaseConfig;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Helper for the QR entry/exit module. Each hostel gate gets a QR sticker that
 * encodes the deep link below; scanning it opens the student entry/exit page
 * pre-filled with the gate id. Generating the actual QR image (PNG) can be done
 * with any QR library at build time and saved to frontend/assets/qr/.
 */
public final class QrUtil {

    private QrUtil() { }

    /** The URL a gate's QR code should encode. */
    public static String gateUrl(String gateCode) {
        String base = DatabaseConfig.baseUrl();
        return base + "/frontend/html/student/entry_exit_log.html?gate="
                + URLEncoder.encode(gateCode == null ? "" : gateCode, StandardCharsets.UTF_8);
    }

    /** A short reference token logged with each entry/exit row. */
    public static String token(String gateCode) {
        return "GATE-" + (gateCode == null ? "NA" : gateCode.toUpperCase());
    }
}
