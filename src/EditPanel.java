import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;

public class EditPanel extends JPanel {

    private TransactionManager transactionManager;
    private DefaultTableModel tableModel;
    private JTable table;

    private JComboBox<String> typeBox;
    private JComboBox<String> categoryBox;
    private JTextField amountField;
    private JTextField dateField;
    private JTextField noteField;
    private JLabel messageLabel;

    public EditPanel(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ── Table at the top ──
        String[] columns = {"#", "Type", "Category", "Amount", "Date", "Note"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(0, 200));
        add(scrollPane, BorderLayout.NORTH);

        // ── Edit form in the middle ──
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder("Edit Selected Transaction"));

        formPanel.add(new JLabel("Type:"));
        typeBox = new JComboBox<>(new String[]{"Expense", "Income"});
        formPanel.add(typeBox);

        formPanel.add(new JLabel("Category:"));
        categoryBox = new JComboBox<>(new String[]{
                "Food", "Transport", "Entertainment", "Shopping",
                "Bills", "Health", "Education", "Other", "Income"
        });
        formPanel.add(categoryBox);

        formPanel.add(new JLabel("Amount ($):"));
        amountField = new JTextField();
        formPanel.add(amountField);

        formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField();
        formPanel.add(dateField);

        formPanel.add(new JLabel("Note:"));
        noteField = new JTextField();
        formPanel.add(noteField);

        add(formPanel, BorderLayout.CENTER);

        // ── Buttons at the bottom ──
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));

        JButton loadBtn   = new JButton("Load Selected");
        JButton saveBtn   = new JButton("Save Changes");
        JButton deleteBtn = new JButton("Delete Selected");

        messageLabel = new JLabel("");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        buttonPanel.add(loadBtn);
        buttonPanel.add(saveBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(messageLabel);

        add(buttonPanel, BorderLayout.SOUTH);

        // ── Button logic ──

        // Load selected row into form
        loadBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Please select a row first.");
                return;
            }

            ArrayList<Transaction> transactions = transactionManager.getTransactions();
            Transaction t = transactions.get(row);

            typeBox.setSelectedItem(t.getType());
            categoryBox.setSelectedItem(t.getCategory());
            amountField.setText(String.valueOf(t.getAmount()));
            dateField.setText(t.getDate());
            noteField.setText(t.getNote());

            messageLabel.setForeground(Color.BLACK);
            messageLabel.setText("Row " + (row + 1) + " loaded. Make changes and click Save.");
        });

        // Save changes to selected row
        saveBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Please select and load a row first.");
                return;
            }

            String amountText = amountField.getText().trim();
            String date       = dateField.getText().trim();

            if (amountText.isEmpty() || date.isEmpty()) {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Amount and date cannot be empty.");
                return;
            }

            try {
                double amount = Double.parseDouble(amountText);
                if (amount <= 0) {
                    messageLabel.setForeground(Color.RED);
                    messageLabel.setText("Amount must be greater than zero.");
                    return;
                }

                Transaction updated = new Transaction(
                        (String) typeBox.getSelectedItem(),
                        (String) categoryBox.getSelectedItem(),
                        amount,
                        date,
                        noteField.getText().trim()
                );

                transactionManager.updateTransaction(row, updated);
                refresh();

                messageLabel.setForeground(new Color(0, 128, 0));
                messageLabel.setText("Transaction updated!");

            } catch (NumberFormatException ex) {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Invalid amount — numbers only.");
            }
        });

        // Delete selected row
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Please select a row to delete.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this transaction?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                transactionManager.removeTransaction(row);
                refresh();

                // Clear form
                amountField.setText("");
                dateField.setText("");
                noteField.setText("");

                messageLabel.setForeground(new Color(0, 128, 0));
                messageLabel.setText("Transaction deleted.");
            }
        });

        refresh();
    }

    public void refresh() {
        tableModel.setRowCount(0);
        ArrayList<Transaction> transactions = transactionManager.getTransactions();
        for (int i = 0; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            tableModel.addRow(new Object[]{
                    i + 1,
                    t.getType(),
                    t.getCategory(),
                    String.format("$%.2f", t.getAmount()),
                    t.getDate(),
                    t.getNote()
            });
        }
    }
}