package ui;

import core.AppointmentService;
import core.DoctorService;
import dao.AppointmentDAO;
import dao.SlotDAO;
import entities.Doctor;
import entities.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/** Doctor home — live stats, schedule manager, patient list (FR10, FR11). */
public class DoctorDashboard extends JFrame {
    private Doctor doctor;
    private final DoctorService doctorService = new DoctorService();
    private final AppointmentService apptService = new AppointmentService();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final SlotDAO slotDAO = new SlotDAO();

    // ── live stat values ──
    private final JLabel statPatients = Theme.statValue();
    private final JLabel statBooked   = Theme.statValue();
    private final JLabel statFree     = Theme.statValue();

    private final JTextField dateField = Theme.textField(12);
    private final JTextField timeField = Theme.textField(12);
    private final DefaultTableModel patientModel = new DefaultTableModel(
            new Object[]{"ID", "Patient", "Phone", "Date", "Time", "Status"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable patientTable = new JTable(patientModel);

    public DoctorDashboard(User user) {
        doctor = doctorService.getDoctorByUserId(user.getId());

        setTitle("MediConnect — Doctor Dashboard");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG);
        setLayout(new BorderLayout());

        String today = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH));
        add(Theme.headerBar("👨‍⚕️  Doctor Panel",
                (doctor != null ? doctor.getName() + " — " + doctor.getSpecialization()
                                : "Doctor") + "   •   " + today), BorderLayout.NORTH);

        // ── center: stats + slot form + patient table ──
        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setBackground(Theme.BG);
        center.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel stats = new JPanel(new GridLayout(1, 3, 10, 10));
        stats.setOpaque(false);
        stats.add(Theme.statCard("TODAY'S PATIENTS", statPatients, Theme.PRIMARY));
        stats.add(Theme.statCard("TOTAL ACTIVE", statBooked, Theme.SUCCESS));
        stats.add(Theme.statCard("FREE SLOTS TODAY", statFree, Theme.DANGER));
        center.add(stats, BorderLayout.NORTH);

        JPanel middle = new JPanel(new BorderLayout(0, 10));
        middle.setOpaque(false);

        JPanel slotCard = Theme.card("Add available slot (FR10)");
        slotCard.setLayout(new BorderLayout(10, 6));
        dateField.setText(LocalDate.now().toString());   // DYNAMIC: aajker date default
        timeField.setText("10:00");
        JPanel slotForm = new JPanel(new GridLayout(1, 2, 10, 0));
        slotForm.setOpaque(false);
        JPanel d = new JPanel(new BorderLayout(4, 0));
        d.setOpaque(false);
        d.add(Theme.fieldLabel("DATE"), BorderLayout.WEST);
        d.add(dateField, BorderLayout.CENTER);
        JPanel t = new JPanel(new BorderLayout(4, 0));
        t.setOpaque(false);
        t.add(Theme.fieldLabel("TIME"), BorderLayout.WEST);
        t.add(timeField, BorderLayout.CENTER);
        slotForm.add(d);
        slotForm.add(t);
        slotCard.add(slotForm, BorderLayout.CENTER);
        JButton addSlotBtn = Theme.primaryButton("➕  Add Slot");
        slotCard.add(addSlotBtn, BorderLayout.EAST);
        middle.add(slotCard, BorderLayout.NORTH);

        JPanel tableCard = Theme.card("My Appointments");
        tableCard.setLayout(new BorderLayout());
        Theme.styleTable(patientTable);
        tableCard.add(new JScrollPane(patientTable), BorderLayout.CENTER);
        middle.add(tableCard, BorderLayout.CENTER);

        center.add(middle, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // ── bottom actions ──
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottom.setBackground(Theme.CARD);
        bottom.setBorder(new EmptyBorder(8, 10, 8, 10));
        JButton viewBtn = Theme.primaryButton("👥  Load My Patients");
        JButton doneBtn = Theme.successButton("✅  Mark Completed");
        JButton logoutBtn = Theme.mutedButton("Logout");
        bottom.add(viewBtn);
        bottom.add(doneBtn);
        bottom.add(logoutBtn);
        add(bottom, BorderLayout.SOUTH);

        addSlotBtn.addActionListener(e -> {
            if (doctor == null) {
                JOptionPane.showMessageDialog(this,
                        "Doctor profile not found! Please contact the admin.");
                return;
            }
            JOptionPane.showMessageDialog(this, doctorService.addSlot(doctor.getId(),
                    dateField.getText().trim(), timeField.getText().trim()));
            loadPatients();   // DYNAMIC: stats + table live update
        });
        viewBtn.addActionListener(e -> loadPatients());
        doneBtn.addActionListener(e -> markCompleted());
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        loadPatients();   // DYNAMIC: open howar sathe sathe data load
    }

    /** Loads table + refreshes live stats. */
    private void loadPatients() {
        patientModel.setRowCount(0);
        if (doctor == null) return;

        List<String[]> rows = appointmentDAO.findByDoctor(doctor.getId());
        for (String[] row : rows) patientModel.addRow(row);

        String today = LocalDate.now().toString();
        statPatients.setText(String.valueOf(rows.stream()
                .filter(r -> "BOOKED".equals(r[5]) && today.equals(r[3])).count()));
        statBooked.setText(String.valueOf(rows.stream()
                .filter(r -> "BOOKED".equals(r[5])).count()));
        statFree.setText(String.valueOf(
                slotDAO.findFree(doctor.getId(), today).size()));
    }

    private void markCompleted() {
        int row = patientTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment!");
            return;
        }
        int apptId = Integer.parseInt((String) patientModel.getValueAt(row, 0));
        JOptionPane.showMessageDialog(this, apptService.complete(apptId));
        loadPatients();
    }
}