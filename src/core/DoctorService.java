package core;

import dao.DoctorDAO;
import dao.SlotDAO;
import dao.UserDAO;
import entities.Doctor;
import entities.User;
import java.util.List;

/** Doctor management + schedule rules (FR10, FR12). */
public class DoctorService {
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final UserDAO userDAO = new UserDAO();
    private final SlotDAO slotDAO = new SlotDAO();

    public List<Doctor> getAllDoctors() { return doctorDAO.findAll(); }

    public Doctor getDoctorByUserId(int userId) {
        return doctorDAO.findByUserId(userId);
    }

    /** FR12: add doctor — auto-creates a linked login account. */
    public String addDoctorWithAccount(String name, String specialization,
                                       String phone, String email, String password) {
        if (name.isEmpty() || specialization.isEmpty()
                || email.isEmpty() || password.isEmpty())
            return "❌ Please fill in all fields!";
        if (!Validator.isEmail(email)) return "❌ Invalid email format!";
        if (userDAO.findByEmail(email) != null) return "❌ This email already exists!";

        userDAO.save(email, PasswordUtil.hash(password), "DOCTOR");
        User u = userDAO.findByEmail(email);
        Doctor d = new Doctor(name, specialization, phone, "Sun-Thu");
        d.setUserId(u.getId());
        return doctorDAO.save(d)
                ? "✅ Doctor added! Login: " + email + " / " + password
                : "❌ Failed to add doctor!";
    }

    public boolean deleteDoctor(int doctorId) {
        return doctorDAO.delete(doctorId);
    }

    /** FR10: add available slot with strict validation. */
    public String addSlot(int doctorId, String date, String time) {
        if (date.isEmpty() || time.isEmpty()) return "❌ Please enter date and time!";
        if (!Validator.isDate(date)) return "❌ Invalid date format! Example: 2025-06-10";
        if (!Validator.isTime(time)) return "❌ Invalid time format! Example: 10:30";
        if (slotDAO.exists(doctorId, date, time)) return "❌ This slot already exists!";
        return slotDAO.save(doctorId, date, time) ? "✅ Slot added!" : "❌ Failed!";
    }
}