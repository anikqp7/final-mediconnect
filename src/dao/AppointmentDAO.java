package dao;

import entities.Appointment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** CRUD for Appointments table (with JOIN views). */
public class AppointmentDAO {

    public boolean save(Appointment a) {
        String sql = "INSERT INTO Appointments (patient_id, doctor_id, slot_id, date, time, status) VALUES (?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, a.getPatientId());
            ps.setInt(2, a.getDoctorId());
            ps.setInt(3, a.getSlotId());
            ps.setString(4, a.getDate());
            ps.setString(5, a.getTime());
            ps.setString(6, a.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public Appointment findById(int id) {
        String sql = "SELECT * FROM Appointments WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Appointment a = new Appointment(rs.getInt("patient_id"),
                            rs.getInt("doctor_id"), rs.getInt("slot_id"),
                            rs.getString("date"), rs.getString("time"));
                    a.setId(rs.getInt("id"));
                    a.setStatus(rs.getString("status"));
                    return a;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE Appointments SET status=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    /** FR9: patient history with doctor names. */
    public List<String[]> findByPatient(int patientId) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT a.id, a.date, a.time, d.name, a.status " +
                     "FROM Appointments a JOIN Doctors d ON a.doctor_id = d.id " +
                     "WHERE a.patient_id = ? ORDER BY a.date, a.time";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("date"), rs.getString("time"),
                        rs.getString("name"), rs.getString("status")});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** FR11: doctor's patient list (id first — for marking COMPLETED). */
    public List<String[]> findByDoctor(int doctorId) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT a.id, p.name, p.phone, a.date, a.time, a.status " +
                     "FROM Appointments a JOIN Patients p ON a.patient_id = p.id " +
                     "WHERE a.doctor_id = ? ORDER BY a.date, a.time";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("name"), rs.getString("phone"),
                        rs.getString("date"), rs.getString("time"),
                        rs.getString("status")});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** FR14: admin — all appointments. */
    public List<String[]> findAll() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT a.id, p.name, d.name, a.date, a.time, a.status " +
                     "FROM Appointments a " +
                     "JOIN Patients p ON a.patient_id = p.id " +
                     "JOIN Doctors d ON a.doctor_id = d.id ORDER BY a.date, a.time";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new String[]{
                    String.valueOf(rs.getInt(1)), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6)});
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** FR15: daily report (date, count). */
    public List<String[]> dailyReport() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT date, COUNT(*) AS total FROM Appointments GROUP BY date ORDER BY date";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new String[]{
                    rs.getString("date"), String.valueOf(rs.getInt("total"))});
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** FR15: weekly report (ISO week, count). */
    public List<String[]> weeklyReport() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT strftime('%Y-W%W', date) AS week, COUNT(*) AS total " +
                     "FROM Appointments GROUP BY week ORDER BY week";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new String[]{
                    rs.getString("week"), String.valueOf(rs.getInt("total"))});
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}