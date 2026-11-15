package entities;

/** A booking: patient + doctor + slot. Status: BOOKED/COMPLETED/CANCELLED. */
public class Appointment {
    private int id, patientId, doctorId, slotId;
    private String date, time, status;

    public Appointment(int patientId, int doctorId, int slotId, String date, String time) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.slotId = slotId;
        this.date = date;
        this.time = time;
        this.status = "BOOKED";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPatientId() { return patientId; }
    public int getDoctorId() { return doctorId; }
    public int getSlotId() { return slotId; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}