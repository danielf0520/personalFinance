import java.util.ArrayList;
import java.io.*;

public class TransactionManager {
    private ArrayList<Transaction> transactions;
    private java.util.HashMap<String, Double> limits;
    private static final String FILE_PATH  = "transactions.csv";
    private static final String LIMITS_PATH = "limits.csv";

    public TransactionManager() {
        transactions = new ArrayList<>();
        limits = new java.util.HashMap<>();
        loadTransactions();
        loadLimits();
    }

    // ── Transactions ──

    public void addTransaction(Transaction t) {
        transactions.add(t);
        saveTransactions();
    }

    public void removeTransaction(int index) {
        transactions.remove(index);
        saveTransactions();
    }

    public void updateTransaction(int index, Transaction t) {
        transactions.set(index, t);
        saveTransactions();
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public double getBalance() {
        double balance = 0;
        for (Transaction t : transactions) {
            if (t.getType().equals("Income")) balance += t.getAmount();
            else balance -= t.getAmount();
        }
        return balance;
    }

    public double getTotalByCategory(String category) {
        double total = 0;
        for (Transaction t : transactions) {
            if (t.getCategory().equals(category) && t.getType().equals("Expense")) {
                total += t.getAmount();
            }
        }
        return total;
    }

    public double getTotalIncome() {
        double total = 0;
        for (Transaction t : transactions)
            if (t.getType().equals("Income")) total += t.getAmount();
        return total;
    }

    public double getTotalExpenses() {
        double total = 0;
        for (Transaction t : transactions)
            if (t.getType().equals("Expense")) total += t.getAmount();
        return total;
    }

    // ── Limits ──

    public void setLimit(String category, double limit) {
        limits.put(category, limit);
        saveLimits();
    }

    public double getLimit(String category) {
        return limits.getOrDefault(category, 0.0);
    }

    public java.util.HashMap<String, Double> getAllLimits() {
        return limits;
    }

    // ── File saving/loading ──

    private void saveTransactions() {
        try {
            FileWriter fw = new FileWriter(FILE_PATH);
            for (Transaction t : transactions)
                fw.write(t.toCSV() + "\n");
            fw.close();
        } catch (IOException e) {
            System.out.println("Error saving transactions: " + e.getMessage());
        }
    }

    private void loadTransactions() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(FILE_PATH));
            String line;
            while ((line = br.readLine()) != null)
                if (!line.trim().isEmpty())
                    transactions.add(Transaction.fromCSV(line));
            br.close();
        } catch (FileNotFoundException e) {
            // First run, no file yet
        } catch (IOException e) {
            System.out.println("Error loading transactions: " + e.getMessage());
        }
    }

    private void saveLimits() {
        try {
            FileWriter fw = new FileWriter(LIMITS_PATH);
            for (String category : limits.keySet())
                fw.write(category + "," + limits.get(category) + "\n");
            fw.close();
        } catch (IOException e) {
            System.out.println("Error saving limits: " + e.getMessage());
        }
    }

    private void loadLimits() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(LIMITS_PATH));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(",");
                    limits.put(parts[0], Double.parseDouble(parts[1]));
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            // First run, no file yet
        } catch (IOException e) {
            System.out.println("Error loading limits: " + e.getMessage());
        }
    }
}