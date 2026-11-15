package dao;

import java.sql.Connection;
import java.sql.Statement;

/** Run ONCE — creates all 5 tables. */
public class TableCreator {
    public static void main(String[] args) throws Exception {
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {

            st.executeUpdate("CREATE TABLE IF NOT EXISTS Users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS Patients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, age INTEGER, gender TEXT, phone TEXT, " +
                "user_id INTEGER)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS Doctors (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, specialization TEXT, phone TEXT, " +
                "available_days TEXT, user_id INTEGER)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS Slots (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "doctor_id INTEGER, date TEXT, time TEXT, " +
                "is_booked INTEGER DEFAULT 0)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS Appointments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "patient_id INTEGER, doctor_id INTEGER, slot_id INTEGER, " +
                "date TEXT, time TEXT, status TEXT DEFAULT 'BOOKED')");

            System.out.println("=== 5 tables created successfully! ===");
        }
    }
}