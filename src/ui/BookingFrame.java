package ui;

import core.AppointmentService;
import dao.DoctorDAO;
import dao.SlotDAO;
import entities.Doctor;
import entities.Slot;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/** Slot booking window (FR4, FR5, FR6). */
public class BookingFrame extends JFrame {
    private final int patientId, doctorId;
    private final PatientDashboard parent;
    private final AppointmentService service = new AppointmentService();
    private final SlotDAO slotDAO = new SlotDAO();

    private final DefaultTableModel slotModel = new DefaultTableModel(
            new Object[]{"Slot ID", "Date", "Time", "Status"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable slotTable = new JTable(slotModel);
    private final JTextField dateField = Theme.textField(16);

    public BookingFrame(int patientId, int doctorId, PatientDashboard parent) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.parent = parent;

        // doctor er naam header e dekhanor jonno
        String docName = new DoctorDAO().findAll().stream()
                .filter(d -> d.getId() == doctorId).findFirst()
                .map(Doctor::getName).orElse("Doctor #" + doctorId);

        setTitle("MediConnect — Book Appointment");
        setSize(560, 480);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG);
        setLayout(new BorderLayout());

        add(Theme.headerBar("📅  Booking", "with " + docName), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setBackground(Theme.BG);
        center.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel dateCard = Theme.card("Appointment date (YYYY-MM-DD)");
        dateCard.setLayout(new BorderLayout(10, 6));
        dateField.setText(LocalDate.now().toString());
        dateCard.add(dateField, BorderLayout.CENTER);
        JButton loadBtn = Theme.primaryButton("↻  Load Slots");
        dateCard.add(loadBtn, BorderLayout.EAST);
        center.add(dateCard, BorderLayout.NORTH);

        JPanel slotCard = Theme.card("Free Slots");
        slotCard.setLayout(new BorderLayout());
        Theme.styleTable(slotTable);
        slotCard.add(new JScrollPane(slotTable), BorderLayout.CENTER);
        center.add(slotCard, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottom.setBackground(Theme.CARD);
        bottom.setBorder(new EmptyBorder(8, 10, 8, 10));
        JButton bookBtn = Theme.successButton("✅  Book Selected Slot");
        bottom.add(bookBtn);
        add(bottom, BorderLayout.SOUTH);

        loadBtn.addActionListener(e -> loadSlots());
        bookBtn.addActionListener(e -> doBook());

        loadSlots();   // DYNAMIC: open howar sathe sathe ajker slot load hoy
    }

    private void loadSlots() {
        slotModel.setRowCount(0);
        String date = dateField.getText().trim();
        if (date.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a date!");
            return;
        }
        List<Slot> slots = slotDAO.findFree(doctorId, date);
        if (slots.isEmpty())
            JOptionPane.showMessageDialog(this,
                    "No free slots available on this date!");
        for (Slot s : slots)
            slotModel.addRow(new Object[]{s.getId(), s.getDate(), s.getTime(), "FREE"});
    }

    private void doBook() {
        int row = slotTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a slot!");
            return;
        }
        int slotId = (int) slotModel.getValueAt(row, 0);
        JOptionPane.showMessageDialog(this,
                service.book(patientId, doctorId, slotId));
        loadSlots();                    // DYNAMIC: table auto-refresh
        parent.refreshAppointments();   // DYNAMIC: parent dashboard live update
    }
}