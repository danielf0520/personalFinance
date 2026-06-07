import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private TransactionManager transactionManager;
    private AddPanel addPanel;
    private EditPanel editPanel;
    private LimitPanel limitPanel;
    private ChartPanel chartPanel;

    public MainFrame() {
        transactionManager = new TransactionManager();

        setTitle("Personal Finance Manager");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addPanel   = new AddPanel(transactionManager);
        editPanel  = new EditPanel(transactionManager);
        limitPanel = new LimitPanel(transactionManager);
        chartPanel = new ChartPanel(transactionManager);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Add Transaction", addPanel);
        tabs.addTab("Edit Transactions", editPanel);
        tabs.addTab("Spending Limit", limitPanel);
        tabs.addTab("Chart", chartPanel);

        // Refresh panels when switching tabs
        tabs.addChangeListener(e -> {
            int i = tabs.getSelectedIndex();
            if (i == 1) editPanel.refresh();
            if (i == 2) limitPanel.refresh();
            if (i == 3) chartPanel.refresh();
        });

        add(tabs);
    }
}