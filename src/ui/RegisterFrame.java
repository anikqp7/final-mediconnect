package ui;

import core.UserService;

import javax.swing.*;
import java.awt.*;

/** Patient registration — branding panel + form card (FR1, FR17). */
public class RegisterFrame extends JFrame {
    private final JTextField nameField = Theme.textField(20);
    private final JTextField ageField = Theme.textField(20);
    private final JComboBox<String> genderBox =
            new JComboBox<>(new String[]{"Male", "Female", "Other"});
    private final JTextField phoneField = Theme.textField(20);
    private final JTextField emailField = Theme.textField(20);
    private final JPasswordField passField = Theme.passwordField(20);
    private final UserService userService = new UserService();

    public RegisterFrame() {
        setTitle("MediConnect — New Patient Registration");
        setSize(1000, 650);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG);
        setLayout(new BorderLayout());

        add(Theme.brandingPanel(), BorderLayout.WEST);

        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Theme.BG);

        JPanel card = Theme.card("Create your account");
        card.setLayout(new BorderLayout(0, 16));

        JLabel title = new JLabel("Patient registration 📝");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Theme.TEXT);
        JLabel subtitle = new JLabel("Fill in your details to create a patient account");
        subtitle.setFont(Theme.BODY);
        subtitle.setForeground(Theme.MUTED);
        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        head.add(title);
        head.add(subtitle);
        card.add(head, BorderLayout.NORTH);

        genderBox.setFont(Theme.BODY);
        genderBox.setBackground(Color.WHITE);

        JPanel form = new JPanel(new GridLayout(7, 2, 12, 12));
        form.setOpaque(false);
        form.add(Theme.fieldLabel("FULL NAME"));
        form.add(nameField);
        form.add(Theme.fieldLabel("AGE"));
        form.add(ageField);
        form.add(Theme.fieldLabel("GENDER"));
        form.add(genderBox);
        form.add(Theme.fieldLabel("PHONE (11 DIGITS)"));
        form.add(phoneField);
        form.add(Theme.fieldLabel("EMAIL"));
        form.add(emailField);
        form.add(Theme.fieldLabel("PASSWORD"));
        form.add(passField);
        form.add(new JLabel());

        JButton registerBtn = Theme.successButton("Create Account");
        form.add(registerBtn);
        card.add(form, BorderLayout.CENTER);

        right.add(card);
        add(right, BorderLayout.CENTER);

        registerBtn.addActionListener(e -> doRegister());
    }

    private void doRegister() {
        int age;
        try {
            age = Integer.parseInt(ageField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "❌ Please enter a valid number for age!");
            return;
        }
        String msg = userService.registerPatient(
                nameField.getText().trim(), age,
                (String) genderBox.getSelectedItem(),
                phoneField.getText().trim(),
                emailField.getText().trim(),
                new String(passField.getPassword()));

        JOptionPane.showMessageDialog(this, msg);
        if (msg.startsWith("✅")) dispose();
    }
}