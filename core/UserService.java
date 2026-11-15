package core;

import dao.PatientDAO;
import dao.UserDAO;
import entities.Patient;
import entities.User;

/** Authentication + registration logic (FR1, FR2). */
public class UserService {
    private final UserDAO userDAO = new UserDAO();
    private final PatientDAO patientDAO = new PatientDAO();

    /** FR2: role-based login with hashed password comparison. */
    public User login(String email, String password) {
        if (email == null || email.trim().isEmpty()
                || password == null || password.isEmpty()) {
            return null;
        }
        User user = userDAO.findByEmail(email.trim());
        if (user == null) return null;
        if (PasswordUtil.hash(password).equals(user.getPassword())) return user;
        return null;
    }

    /** FR1: patient registration — creates account + profile. */
    public String registerPatient(String name, int age, String gender,
                                  String phone, String email, String password) {
        if (name == null || name.trim().isEmpty()
                || phone == null || phone.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || password == null || password.isEmpty()) {
            return "❌ Please fill in all fields!";
        }
        if (!Validator.isEmail(email)) return "❌ Invalid email format!";
        if (!Validator.isPhone(phone)) return "❌ Phone must be exactly 11 digits!";
        if (password.length() < 4) return "❌ Password must be at least 4 characters!";
        if (age <= 0 || age > 120) return "❌ Invalid age!";
        if (userDAO.findByEmail(email.trim()) != null)
            return "❌ This email is already registered!";

        userDAO.save(email.trim(), PasswordUtil.hash(password), "PATIENT");
        User user = userDAO.findByEmail(email.trim());
        patientDAO.save(new Patient(name.trim(), age, gender, phone.trim(), user.getId()));
        return "✅ Registration successful! Please login now.";
    }
}