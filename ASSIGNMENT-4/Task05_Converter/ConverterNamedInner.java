import java.awt.FlowLayout;
import java.awt.event.*;
import javax.swing.*;

public class ConverterNamedInner extends JFrame {
    private final JTextField amount = new JTextField(10);
    private final JComboBox<String> target = new JComboBox<>(new String[]{"USD", "EUR"});
    private final JLabel result = new JLabel(" ");

    public ConverterNamedInner() {
        super("Converter – Named Inner Class");
        setLayout(new FlowLayout());
        JButton button = new JButton("Convert");
        add(new JLabel("INR")); add(amount); add(target); add(button); add(result);
        ConvertHandler handler = new ConvertHandler();
        button.addActionListener(handler);
        amount.addActionListener(handler);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { dispose(); }
        });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        pack();
    }

    private class ConvertHandler implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) { convert(); }
    }

    private void convert() {
        try {
            double value = Double.parseDouble(amount.getText().trim());
            String cur = (String) target.getSelectedItem();
            String out = String.format("%.2f %s", value * (cur.equals("USD") ? .012 : .011), cur);
            result.setText(out);
            JOptionPane.showMessageDialog(this, out);
        } catch (NumberFormatException ex) {
            result.setText("Invalid amount");
            JOptionPane.showMessageDialog(this, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public static void main(String[] a) { SwingUtilities.invokeLater(() -> new ConverterNamedInner().setVisible(true)); }
}
