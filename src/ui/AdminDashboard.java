package ui;

import core.DoctorService;
import core.ReportService;
import dao.AppointmentDAO;
import dao.PatientDAO;
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

/** Admin panel — live stats + 4 management tabs (FR12-FR15). */
public class AdminDashboard extends JFrame {
    private final DoctorService doctorService = new DoctorService();
    private final ReportService reportService = new ReportService();
    private final PatientDAO patientDAO = new PatientDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    // ── live stat values ──
    private final JLabel statDoctors      = Theme.statValue();
    private final JLabel statPatients     = Theme.statValue();
    private final JLabel statAppointments = Theme.statValue();
    private final JLabel statToday        = Theme.statValue();

    private final JTextField dName = Theme.textField(14);
    private final JTextField dSpec = Theme.textField(14);
    private final JTextField dPhone = Theme.textField(14);
    private final JTextField dEmail = Theme.textField(14);
    private final JPasswordField dPass = Theme.passwordField(14);

    private final DefaultTableModel doctorModel = new DefaultTableModel(
            new Object[]{"ID", "Name", "Specialization", "Phone"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final DefaultTableModel patientModel = new DefaultTableModel(
            new Object[]{"ID", "Name", "Age", "Gender", "Phone", "Email"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final DefaultTableModel apptModel = new DefaultTableModel(
            new Object[]{"ID", "Patient", "Doctor", "Date", "Time", "Status"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final DefaultTableModel reportModel = new DefaultTableModel();

    private final JTable doctorTable = new JTable(doctorModel);
    private final JTable patientTable = new JTable(patientModel);
    private final JTable apptTable = new JTable(apptModel);
    private final JTable reportTable = new JTable(reportModel);

    public AdminDashboard(User user) {
        setTitle("MediConnect — Admin Dashboard");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG);
        setLayout(new BorderLayout());

        String today = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH));
        add(Theme.headerBar("🛡  Admin Panel",
                user.getEmail() + "   •   " + today), BorderLayout.NORTH);

        // ── DYNAMIC: live stats row ──
        JPanel stats = new JPanel(new GridLayout(1, 4, 10, 10));
        stats.setOpaque(false);
        stats.add(Theme.statCard("DOCTORS", statDoctors, Theme.PRIMARY));
        stats.add(Theme.statCard("PATIENTS", statPatients, Theme.PRIMARY_SOFT));
        stats.add(Theme.statCard("APPOINTMENTS", statAppointments, Theme.SUCCESS));
        stats.add(Theme.statCard("TODAY'S BOOKINGS", statToday, Theme.DANGER));

        JPanel statsWrap = new JPanel(new BorderLayout());
        statsWrap.setOpaque(false);
        statsWrap.setBorder(new EmptyBorder(10, 12, 0, 12));
        statsWrap.add(stats, BorderLayout.CENTER);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Theme.BTN);
        tabs.addTab("👨‍⚕️ Doctors", doctorsTab());
        tabs.addTab("👥 Patients", patientsTab());
        tabs.addTab("📋 Appointments", appointmentsTab());
        tabs.addTab("📊 Reports", reportsTab());

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Theme.BG);
        center.add(statsWrap, BorderLayout.NORTH);
        center.add(tabs, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        south.setBackground(Theme.CARD);
        JButton logout = Theme.dangerButton("Logout");
        south.add(logout);
        add(south, BorderLayout.SOUTH);
        logout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        loadDoctors();
        loadPatients();
        loadAppointments();
    }

    private JPanel doctorsTab() {
        JPanel tab = new JPanel(new BorderLayout(10, 10));
        tab.setBackground(Theme.BG);
        tab.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel form = Theme.card("Add new doctor (creates login account)");
        form.setLayout(new GridLayout(6, 2, 10, 10));
        form.add(Theme.fieldLabel("NAME"));
        form.add(dName);
        form.add(Theme.fieldLabel("SPECIALIZATION"));
        form.add(dSpec);
        form.add(Theme.fieldLabel("PHONE"));
        form.add(dPhone);
        form.add(Theme.fieldLabel("LOGIN EMAIL"));
        form.add(dEmail);
        form.add(Theme.fieldLabel("PASSWORD"));
        form.add(dPass);
        form.add(new JLabel());

        JButton addBtn = Theme.successButton("➕  Add Doctor");
        form.add(addBtn);

        JPanel tableCard = Theme.card("Doctors");
        tableCard.setLayout(new BorderLayout());
        Theme.styleTable(doctorTable);
        tableCard.add(new JScrollPane(doctorTable), BorderLayout.CENTER);
        JButton delBtn = Theme.dangerButton("🗑  Delete Selected");
        tableCard.add(delBtn, BorderLayout.SOUTH);

        tab.add(form, BorderLayout.WEST);
        tab.add(tableCard, BorderLayout.CENTER);

        addBtn.addActionListener(e -> {
            String msg = doctorService.addDoctorWithAccount(
                    dName.getText().trim(), dSpec.getText().trim(),
                    dPhone.getText().trim(), dEmail.getText().trim(),
                    new String(dPass.getPassword()));
            JOptionPane.showMessageDialog(this, msg);
            loadDoctors();
        });
        delBtn.addActionListener(e -> {
            int row = doctorTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a doctor!");
                return;
            }
            int id = (int) doctorModel.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete?") == JOptionPane.YES_OPTION) {
                doctorService.deleteDoctor(id);
                loadDoctors();
            }
        });
        return tab;
    }

    private JPanel patientsTab() {
        JPanel tab = new JPanel(new BorderLayout(10, 10));
        tab.setBackground(Theme.BG);
        tab.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel card = Theme.card("All Patients (FR13)");
        card.setLayout(new BorderLayout());
        Theme.styleTable(patientTable);
        card.add(new JScrollPane(patientTable), BorderLayout.CENTER);
        JButton delBtn = Theme.dangerButton("🗑  Delete Selected Patient");
        card.add(delBtn, BorderLayout.SOUTH);

        delBtn.addActionListener(e -> {
            int row = patientTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a patient!");
                return;
            }
            int id = Integer.parseInt((String) patientModel.getValueAt(row, 0));
            if (JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete?") == JOptionPane.YES_OPTION) {
                patientDAO.delete(id);
                loadPatients();
            }
        });
        tab.add(card, BorderLayout.CENTER);
        return tab;
    }

    private JPanel appointmentsTab() {
        JPanel tab = new JPanel(new BorderLayout(10, 10));
        tab.setBackground(Theme.BG);
        tab.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel card = Theme.card("All Appointments (FR14)");
        card.setLayout(new BorderLayout());
        Theme.styleTable(apptTable);
        card.add(new JScrollPane(apptTable), BorderLayout.CENTER);
        JButton refreshBtn = Theme.primaryButton("↻  Refresh");
        card.add(refreshBtn, BorderLayout.SOUTH);
        refreshBtn.addActionListener(e -> loadAppointments());

        tab.add(card, BorderLayout.CENTER);
        return tab;
    }

    private JPanel reportsTab() {
        JPanel tab = new JPanel(new BorderLayout(10, 10));
        tab.setBackground(Theme.BG);
        tab.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel card = Theme.card("Appointment Reports (FR15)");
        card.setLayout(new BorderLayout());
        Theme.styleTable(reportTable);
        card.add(new JScrollPane(reportTable), BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        btns.setOpaque(false);
        JButton dailyBtn = Theme.primaryButton("📅  Daily Report");
        JButton weeklyBtn = Theme.primaryButton("📅  Weekly Report");
        btns.add(dailyBtn);
        btns.add(weeklyBtn);
        card.add(btns, BorderLayout.SOUTH);

        dailyBtn.addActionListener(e -> loadReport(true));
        weeklyBtn.addActionListener(e -> loadReport(false));

        tab.add(card, BorderLayout.CENTER);
        return tab;
    }

    // ── data loaders (each also refreshes live stats) ──

    private void loadDoctors() {
        doctorModel.setRowCount(0);
        for (Doctor d : doctorService.getAllDoctors())
            doctorModel.addRow(new Object[]{d.getId(), d.getName(),
                    d.getSpecialization(), d.getPhone()});
        refreshStats();
    }

    private void loadPatients() {
        patientModel.setRowCount(0);
        for (String[] row : patientDAO.findAll()) patientModel.addRow(row);
        refreshStats();
    }

    private void loadAppointments() {
        apptModel.setRowCount(0);
        for (String[] row : appointmentDAO.findAll()) apptModel.addRow(row);
        refreshStats();
    }

    /** DYNAMIC: live stat numbers — recalculated after every change. */
    private void refreshStats() {
        String today = LocalDate.now().toString();
        statDoctors.setText(String.valueOf(doctorService.getAllDoctors().size()));
        statPatients.setText(String.valueOf(patientDAO.findAll().size()));
        statAppointments.setText(String.valueOf(appointmentDAO.findAll().size()));

        String count = reportService.dailyReport().stream()
                .filter(r -> r[0].equals(today))
                .map(r -> r[1]).findFirst().orElse("0");
        statToday.setText(count);
    }

    private void loadReport(boolean daily) {
        List<String[]> rows = daily ? reportService.dailyReport()
                                    : reportService.weeklyReport();
        reportModel.setRowCount(0);
        reportModel.setColumnIdentifiers(daily
                ? new Object[]{"Date", "Total Appointments"}
                : new Object[]{"Week", "Total Appointments"});
        for (String[] row : rows) reportModel.addRow(row);
    }
}