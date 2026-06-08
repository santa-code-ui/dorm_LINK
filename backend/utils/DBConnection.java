package utils;

import config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Thin JDBC connection factory. Each call returns a fresh Connection; callers
 * use try-with-resources to close it. (A full pool such as HikariCP can be
 * dropped in later without touching the DAO layer.)
 */
public final class DBConnection {

    private static boolean driverLoaded = false;

    private DBConnection() { }

    public static synchronized Connection get() throws SQLException {
        if (!driverLoaded) {
            try {
                Class.forName(DatabaseConfig.driver());
                driverLoaded = true;
            } catch (ClassNotFoundException e) {
                throw new SQLException("JDBC driver not found on classpath: "
                        + DatabaseConfig.driver()
                        + " — add mysql-connector-j.jar to WEB-INF/lib", e);
            }
        }
        return DriverManager.getConnection(
                DatabaseConfig.url(),
                DatabaseConfig.user(),
                DatabaseConfig.password());
    }
}
