package core;

import dao.AppointmentDAO;
import dao.SlotDAO;
import entities.Appointment;
import entities.Slot;
import java.util.List;

/** Booking business rules (FR5-FR9). */
public class AppointmentService {
    private final SlotDAO slotDAO = new SlotDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    /** FR5 + FR6: book with double-booking prevention. */
    public String book(int patientId, int doctorId, int slotId) {
        Slot slot = slotDAO.findById(slotId);
        if (slot == null) return "❌ Slot not found!";
        if (slotDAO.isBooked(slotId)) return "❌ This slot is already booked!";

        Appointment appt = new Appointment(patientId, doctorId, slotId,
                slot.getDate(), slot.getTime());
        if (!appointmentDAO.save(appt)) return "❌ Booking failed!";
        slotDAO.markBooked(slotId);
        return "✅ Appointment booked successfully!";
    }

    /** FR7: cancel — slot becomes instantly free. */
    public String cancel(int appointmentId) {
        Appointment a = appointmentDAO.findById(appointmentId);
        if (a == null) return "❌ Appointment not found!";
        if (!"BOOKED".equals(a.getStatus())) return "❌ This appointment is not active!";

        appointmentDAO.updateStatus(appointmentId, "CANCELLED");
        slotDAO.markFree(a.getSlotId());
        return "✅ Appointment cancelled! The slot is now free.";
    }

    /** FR8: reschedule — frees old slot, then patient books a new one. */
    public String reschedule(int appointmentId) {
        String msg = cancel(appointmentId);
        if (msg.startsWith("✅"))
            return "✅ Old slot freed! Now select a new slot to book.";
        return msg;
    }

    /** Doctor marks an appointment as completed. */
    public String complete(int appointmentId) {
        Appointment a = appointmentDAO.findById(appointmentId);
        if (a == null) return "❌ Appointment not found!";
        if (!"BOOKED".equals(a.getStatus())) return "❌ This appointment is not active!";
        appointmentDAO.updateStatus(appointmentId, "COMPLETED");
        return "✅ Marked as COMPLETED.";
    }

    /** FR9: patient's appointment history. */
    public List<String[]> getHistory(int patientId) {
        return appointmentDAO.findByPatient(patientId);
    }
}