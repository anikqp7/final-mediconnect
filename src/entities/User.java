package entities;

/** System login account — credentials + role (PATIENT/DOCTOR/ADMIN). */
public class User {
    private int id;
    private String email, password, role;

    public User(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}