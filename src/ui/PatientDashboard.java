package ui;

import core.AppointmentService;
import dao.DoctorDAO;
import dao.PatientDAO;
import entities.Doctor;
import entities.Patient;
import entities.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/** Patient home — live stats, instant search, booking history (FR3-FR9). */
public class PatientDashboard extends JFrame {
    private final Patient patient;
    private final AppointmentService apptService = new AppointmentService();
    private final DoctorDAO doctorDAO = new DoctorDAO();

    // ── live stat values (update after every action) ──
    private final JLabel statDoctors = Theme.statValue();
    private final JLabel statTotal    = Theme.statValue();
    private final JLabel statActive   = Theme.statValue();

    private final DefaultTableModel doctorModel = new DefaultTableModel(
            new Object[]{"ID", "Name", "Specialization", "Phone"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final DefaultTableModel apptModel = new DefaultTableModel(
            new Object[]{"Appt ID", "Date", "Time", "Doctor", "Status"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable doctorTable = new JTable(doctorModel);
    private final JTable apptTable = new JTable(apptModel);
    private final JTextField searchField = Theme.textField(22);

    public PatientDashboard(User user) {
        this.patient = new PatientDAO().findByUserId(user.getId());

        setTitle("MediConnect — Patient Dashboard");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG);
        setLayout(new BorderLayout());

        String today = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH));
        add(Theme.headerBar("👤  Patient Panel",
                (patient != null ? patient.getName() : user.getEmail())
                        + "   •   " + today), BorderLayout.NORTH);

        // ── center: stats + tables ──
        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setBackground(Theme.BG);
        center.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel stats = new JPanel(new GridLayout(1, 3, 10, 10));
        stats.setOpaque(false);
        stats.add(Theme.statCard("TOTAL DOCTORS", statDoctors, Theme.PRIMARY));
        stats.add(Theme.statCard("MY APPOINTMENTS", statTotal, Theme.PRIMARY));
        stats.add(Theme.statCard("ACTIVE BOOKINGS", statActive, Theme.SUCCESS));
        center.add(stats, BorderLayout.NORTH);

        JPanel tables = new JPanel(new GridLayout(2, 1, 10, 10));
        tables.setOpaque(false);

        JPanel docCard = Theme.card("Available Doctors");
        docCard.setLayout(new BorderLayout(8, 6));
        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setOpaque(false);
        searchRow.add(searchField, BorderLayout.CENTER);
        JButton clearBtn = Theme.linkButton("clear");
        searchRow.add(clearBtn, BorderLayout.EAST);
        docCard.add(searchRow, BorderLayout.NORTH);
        Theme.styleTable(doctorTable);
        docCard.add(new JScrollPane(doctorTable), BorderLayout.CENTER);

        JPanel apptCard = Theme.card("My Appointments");
        apptCard.setLayout(new BorderLayout());
        Theme.styleTable(apptTable);
        apptCard.add(new JScrollPane(apptTable), BorderLayout.CENTER);

        tables.add(docCard);
        tables.add(apptCard);
        center.add(tables, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        // ── bottom actions ──
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottom.setBackground(Theme.CARD);
        bottom.setBorder(new EmptyBorder(8, 10, 8, 10));
        JButton bookBtn = Theme.successButton("📅  Book Appointment");
        JButton reschedBtn = Theme.primaryButton("🔄  Reschedule");
        JButton cancelBtn = Theme.dangerButton("❌  Cancel Selected");
        JButton logoutBtn = Theme.mutedButton("Logout");
        bottom.add(bookBtn);
        bottom.add(reschedBtn);
        bottom.add(cancelBtn);
        bottom.add(logoutBtn);
        add(bottom, BorderLayout.SOUTH);

        // ── DYNAMIC: instant search while typing ──
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { liveSearch(); }
            public void removeUpdate(DocumentEvent e)  { liveSearch(); }
            public void changedUpdate(DocumentEvent e) { liveSearch(); }
        });
        clearBtn.addActionListener(e -> searchField.setText(""));

        bookBtn.addActionListener(e -> openBooking());
        reschedBtn.addActionListener(e -> doReschedule());
        cancelBtn.addActionListener(e -> doCancel());
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        loadDoctors("");
        refreshAppointments();
    }

    /** Live search — runs on every keystroke. */
    private void liveSearch() {
        loadDoctors(searchField.getText().trim());
    }

    private void loadDoctors(String keyword) {
        doctorModel.setRowCount(0);
        List<Doctor> doctors = (keyword == null || keyword.isEmpty())
                ? doctorDAO.findAll() : doctorDAO.search(keyword);
        for (Doctor d : doctors)
            doctorModel.addRow(new Object[]{d.getId(), d.getName(),
                    d.getSpecialization(), d.getPhone()});
    }

    /** Refresh table + live stats — called after every change. */
    public void refreshAppointments() {
        apptModel.setRowCount(0);
        if (patient == null) return;
        List<String[]> history = apptService.getHistory(patient.getId());
        for (String[] row : history) apptModel.addRow(row);

        statDoctors.setText(String.valueOf(doctorDAO.findAll().size()));
        statTotal.setText(String.valueOf(history.size()));
        statActive.setText(String.valueOf(
                history.stream().filter(r -> "BOOKED".equals(r[4])).count()));
    }

    private void openBooking() {
        if (patient == null) {
            JOptionPane.showMessageDialog(this, "Patient profile not found!");
            return;
        }
        int row = doctorTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a doctor from the table above!");
            return;
        }
        int doctorId = (int) doctorModel.getValueAt(row, 0);
        new BookingFrame(patient.getId(), doctorId, this).setVisible(true);
    }

    private void doCancel() {
        int row = apptTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an appointment from the table below!");
            return;
        }
        int apptId = Integer.parseInt((String) apptModel.getValueAt(row, 0));
        if (JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel?") == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, apptService.cancel(apptId));
            refreshAppointments();
        }
    }

    private void doReschedule() {
        int row = apptTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an appointment to reschedule!");
            return;
        }
        int apptId = Integer.parseInt((String) apptModel.getValueAt(row, 0));
        String msg = apptService.reschedule(apptId);
        JOptionPane.showMessageDialog(this, msg);
        if (msg.startsWith("✅")) {
            refreshAppointments();
            openBooking();
        }
    }
}