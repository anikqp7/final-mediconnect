package entities;

/** A bookable time slot owned by a doctor. is_booked: 0 free / 1 booked. */
public class Slot {
    private int id, doctorId;
    private String date, time;
    private boolean booked;

    public Slot(int id, int doctorId, String date, String time, boolean booked) {
        this.id = id;
        this.doctorId = doctorId;
        this.date = date;
        this.time = time;
        this.booked = booked;
    }

    public int getId() { return id; }
    public int getDoctorId() { return doctorId; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public boolean isBooked() { return booked; }
}