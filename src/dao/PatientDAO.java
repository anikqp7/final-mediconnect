package dao;

import entities.Patient;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** CRUD for Patients table. */
public class PatientDAO {

    public boolean save(Patient p) {
        String sql = "INSERT INTO Patients (name, age, gender, phone, user_id) VALUES (?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setInt(2, p.getAge());
            ps.setString(3, p.getGender());
            ps.setString(4, p.getPhone());
            ps.setInt(5, p.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public Patient findByUserId(int userId) {
        String sql = "SELECT * FROM Patients WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Patient p = new Patient(rs.getString("name"), rs.getInt("age"),
                            rs.getString("gender"), rs.getString("phone"), userId);
                    p.setId(rs.getInt("id"));
                    return p;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Admin view (FR13): all patients with their email. */
    public List<String[]> findAll() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT p.id, p.name, p.age, p.gender, p.phone, u.email " +
                     "FROM Patients p LEFT JOIN Users u ON p.user_id = u.id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("name"),
                        String.valueOf(rs.getInt("age")),
                        rs.getString("gender"),
                        rs.getString("phone"),
                        rs.getString("email")});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Admin delete (FR13). */
    public boolean delete(int id) {
        String sql = "DELETE FROM Patients WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
}