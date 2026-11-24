import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class FeeWindow extends JFrame {
    private final JTextField regNo = new JTextField(14);
    private final JTextField amount = new JTextField(10);
    private final JLabel status = new JLabel("Enter fee details.");
    private final FeeService service = new FeeService();

    public FeeWindow() {
        super("Student Fee Payment");
        setLayout(new BorderLayout(8, 8));
        JPanel form = new JPanel(new GridLayout(2, 2, 6, 6));
        form.add(new JLabel("Registration number:"));
        form.add(regNo);
        form.add(new JLabel("Amount:"));
        form.add(amount);

        JPanel buttons = new JPanel(new FlowLayout());
        JButton pay = new JButton("Pay");
        JButton clear = new JButton("Clear");
        JButton receipt = new JButton("Receipt");
        buttons.add(pay);
        buttons.add(clear);
        buttons.add(receipt);

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.NORTH);
        add(status, BorderLayout.SOUTH);

        pay.addActionListener(e -> doPayment());
        clear.addActionListener(e -> {
            regNo.setText("");
            amount.setText("");
            status.setText("Fields cleared.");
        });
        receipt.addActionListener(e -> {
            try {
                status.setText(service.receipt(regNo.getText()));
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Receipt", JOptionPane.WARNING_MESSAGE);
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { dispose(); }
        });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private void doPayment() {
        final String id = regNo.getText();
        final String value = amount.getText();
        setButtonsEnabled(false);
        status.setText("Processing...");
        new SwingWorker<String, Void>() {
            @Override protected String doInBackground() {
                return service.pay(id, value);
            }
            @Override protected void done() {
                setButtonsEnabled(true);
                try {
                    String message = get();
                    status.setText(message);
                    JOptionPane.showMessageDialog(FeeWindow.this, message, "Payment", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    String message = ex.getCause() == null ? ex.getMessage() : ex.getCause().getMessage();
                    status.setText(message);
                    JOptionPane.showMessageDialog(FeeWindow.this, message, "Payment error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void setButtonsEnabled(boolean enabled) {
        for (Component c : ((JPanel)getContentPane().getComponent(1)).getComponents()) {
            c.setEnabled(enabled);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FeeWindow().setVisible(true));
    }
}
