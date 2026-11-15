package dao;

import entities.Doctor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/** CRUD for Doctors table. */
public class DoctorDAO {

    public boolean save(Doctor d) {
        String sql = "INSERT INTO Doctors (name, specialization, phone, available_days, user_id) VALUES (?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getName());
            ps.setString(2, d.getSpecialization());
            ps.setString(3, d.getPhone());
            ps.setString(4, d.getAvailableDays());
            if (d.getUserId() > 0) ps.setInt(5, d.getUserId());
            else ps.setNull(5, Types.INTEGER);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Doctor> findAll() {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT * FROM Doctors";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** FR3: search by name OR specialization. */
    public List<Doctor> search(String keyword) {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT * FROM Doctors WHERE name LIKE ? OR specialization LIKE ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Doctor findByUserId(int userId) {
        String sql = "SELECT * FROM Doctors WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Doctors WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    private Doctor map(ResultSet rs) throws SQLException {
        Doctor d = new Doctor(rs.getString("name"), rs.getString("specialization"),
                rs.getString("phone"), rs.getString("available_days"));
        d.setId(rs.getInt("id"));
        int uid = rs.getInt("user_id");
        if (!rs.wasNull()) d.setUserId(uid);
        return d;
    }
}