package dao;

import entities.Slot;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** CRUD for Slots table. */
public class SlotDAO {

    public boolean save(int doctorId, String date, String time) {
        String sql = "INSERT INTO Slots (doctor_id, date, time) VALUES (?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setString(2, date);
            ps.setString(3, time);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean exists(int doctorId, String date, String time) {
        String sql = "SELECT id FROM Slots WHERE doctor_id=? AND date=? AND time=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setString(2, date);
            ps.setString(3, time);
            return ps.executeQuery().next();
        } catch (SQLException e) { return true; }
    }

    public boolean isBooked(int slotId) {
        String sql = "SELECT is_booked FROM Slots WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, slotId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt("is_booked") == 1;
            }
        } catch (SQLException e) { return true; }
    }

    public void markBooked(int slotId) { update(slotId, 1); }
    public void markFree(int slotId) { update(slotId, 0); }

    private void update(int slotId, int value) {
        String sql = "UPDATE Slots SET is_booked=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, value);
            ps.setInt(2, slotId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public Slot findById(int slotId) {
        String sql = "SELECT * FROM Slots WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, slotId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Slot(rs.getInt("id"), rs.getInt("doctor_id"),
                        rs.getString("date"), rs.getString("time"), rs.getInt("is_booked") == 1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** FR4: free slots of a doctor on a date. */
    public List<Slot> findFree(int doctorId, String date) {
        List<Slot> list = new ArrayList<>();
        String sql = "SELECT * FROM Slots WHERE doctor_id=? AND date=? AND is_booked=0";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setString(2, date);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new Slot(rs.getInt("id"), rs.getInt("doctor_id"),
                        rs.getString("date"), rs.getString("time"), false));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}