package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Single point of database access (SQLite). */
public class DBConnection {
    private static final String URL = "jdbc:sqlite:mediconnect.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite driver nei! lib/ folder e jar ache kina check korun.");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}