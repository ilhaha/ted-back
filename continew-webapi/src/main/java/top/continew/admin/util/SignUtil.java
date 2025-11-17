package top.continew.admin.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class SignUtil {

    private static final String SECRET = "your_secret_key";

    public static String sign(long ts) {
        return sha256(ts + SECRET);
    }

    public static boolean verify(long ts, String sign) {
        return sign(ts).equals(sign);
    }

    private static String sha256(String v) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(v.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
