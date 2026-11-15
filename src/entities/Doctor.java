package entities;

/** Doctor profile + schedule info, linked to User account. */
public class Doctor {
    private int id, userId;
    private String name, specialization, phone, availableDays;

    public Doctor(String name, String specialization, String phone, String availableDays) {
        this.name = name;
        this.specialization = specialization;
        this.phone = phone;
        this.availableDays = availableDays;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public String getSpecialization() { return specialization; }
    public String getPhone() { return phone; }
    public String getAvailableDays() { return availableDays; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}