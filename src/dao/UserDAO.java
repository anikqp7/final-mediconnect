package dao;

import entities.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** CRUD for Users table. */
public class UserDAO {

    public boolean save(String email, String hashedPassword, String role) {
        String sql = "INSERT INTO Users (email, password, role) VALUES (?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, hashedPassword);
            ps.setString(3, role);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User findByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User(rs.getString("email"),
                            rs.getString("password"), rs.getString("role"));
                    u.setId(rs.getInt("id"));
                    return u;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}