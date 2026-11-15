package entities;

/** Patient profile, linked to a User account by user_id. */
public class Patient {
    private int id, age, userId;
    private String name, gender, phone;

    public Patient(String name, int age, String gender, String phone, int userId) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
        this.userId = userId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getPhone() { return phone; }
    public int getUserId() { return userId; }
}