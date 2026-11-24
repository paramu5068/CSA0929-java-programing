import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class LibrarySearchScreen extends JFrame {
    private final JTextField query = new JTextField(22);
    private final JComboBox<String> category =
            new JComboBox<>(new String[]{"All", "Title", "Author", "ISBN"});
    private final JLabel status = new JLabel("Ready.");

    public LibrarySearchScreen() {
        super("Campus Library Search");
        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.add(new JLabel("CAMPUS LIBRARY"));
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton search = new JButton("Search");
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(query);
        searchPanel.add(category);
        searchPanel.add(search);
        top.add(titlePanel);
        top.add(searchPanel);

        String[] columns = {"Book ID", "Title", "Author", "Category", "Availability"};
        Object[][] rows = {
            {"B101", "Java Programming", "Herbert Schildt", "Computer Science", "Available"},
            {"B102", "Clean Code", "Robert C. Martin", "Computer Science", "Issued"},
            {"B103", "Database System Concepts", "Silberschatz", "Database", "Available"}
        };
        JTable table = new JTable(new DefaultTableModel(rows, columns) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        });
        JScrollPane results = new JScrollPane(table);

        JPanel navigation = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton prev = new JButton("Prev");
        JButton next = new JButton("Next");
        JButton reserve = new JButton("Reserve");
        navigation.add(prev);
        navigation.add(next);
        navigation.add(reserve);

        JPanel footer = new JPanel(new BorderLayout());
        footer.add(navigation, BorderLayout.CENTER);
        footer.add(status, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(results, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        search.addActionListener(e -> status.setText(
                "Search requested: " + query.getText() + " (" + category.getSelectedItem() + ")"));
        prev.addActionListener(e -> status.setText("Previous page selected."));
        next.addActionListener(e -> status.setText("Next page selected."));
        reserve.addActionListener(e -> status.setText("Select an available book to reserve."));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 420);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibrarySearchScreen().setVisible(true));
    }
}
