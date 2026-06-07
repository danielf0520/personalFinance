import javax.swing.*;
import java.awt.*;
import java.util.*;

public class ChartPanel extends JPanel {

    private TransactionManager transactionManager;
    private GraphPanel graphPanel;

    public ChartPanel(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ── Title ──
        JLabel title = new JLabel("Income vs Expenses Over Time");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        // ── Center: graph ──
        graphPanel = new GraphPanel();
        add(graphPanel, BorderLayout.CENTER);

        // ── Legend ──
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 5));

        JLabel incomeLegend  = new JLabel("— Income");
        incomeLegend.setForeground(new Color(0, 150, 0));
        incomeLegend.setFont(new Font("Arial", Font.BOLD, 13));

        JLabel expenseLegend = new JLabel("— Expenses");
        expenseLegend.setForeground(Color.RED);
        expenseLegend.setFont(new Font("Arial", Font.BOLD, 13));

        JLabel balanceLegend = new JLabel("— Balance");
        balanceLegend.setForeground(Color.BLUE);
        balanceLegend.setFont(new Font("Arial", Font.BOLD, 13));

        legendPanel.add(incomeLegend);
        legendPanel.add(expenseLegend);
        legendPanel.add(balanceLegend);
        add(legendPanel, BorderLayout.SOUTH);

        refresh();
    }

    public void refresh() {
        ArrayList<Transaction> all = transactionManager.getTransactions();

        // Sort everything by date
        all.sort(Comparator.comparing(Transaction::getDate));

        // Build cumulative data points
        ArrayList<String> dates          = new ArrayList<>();
        ArrayList<Double> incomePoints   = new ArrayList<>();
        ArrayList<Double> expensePoints  = new ArrayList<>();
        ArrayList<Double> balancePoints  = new ArrayList<>();

        double cumIncome  = 0;
        double cumExpense = 0;

        for (Transaction t : all) {
            if (t.getType().equals("Income")) {
                cumIncome += t.getAmount();
            } else {
                cumExpense += t.getAmount();
            }
            dates.add(t.getDate());
            incomePoints.add(cumIncome);
            expensePoints.add(cumExpense);
            balancePoints.add(cumIncome - cumExpense);
        }

        graphPanel.setData(dates, incomePoints, expensePoints, balancePoints);
        graphPanel.repaint();
    }

    // ── Inner class that draws the graph ──
    class GraphPanel extends JPanel {

        private ArrayList<String> dates         = new ArrayList<>();
        private ArrayList<Double> incomePoints  = new ArrayList<>();
        private ArrayList<Double> expensePoints = new ArrayList<>();
        private ArrayList<Double> balancePoints = new ArrayList<>();

        public void setData(ArrayList<String> dates,
                            ArrayList<Double> incomePoints,
                            ArrayList<Double> expensePoints,
                            ArrayList<Double> balancePoints) {
            this.dates         = dates;
            this.incomePoints  = incomePoints;
            this.expensePoints = expensePoints;
            this.balancePoints = balancePoints;
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width     = getWidth();
            int height    = getHeight();
            int padLeft   = 70;
            int padBottom = 50;
            int padTop    = 20;
            int padRight  = 20;

            int graphWidth  = width  - padLeft - padRight;
            int graphHeight = height - padTop  - padBottom;

            // ── Background ──
            g2.setColor(Color.WHITE);
            g2.fillRect(padLeft, padTop, graphWidth, graphHeight);

            // ── Border ──
            g2.setColor(Color.BLACK);
            g2.drawRect(padLeft, padTop, graphWidth, graphHeight);

            // ── No data message ──
            if (dates.isEmpty()) {
                g2.setColor(Color.GRAY);
                g2.setFont(new Font("Arial", Font.ITALIC, 14));
                String msg = "No transactions yet. Add some to see the chart.";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(msg, (width - fm.stringWidth(msg)) / 2, height / 2);
                return;
            }

            // ── Find max value for scaling ──
            double maxVal = 0;
            for (double d : incomePoints)  if (d > maxVal) maxVal = d;
            for (double d : expensePoints) if (d > maxVal) maxVal = d;
            if (maxVal == 0) maxVal = 100;

            // ── Y axis grid lines and labels ──
            g2.setFont(new Font("Arial", Font.PLAIN, 11));
            int ySteps = 5;
            for (int i = 0; i <= ySteps; i++) {
                double val  = (maxVal / ySteps) * i;
                int    yPos = padTop + graphHeight - (int)((val / maxVal) * graphHeight);

                // Grid line
                g2.setColor(new Color(220, 220, 220));
                g2.drawLine(padLeft, yPos, padLeft + graphWidth, yPos);

                // Label
                g2.setColor(Color.BLACK);
                g2.drawString(String.format("$%.0f", val), 5, yPos + 4);
            }

            int n = dates.size();

            // ── Draw a line helper ──
            // We draw income, expense, and balance lines
            drawLine(g2, incomePoints,  new Color(0, 150, 0), n, padLeft, padTop, graphWidth, graphHeight, maxVal);
            drawLine(g2, expensePoints, Color.RED,            n, padLeft, padTop, graphWidth, graphHeight, maxVal);
            drawLine(g2, balancePoints, Color.BLUE,           n, padLeft, padTop, graphWidth, graphHeight, maxVal);

            // ── X axis date labels ──
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.PLAIN, 10));

            // Only show a max of 8 date labels so they don't overlap
            int step = Math.max(1, n / 8);
            for (int i = 0; i < n; i += step) {
                int x = padLeft + (int)((double) i / Math.max(n - 1, 1) * graphWidth);
                String dateLabel = dates.get(i).length() >= 10
                        ? dates.get(i).substring(5)
                        : dates.get(i);
                g2.drawString(dateLabel, x - 15, height - padBottom + 15);
            }

            // ── Summary text in top right ──
            if (!incomePoints.isEmpty()) {
                double lastIncome  = incomePoints.get(incomePoints.size() - 1);
                double lastExpense = expensePoints.get(expensePoints.size() - 1);
                double lastBalance = lastIncome - lastExpense;

                g2.setFont(new Font("Arial", Font.BOLD, 11));

                g2.setColor(new Color(0, 150, 0));
                g2.drawString(String.format("Total Income:   $%.2f", lastIncome),
                        padLeft + graphWidth - 160, padTop + 20);

                g2.setColor(Color.RED);
                g2.drawString(String.format("Total Expenses: $%.2f", lastExpense),
                        padLeft + graphWidth - 160, padTop + 35);

                g2.setColor(Color.BLUE);
                g2.drawString(String.format("Balance:        $%.2f", lastBalance),
                        padLeft + graphWidth - 160, padTop + 50);
            }
        }

        private void drawLine(Graphics2D g2, ArrayList<Double> points, Color color,
                              int n, int padLeft, int padTop,
                              int graphWidth, int graphHeight, double maxVal) {
            if (points.isEmpty()) return;

            g2.setColor(color);
            g2.setStroke(new BasicStroke(2));

            if (n == 1) {
                int x = padLeft + graphWidth / 2;
                int y = padTop + graphHeight - (int)((points.get(0) / maxVal) * graphHeight);
                g2.fillOval(x - 4, y - 4, 8, 8);
                return;
            }

            for (int i = 0; i < n - 1; i++) {
                int x1 = padLeft + (int)((double) i       / (n - 1) * graphWidth);
                int x2 = padLeft + (int)((double)(i + 1)  / (n - 1) * graphWidth);
                int y1 = padTop + graphHeight - (int)((points.get(i)     / maxVal) * graphHeight);
                int y2 = padTop + graphHeight - (int)((points.get(i + 1) / maxVal) * graphHeight);
                g2.drawLine(x1, y1, x2, y2);
            }

            // Dots at each point
            for (int i = 0; i < n; i++) {
                int x = padLeft + (int)((double) i / (n - 1) * graphWidth);
                int y = padTop + graphHeight - (int)((points.get(i) / maxVal) * graphHeight);
                g2.fillOval(x - 3, y - 3, 6, 6);
            }

            g2.setStroke(new BasicStroke(1));
        }
    }
}