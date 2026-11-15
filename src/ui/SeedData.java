package ui;

import core.PasswordUtil;
import dao.DoctorDAO;
import dao.PatientDAO;
import dao.SlotDAO;
import dao.UserDAO;
import entities.Doctor;
import entities.Patient;
import entities.User;

import java.time.LocalDate;
import java.util.List;

/** Run ONCE after TableCreator — inserts demo data for testing. */
public class SeedData {
    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();
        PatientDAO patientDAO = new PatientDAO();
        DoctorDAO doctorDAO = new DoctorDAO();
        SlotDAO slotDAO = new SlotDAO();

        // 1) Admin
        if (userDAO.findByEmail("admin@medi.com") == null)
            userDAO.save("admin@medi.com", PasswordUtil.hash("admin123"), "ADMIN");

        // 2) Patients (account + profile)
        seedPatient(userDAO, patientDAO, "Rahim Uddin", 30, "Male",
                "01711111111", "rahim@gmail.com", "1234");
        seedPatient(userDAO, patientDAO, "Ayesha Siddika", 26, "Female",
                "01722222222", "ayesha@gmail.com", "1234");

        // 3) Doctors (account + linked profile)
        seedDoctor(userDAO, doctorDAO, "Dr. Abdul Karim", "Cardiology",
                "01733333333", "karim@medi.com", "karim123");
        seedDoctor(userDAO, doctorDAO, "Dr. Salma Begum", "Dermatology",
                "01744444444", "salma@medi.com", "salma123");
        seedDoctor(userDAO, doctorDAO, "Dr. Jahid Hasan", "Medicine",
                "01755555555", "jahid@medi.com", "jahid123");

        // 4) Slots — ajke + kalke, prottek doctor er 4ta kore
        String today = LocalDate.now().toString();
        String tomorrow = LocalDate.now().plusDays(1).toString();
        String[] times = {"10:00", "10:30", "11:00", "11:30"};

        List<Doctor> doctors = doctorDAO.findAll();
        for (Doctor d : doctors) {
            for (String t : times) {
                if (!slotDAO.exists(d.getId(), today, t)) slotDAO.save(d.getId(), today, t);
                if (!slotDAO.exists(d.getId(), tomorrow, t)) slotDAO.save(d.getId(), tomorrow, t);
            }
        }

        System.out.println("=== DEMO DATA READY ===");
        System.out.println("Admin   : admin@medi.com / admin123");
        System.out.println("Patient : rahim@gmail.com / 1234");
        System.out.println("Patient : ayesha@gmail.com / 1234");
        System.out.println("Doctor  : karim@medi.com / karim123");
        System.out.println("Doctor  : salma@medi.com / salma123");
        System.out.println("Doctor  : jahid@medi.com / jahid123");
        System.out.println("Slots   : aj + kalke, 3 jon doctor er 4ta kore");
    }

    private static void seedPatient(UserDAO userDAO, PatientDAO patientDAO,
            String name, int age, String gender, String phone, String email, String pass) {
        if (userDAO.findByEmail(email) != null) return;
        userDAO.save(email, PasswordUtil.hash(pass), "PATIENT");
        User u = userDAO.findByEmail(email);
        patientDAO.save(new Patient(name, age, gender, phone, u.getId()));
    }

    private static void seedDoctor(UserDAO userDAO, DoctorDAO doctorDAO,
            String name, String spec, String phone, String email, String pass) {
        if (userDAO.findByEmail(email) != null) return;
        userDAO.save(email, PasswordUtil.hash(pass), "DOCTOR");
        User u = userDAO.findByEmail(email);
        Doctor d = new Doctor(name, spec, phone, "Sun-Thu");
        d.setUserId(u.getId());
        doctorDAO.save(d);
    }
}