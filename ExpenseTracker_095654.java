import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExpenseTracker {

    private static final String FILE_NAME = "expenses.txt";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    static class Expense {
        Date date;
        String category;
        double amount;

        Expense(Date date, String category, double amount) {
            this.date = date;
            this.category = category;
            this.amount = amount;
        }

        @Override
        public String toString() {
            return DATE_FORMAT.format(date) + " | " + category + " | $" + amount;
        }
    }

    private static Map<String, List<Expense>> expenses = new HashMap<>();

    public static void main(String[] args) {
        loadExpenses();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nExpense Tracker");
            System.out.println("1. Create Account");
            System.out.println("2. Add Expense");
            System.out.println("3. View Expenses");
            System.out.println("4. View Expenses by Category");
            System.out.println("5. Save and Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline
            switch (choice) {
                case 1:
                    createAccount(scanner);
                    break;
                case 2:
                    addExpense(scanner);
                    break;
                case 3:
                    viewExpenses();
                    break;
                case 4:
                    viewExpensesByCategory();
                    break;
                case 5:
                    saveExpenses();
                    System.out.println("Data saved. Exiting.");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void createAccount(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        if (!expenses.containsKey(username)) {
            expenses.put(username, new ArrayList<>());
            System.out.println("Account created successfully.");
        } else {
            System.out.println("Account already exists.");
        }
    }

    private static void addExpense(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        if (!expenses.containsKey(username)) {
            System.out.println("Account does not exist. Please create an account first.");
            return;
        }
        try {
            System.out.print("Enter date (yyyy-MM-dd): ");
            Date date = DATE_FORMAT.parse(scanner.nextLine());
            System.out.print("Enter category: ");
            String category = scanner.nextLine();
            System.out.print("Enter amount: ");
            double amount = scanner.nextDouble();
            scanner.nextLine();  // Consume newline
            expenses.get(username).add(new Expense(date, category, amount));
            System.out.println("Expense added successfully.");
        } catch (ParseException e) {
            System.out.println("Invalid date format.");
        }
    }

    private static void viewExpenses() {
        for (Map.Entry<String, List<Expense>> entry : expenses.entrySet()) {
            System.out.println("\nExpenses for user: " + entry.getKey());
            for (Expense expense : entry.getValue()) {
                System.out.println(expense);
            }
        }
    }

    private static void viewExpensesByCategory() {
        System.out.print("Enter username: ");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();
        if (!expenses.containsKey(username)) {
            System.out.println("Account does not exist.");
            return;
        }
        Map<String, Double> categoryTotals = new HashMap<>();
        for (Expense expense : expenses.get(username)) {
            categoryTotals.merge(expense.category, expense.amount, Double::sum);
        }
        System.out.println("\nExpenses by Category for user: " + username);
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            System.out.println(entry.getKey() + ": $" + entry.getValue());
        }
    }

    private static void saveExpenses() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(expenses);
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    private static void loadExpenses() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                expenses = (Map<String, List<Expense>>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error loading data: " + e.getMessage());
            }
        }
    }
}
