import javax.swing.*;
import java.awt.*;

public class AddPanel extends JPanel {

    private TransactionManager transactionManager;

    private JComboBox<String> typeBox;
    private JComboBox<String> categoryBox;
    private JTextField amountField;
    private JTextField dateField;
    private JTextField noteField;
    private JLabel messageLabel;

    public AddPanel(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;

        setLayout(new GridLayout(7, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Type
        add(new JLabel("Type:"));
        typeBox = new JComboBox<>(new String[]{"Expense", "Income"});
        add(typeBox);

        // Category
        add(new JLabel("Category:"));
        categoryBox = new JComboBox<>(new String[]{
                "Food", "Transport", "Entertainment", "Shopping",
                "Bills", "Health", "Education", "Other", "Income"
        });
        add(categoryBox);

        // Amount
        add(new JLabel("Amount ($):"));
        amountField = new JTextField();
        add(amountField);

        // Date
        add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField(java.time.LocalDate.now().toString());
        add(dateField);

        // Note
        add(new JLabel("Note:"));
        noteField = new JTextField();
        add(noteField);

        // Submit button
        JButton submitBtn = new JButton("Add Transaction");
        add(submitBtn);

        // Message label
        messageLabel = new JLabel("");
        add(messageLabel);

        // Button logic
        submitBtn.addActionListener(e -> {
            String type       = (String) typeBox.getSelectedItem();
            String category   = (String) categoryBox.getSelectedItem();
            String date       = dateField.getText().trim();
            String note       = noteField.getText().trim();
            String amountText = amountField.getText().trim();

            // Validation
            if (amountText.isEmpty() || date.isEmpty()) {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Please fill in amount and date.");
                return;
            }

            try {
                double amount = Double.parseDouble(amountText);
                if (amount <= 0) {
                    messageLabel.setForeground(Color.RED);
                    messageLabel.setText("Amount must be greater than zero.");
                    return;
                }

                transactionManager.addTransaction(
                        new Transaction(type, category, amount, date, note)
                );

                messageLabel.setForeground(new Color(0, 128, 0));
                messageLabel.setText("Transaction added!");

                // Clear fields
                amountField.setText("");
                noteField.setText("");
                dateField.setText(java.time.LocalDate.now().toString());

            } catch (NumberFormatException ex) {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Invalid amount — numbers only.");
            }
        });
    }
}