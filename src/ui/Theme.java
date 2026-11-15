package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Central UI theme — modern flat "medical blue" design.
 * Every screen pulls colors, fonts and styled components from this
 * single class, so the whole application stays visually consistent.
 */
public class Theme {

    // ── Color palette ──
    public static final Color PRIMARY      = new Color(23, 78, 166);
    public static final Color PRIMARY_DARK = new Color(13, 46, 99);
    public static final Color PRIMARY_SOFT = new Color(72, 126, 214);
    public static final Color BG           = new Color(243, 247, 251);
    public static final Color CARD         = Color.WHITE;
    public static final Color TEXT         = new Color(25, 35, 45);
    public static final Color MUTED        = new Color(108, 122, 137);
    public static final Color SUCCESS      = new Color(21, 158, 74);
    public static final Color DANGER       = new Color(219, 38, 72);
    public static final Color BORDER       = new Color(221, 229, 238);
    public static final Color ROW_ODD      = new Color(239, 245, 251);
    public static final Color ROW_SEL      = new Color(197, 221, 247);

    // ── Fonts ──
    public static final Font H1    = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font BODY  = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font SMALL = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font BTN   = new Font("Segoe UI", Font.BOLD, 14);

    private Theme() {}

    // ═══════════════ BUTTONS ═══════════════

    public static JButton primaryButton(String text) { return makeButton(text, PRIMARY, PRIMARY_DARK); }
    public static JButton successButton(String text) { return makeButton(text, SUCCESS, new Color(15, 120, 55)); }
    public static JButton dangerButton(String text)  { return makeButton(text, DANGER, new Color(180, 27, 58)); }
    public static JButton mutedButton(String text)   { return makeButton(text, new Color(90, 104, 120), new Color(70, 82, 95)); }

    /** Flat button with a hover effect (color darkens on mouse-over). */
    private static JButton makeButton(String text, Color normal, Color hover) {
        JButton b = new JButton(text);
        b.setFont(BTN);
        b.setBackground(normal);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBorder(new EmptyBorder(11, 24, 11, 24));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(hover); }
            @Override public void mouseExited(MouseEvent e)  { b.setBackground(normal); }
        });
        return b;
    }

    /** Link-style button for secondary actions. */
    public static JButton linkButton(String text) {
        JButton b = new JButton(text);
        b.setFont(BODY);
        b.setForeground(PRIMARY);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(6, 4, 6, 4));
        return b;
    }

    // ═══════════════ INPUT FIELDS ═══════════════

    /** Styled text field with soft border. */
    public static JTextField textField(int columns) {
        JTextField f = new JTextField(columns);
        f.setFont(BODY);
        f.setBackground(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(9, 10, 9, 10)));
        return f;
    }

    /** Styled password field. */
    public static JPasswordField passwordField(int columns) {
        JPasswordField f = new JPasswordField(columns);
        f.setFont(BODY);
        f.setBackground(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(9, 10, 9, 10)));
        return f;
    }

    /** Small uppercase label for form fields. */
    public static JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(MUTED);
        return l;
    }

    // ═══════════════ PANELS ═══════════════

    /** White card panel with a titled border. */
    public static JPanel card(String title) {
        JPanel p = new JPanel();
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(BORDER, 1),
                        "  " + title + "  ",
                        TitledBorder.DEFAULT_JUSTIFICATION,
                        TitledBorder.DEFAULT_POSITION,
                        new Font("Segoe UI", Font.BOLD, 13),
                        PRIMARY),
                new EmptyBorder(12, 14, 12, 14)));
        return p;
    }

    /** Blue header bar with a light accent line underneath. */
    public static JPanel headerBar(String title, String subtitle) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(PRIMARY);
        bar.setPreferredSize(new Dimension(0, 64));
        bar.setBorder(new EmptyBorder(10, 22, 10, 22));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 20));
        t.setForeground(Color.WHITE);

        JLabel s = new JLabel(subtitle, SwingConstants.RIGHT);
        s.setFont(SMALL);
        s.setForeground(new Color(190, 214, 245));

        bar.add(t, BorderLayout.WEST);
        bar.add(s, BorderLayout.EAST);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(PRIMARY);
        outer.add(bar, BorderLayout.CENTER);

        JPanel line = new JPanel();
        line.setBackground(PRIMARY_SOFT);
        line.setPreferredSize(new Dimension(0, 3));
        outer.add(line, BorderLayout.SOUTH);
        return outer;
    }

    /** Left-side branding panel for login / registration screens. */
    public static JPanel brandingPanel() {
        JPanel p = new JPanel();
        p.setBackground(PRIMARY_DARK);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(70, 45, 60, 45));
        p.setPreferredSize(new Dimension(360, 0));

        JLabel logo = new JLabel("🏥");
        logo.setFont(new Font("Segoe UI", Font.PLAIN, 54));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel name = new JLabel("MediConnect");
        name.setFont(new Font("Segoe UI", Font.BOLD, 34));
        name.setForeground(Color.WHITE);
        name.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tag = new JLabel("Doctor Appointment System");
        tag.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tag.setForeground(new Color(168, 198, 240));
        tag.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(logo);
        p.add(Box.createVerticalStrut(12));
        p.add(name);
        p.add(Box.createVerticalStrut(4));
        p.add(tag);
        p.add(Box.createVerticalStrut(48));

        String[] features = {
                "✓  Search doctors by specialization",
                "✓  Book, cancel or reschedule visits",
                "✓  Doctors manage their own schedules",
                "✓  Admin panel with live reports"
        };
        for (String f : features) {
            JLabel l = new JLabel(f);
            l.setFont(SMALL);
            l.setForeground(new Color(198, 219, 246));
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            l.setBorder(new EmptyBorder(7, 0, 7, 0));
            p.add(l);
        }
        return p;
    }

    // ═══════════════ TABLES ═══════════════

    /** Zebra-striped table with colored status text + styled header. */
    public static void styleTable(JTable t) {
        t.setFont(BODY);
        t.setRowHeight(30);
        t.setShowVerticalLines(false);
        t.setShowHorizontalLines(false);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setSelectionBackground(ROW_SEL);
        t.setSelectionForeground(TEXT);
        t.setGridColor(BORDER);

        // zebra stripes + status colors
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean selected, boolean focus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, selected, focus, row, column);
                if (!selected) {
                    c.setBackground(row % 2 == 0 ? CARD : ROW_ODD);
                    c.setForeground(TEXT);
                }
                if (!selected && value != null) {
                    String s = value.toString();
                    if (s.equals("BOOKED"))          c.setForeground(PRIMARY);
                    else if (s.equals("COMPLETED")
                            || s.equals("FREE"))     c.setForeground(SUCCESS);
                    else if (s.equals("CANCELLED"))  c.setForeground(DANGER);
                }
                if (c instanceof JComponent) {
                    ((JComponent) c).setBorder(new EmptyBorder(0, 8, 0, 8));
                }
                return c;
            }
        });

        JTableHeader h = t.getTableHeader();
        h.setBackground(PRIMARY);
        h.setForeground(Color.WHITE);
        h.setFont(new Font("Segoe UI", Font.BOLD, 13));
        h.setPreferredSize(new Dimension(0, 36));
        h.setReorderingAllowed(false);
        try {
            DefaultTableCellRenderer hr = (DefaultTableCellRenderer) h.getDefaultRenderer();
            hr.setBackground(PRIMARY);
            hr.setForeground(Color.WHITE);
            hr.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (ClassCastException ignored) { }
    }

    // ═══════════════ LABELS ═══════════════

    public static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(BODY);
        l.setForeground(TEXT);
        return l;
    }

        /** Big stat number label (used inside stat cards). */
    public static JLabel statValue() {
        JLabel v = new JLabel("0");
        v.setFont(new Font("Segoe UI", Font.BOLD, 24));
        return v;
    }

    /** KPI stat card: colored stripe + small title + big number. */
    public static JPanel statCard(String title, JLabel value, Color accent) {
        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.setForeground(MUTED);
        value.setForeground(accent);

        JPanel card = new JPanel(new BorderLayout(10, 2));
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(10, 14, 10, 16)));

        JPanel stripe = new JPanel();
        stripe.setBackground(accent);
        stripe.setPreferredSize(new Dimension(4, 0));

        card.add(stripe, BorderLayout.WEST);
        card.add(t, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        return card;
    }
}