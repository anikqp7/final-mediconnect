package ui;

import core.UserService;
import entities.User;

import javax.swing.*;
import java.awt.*;

/** Login screen — branding panel + form card (FR2). */
public class LoginFrame extends JFrame {
    private final JTextField emailField = Theme.textField(22);
    private final JPasswordField passField = Theme.passwordField(22);
    private final UserService userService = new UserService();

    public LoginFrame() {
        setTitle("MediConnect — Login");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG);
        setLayout(new BorderLayout());

        // left: branding, right: login form
        add(Theme.brandingPanel(), BorderLayout.WEST);

        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Theme.BG);

        JPanel card = Theme.card("Login");
        card.setLayout(new BorderLayout(0, 18));

        JLabel title = new JLabel("Welcome back 👋");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Theme.TEXT);
        JLabel subtitle = new JLabel("Login to book your appointment");
        subtitle.setFont(Theme.BODY);
        subtitle.setForeground(Theme.MUTED);
        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        head.add(title);
        head.add(subtitle);
        card.add(head, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 1, 4, 10));
        form.setOpaque(false);
        form.add(Theme.fieldLabel("EMAIL"));
        form.add(emailField);
        form.add(Theme.fieldLabel("PASSWORD"));
        form.add(passField);
        card.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new BorderLayout(0, 8));
        actions.setOpaque(false);

        JButton loginBtn = Theme.successButton("Login");
        JPanel btnWrap = new JPanel(new BorderLayout());
        btnWrap.setOpaque(false);
        btnWrap.add(loginBtn, BorderLayout.CENTER);
        actions.add(btnWrap, BorderLayout.NORTH);

        JPanel createWrap = new JPanel(new FlowLayout(FlowLayout.CENTER));
        createWrap.setOpaque(false);
        JButton createBtn = Theme.linkButton("New patient? Create an account →");
        createWrap.add(createBtn);
        actions.add(createWrap, BorderLayout.SOUTH);

        card.add(actions, BorderLayout.SOUTH);

        right.add(card);
        add(right, BorderLayout.CENTER);

        loginBtn.addActionListener(e -> doLogin());
        createBtn.addActionListener(e -> new RegisterFrame().setVisible(true));
    }

    private void doLogin() {
        User user = userService.login(emailField.getText().trim(),
                new String(passField.getPassword()));
        if (user == null) {
            JOptionPane.showMessageDialog(this,
                    "❌ Wrong email or password!", "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        dispose();
        switch (user.getRole()) {
            case "PATIENT" -> new PatientDashboard(user).setVisible(true);
            case "DOCTOR"  -> new DoctorDashboard(user).setVisible(true);
            case "ADMIN"   -> new AdminDashboard(user).setVisible(true);
        }
    }
}