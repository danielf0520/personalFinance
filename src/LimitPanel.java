import javax.swing.*;
import java.awt.*;

public class LimitPanel extends JPanel {

    private TransactionManager transactionManager;

    private static final String[] CATEGORIES = {
            "Food", "Transport", "Entertainment", "Shopping",
            "Bills", "Health", "Education", "Other"
    };

    private java.util.HashMap<String, JTextField> limitFields;
    private JLabel messageLabel;

    public LimitPanel(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // ── Title ──
        JLabel title = new JLabel("Set Spending Limits Per Category");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        // ── Grid of category + input field ──
        JPanel gridPanel = new JPanel(new GridLayout(CATEGORIES.length, 2, 10, 10));
        limitFields = new java.util.HashMap<>();

        for (String category : CATEGORIES) {
            gridPanel.add(new JLabel(category + ":"));
            JTextField field = new JTextField();
            limitFields.put(category, field);
            gridPanel.add(field);
        }

        add(gridPanel, BorderLayout.CENTER);

        // ── Save button + message ──
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));

        JButton saveBtn = new JButton("Save Limits");
        messageLabel = new JLabel("");

        bottomPanel.add(saveBtn);
        bottomPanel.add(messageLabel);
        add(bottomPanel, BorderLayout.SOUTH);

        // Save button logic
        saveBtn.addActionListener(e -> {
            for (String category : CATEGORIES) {
                String text = limitFields.get(category).getText().trim();

                if (text.isEmpty()) continue; // skip empty fields

                try {
                    double limit = Double.parseDouble(text);
                    if (limit < 0) {
                        messageLabel.setForeground(Color.RED);
                        messageLabel.setText("Limit for " + category + " cannot be negative.");
                        return;
                    }
                    transactionManager.setLimit(category, limit);
                } catch (NumberFormatException ex) {
                    messageLabel.setForeground(Color.RED);
                    messageLabel.setText("Invalid amount for " + category + " — numbers only.");
                    return;
                }
            }
            messageLabel.setForeground(new Color(0, 128, 0));
            messageLabel.setText("Limits saved!");
        });

        refresh();
    }

    public void refresh() {
        for (String category : CATEGORIES) {
            double limit = transactionManager.getLimit(category);
            if (limit > 0) {
                limitFields.get(category).setText(String.valueOf(limit));
            }
        }
    }
}