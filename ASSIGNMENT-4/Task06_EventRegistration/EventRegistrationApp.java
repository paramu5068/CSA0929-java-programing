import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class EventRegistrationApp extends JFrame {
    private final JTextField studentId = new JTextField(12);
    private final JTextField studentName = new JTextField(12);
    private final JTextField email = new JTextField(16);
    private final JComboBox<EventItem> eventChoice = new JComboBox<>(new EventItem[]{
        new EventItem(1, "Cultural Fest"), new EventItem(2, "Tech Symposium"),
        new EventItem(3, "Sports Day")
    });
    private final JComboBox<EventItem> filterChoice = new JComboBox<>();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Student ID", "Name", "Email", "Event", "Registered At"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JLabel status = new JLabel("Ready.");
    private final JButton registerButton = new JButton("Register");
    private final RegistrationService service = new RegistrationService();
    private final int pageSize = 50;
    private int page = 0;
    private SwingWorker<?, ?> activeWorker;

    public EventRegistrationApp() {
        super("Campus Event Registration");
        setLayout(new BorderLayout(8, 8));
        setJMenuBar(buildMenu());

        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Student Registration"));
        form.add(new JLabel("Student ID")); form.add(studentId);
        form.add(new JLabel("Student name")); form.add(studentName);
        form.add(new JLabel("Email")); form.add(email);
        form.add(new JLabel("Event")); form.add(eventChoice);
        form.add(new JLabel("")); form.add(registerButton);

        filterChoice.addItem(new EventItem(0, "All events"));
        filterChoice.addItem(new EventItem(1, "Cultural Fest"));
        filterChoice.addItem(new EventItem(2, "Tech Symposium"));
        filterChoice.addItem(new EventItem(3, "Sports Day"));
        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filter.add(new JLabel("Filter event:"));
        filter.add(filterChoice);
        JButton refresh = new JButton("Refresh");
        JButton previous = new JButton("Previous");
        JButton next = new JButton("Next");
        filter.add(refresh); filter.add(previous); filter.add(next);

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.NORTH);
        top.add(filter, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        registerButton.addActionListener(e -> registerAsync());
        refresh.addActionListener(e -> { page = 0; loadPage(); });
        filterChoice.addActionListener(e -> { page = 0; loadPage(); });
        previous.addActionListener(e -> { if (page > 0) { page--; loadPage(); } });
        next.addActionListener(e -> { page++; loadPage(); });

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                if (activeWorker != null && !activeWorker.isDone()) {
                    int choice = JOptionPane.showConfirmDialog(EventRegistrationApp.this,
                            "A database operation is in progress. Close anyway?",
                            "Confirm close", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) dispose();
                } else dispose();
            }
        });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        loadPage();
    }

    private JMenuBar buildMenu() {
        JMenuBar bar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem refresh = new JMenuItem("Refresh");
        JMenuItem export = new JMenuItem("Export CSV");
        JMenuItem exit = new JMenuItem("Exit");
        refresh.addActionListener(e -> { page = 0; loadPage(); });
        export.addActionListener(e -> exportCsv());
        exit.addActionListener(e -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        file.add(refresh); file.add(export); file.addSeparator(); file.add(exit);
        bar.add(file);
        return bar;
    }

    private void registerAsync() {
        EventItem event = (EventItem) eventChoice.getSelectedItem();
        if (event == null) return;
        registerButton.setEnabled(false);
        status.setText("Saving registration...");
        activeWorker = new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                service.register(studentId.getText(), studentName.getText(), email.getText(), event.id);
                return null;
            }
            @Override protected void done() {
                registerButton.setEnabled(true);
                try {
                    get();
                    JOptionPane.showMessageDialog(EventRegistrationApp.this,
                            "Registration successful.");
                    studentId.setText(""); studentName.setText(""); email.setText("");
                    status.setText("Registration saved.");
                    page = 0; loadPage();
                } catch (Exception ex) {
                    showError(ex);
                }
            }
        };
        activeWorker.execute();
    }

    private void loadPage() {
        EventItem selected = (EventItem) filterChoice.getSelectedItem();
        Integer eventId = selected == null || selected.id == 0 ? null : selected.id;
        final Integer filterId = eventId;
        status.setText("Loading page " + (page + 1) + "...");
        activeWorker = new SwingWorker<List<Registration>, Void>() {
            @Override protected List<Registration> doInBackground() throws Exception {
                return service.findPage(filterId, pageSize, page * pageSize);
            }
            @Override protected void done() {
                try {
                    List<Registration> rows = get();
                    model.setRowCount(0);
                    for (Registration r : rows) {
                        model.addRow(new Object[]{r.id, r.studentId, r.studentName,
                                r.email, r.eventName, r.registeredAt});
                    }
                    status.setText("Loaded " + rows.size() + " registration(s), page " + (page + 1));
                } catch (Exception ex) { showError(ex); }
            }
        };
        activeWorker.execute();
    }

    private void exportCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("registrations.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        Path path = chooser.getSelectedFile().toPath();
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            for (int c = 0; c < model.getColumnCount(); c++) {
                if (c > 0) writer.write(",");
                writer.write(csv(model.getColumnName(c)));
            }
            writer.newLine();
            for (int r = 0; r < model.getRowCount(); r++) {
                for (int c = 0; c < model.getColumnCount(); c++) {
                    if (c > 0) writer.write(",");
                    writer.write(csv(String.valueOf(model.getValueAt(r, c))));
                }
                writer.newLine();
            }
            status.setText("Exported current page to " + path);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Export error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String csv(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private void showError(Exception ex) {
        Throwable cause = ex.getCause() == null ? ex : ex.getCause();
        String message = cause.getMessage() == null ? cause.toString() : cause.getMessage();
        if (message.toLowerCase().contains("duplicate") || message.toLowerCase().contains("unique")) {
            message = "This student is already registered for this event.";
        }
        status.setText("Error: " + message);
        JOptionPane.showMessageDialog(this, message, "Operation failed", JOptionPane.ERROR_MESSAGE);
    }

    private static class EventItem {
        final int id; final String name;
        EventItem(int id, String name) { this.id = id; this.name = name; }
        @Override public String toString() { return name; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EventRegistrationApp().setVisible(true));
    }
}
