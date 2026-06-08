package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Central configuration holder. Reads db.properties once from the classpath.
 * Deploy db.properties to WEB-INF/classes/ (or backend/config on the classpath).
 */
public final class DatabaseConfig {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in != null) {
                PROPS.load(in);
            } else {
                // Fallback defaults for first-run / local dev
                PROPS.setProperty("db.url",
                        "jdbc:mysql://localhost:3306/dorm_link?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
                PROPS.setProperty("db.user", "root");
                PROPS.setProperty("db.password", "");
                PROPS.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
                PROPS.setProperty("app.baseUrl", "http://localhost:8080/dormlink");
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private DatabaseConfig() { }

    public static String get(String key)              { return PROPS.getProperty(key); }
    public static String get(String key, String def)  { return PROPS.getProperty(key, def); }

    public static String url()      { return get("db.url"); }
    public static String user()     { return get("db.user"); }
    public static String password() { return get("db.password"); }
    public static String driver()   { return get("db.driver"); }
    public static String baseUrl()  { return get("app.baseUrl"); }
}
