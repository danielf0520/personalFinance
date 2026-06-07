public class Transaction {
    private String type;
    private String category;
    private double amount;
    private String date;
    private String note;

    public Transaction(String type, String category, double amount, String date, String note) {
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.note = note;
    }

    public String getType()     { return type; }
    public String getCategory() { return category; }
    public double getAmount()   { return amount; }
    public String getDate()     { return date; }
    public String getNote()     { return note; }

    public void setType(String type)         { this.type = type; }
    public void setCategory(String category) { this.category = category; }
    public void setAmount(double amount)     { this.amount = amount; }
    public void setDate(String date)         { this.date = date; }
    public void setNote(String note)         { this.note = note; }

    public String toCSV() {
        return type + "," + category + "," + amount + "," + date + "," + note;
    }

    public static Transaction fromCSV(String line) {
        String[] parts = line.split(",");
        return new Transaction(parts[0], parts[1], Double.parseDouble(parts[2]), parts[3], parts[4]);
    }
}