import java.util.*;

public class BankApp {
    private static Scanner sc = new Scanner(System.in);
    private static UserDAO userDAO = new UserDAO();
    private static String loggedInUser = null;

    private static final String RESET = "\u001B[0m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";

    public static void main(String[] args) {
        while (true) {
            System.out.println(BLUE + "\n--- BankEase ---" + RESET);
            System.out.println(CYAN + "1. Register" + RESET);
            System.out.println(CYAN + "2. Login" + RESET);
            System.out.println(CYAN + "3. Exit" + RESET);
            System.out.print(BLUE + "Enter choice: " + RESET);
            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> register();
                case 2 -> login();
                case 3 -> {
                    System.out.println(BLUE + "Goodbye!" + RESET);
                    System.exit(0);
                }
                default -> System.out.println(RED + "*** Invalid choice! ***" + RESET);
            }
        }
    }

    private static void register() {
        System.out.print(BLUE + "Enter username: " + RESET);
        String username = sc.next();
        System.out.print(BLUE + "Enter password: " + RESET);
        String password = sc.next();

        if (userDAO.registerUser(username, password)) {
            System.out.println(GREEN + "✅ Registration successful!" + RESET);
        } else {
            System.out.println(RED + "*** Registration failed (username may exist). ***" + RESET);
        }
    }

    private static void login() {
        System.out.print(BLUE + "Enter username: " + RESET);
        String username = sc.next();
        System.out.print(BLUE + "Enter password: " + RESET);
        String password = sc.next();

        if (userDAO.loginUser(username, password)) {
            loggedInUser = username;
            System.out.println(GREEN + "✅ Login successful!" + RESET);

            if (username.equals("admin")) {
                adminMenu();
            } else {
                userMenu();
            }
        } else {
            System.out.println(RED + "*** Login failed. Try again. ***" + RESET);
        }
    }

    private static void userMenu() {
        while (true) {
            System.out.println(BLUE + "\n--- User Menu (" + loggedInUser + ") ---" + RESET);
            System.out.println(CYAN + "1. Check Balance" + RESET);
            System.out.println(CYAN + "2. Deposit" + RESET);
            System.out.println(CYAN + "3. Withdraw" + RESET);
            System.out.println(CYAN + "4. Transfer" + RESET);
            System.out.println(CYAN + "5. View Transactions" + RESET);
            System.out.println(CYAN + "6. Logout" + RESET);
            System.out.print(BLUE + "Enter choice: " + RESET);
            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> System.out.println(BLUE + "Balance: " + userDAO.getBalance(loggedInUser) + RESET);
                case 2 -> {
                    System.out.print(BLUE + "Enter amount: " + RESET);
                    double amt = sc.nextDouble();
                    if (userDAO.deposit(loggedInUser, amt))
                        System.out.println(GREEN + "✅ Deposit successful!" + RESET);
                    else
                        System.out.println(RED + "*** Deposit failed. ***" + RESET);
                }
                case 3 -> {
                    System.out.print(BLUE + "Enter amount: " + RESET);
                    double amt = sc.nextDouble();
                    if (userDAO.withdraw(loggedInUser, amt))
                        System.out.println(GREEN + "✅ Withdraw successful!" + RESET);
                    else
                        System.out.println(RED + "*** Not enough balance. ***" + RESET);
                }
                case 4 -> {
                    System.out.print(BLUE + "Enter recipient username: " + RESET);
                    String toUser = sc.next();
                    System.out.print(BLUE + "Enter amount: " + RESET);
                    double amt = sc.nextDouble();
                    if (userDAO.transfer(loggedInUser, toUser, amt))
                        System.out.println(GREEN + "✅ Transfer successful!" + RESET);
                    else
                        System.out.println(RED + "*** Transfer failed. ***" + RESET);
                }
                case 5 -> {
                    List<String> txns = userDAO.getTransactions(loggedInUser);
                    System.out.println(BLUE + "--- Last Transactions ---" + RESET);
                    for (String t : txns) System.out.println(BLUE + t + RESET);
                }
                case 6 -> {
                    loggedInUser = null;
                    System.out.println(RED + "*** Logged out. ***" + RESET);
                    return;
                }
                default -> System.out.println(RED + "*** Invalid choice! ***" + RESET);
            }
        }
    }

    private static void adminMenu() {
        while (true) {
            System.out.println(BLUE + "\n--- Admin Menu ---" + RESET);
            System.out.println(CYAN + "1. View All Users" + RESET);
            System.out.println(CYAN + "2. View All Transactions" + RESET);
            System.out.println(CYAN + "3. Delete User" + RESET);
            System.out.println(CYAN + "4. Logout" + RESET);
            System.out.print(BLUE + "Enter choice: " + RESET);
            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> {
                    List<String> users = userDAO.getAllUsers();
                    System.out.println(BLUE + "--- Users ---" + RESET);
                    for (String u : users) System.out.println(BLUE + u + RESET);
                }
                case 2 -> {
                    List<String> txns = userDAO.getAllTransactions();
                    System.out.println(BLUE + "--- All Transactions ---" + RESET);
                    for (String t : txns) System.out.println(BLUE + t + RESET);
                }
                case 3 -> {
                    System.out.print(BLUE + "Enter username to delete: " + RESET);
                    String uname = sc.next();
                    if (userDAO.deleteUser(uname))
                        System.out.println(GREEN + "✅ User deleted." + RESET);
                    else
                        System.out.println(RED + "*** Could not delete user. ***" + RESET);
                }
                case 4 -> {
                    loggedInUser = null;
                    System.out.println(RED + "*** Logged out. ***" + RESET);
                    return;
                }
                default -> System.out.println(RED + "*** Invalid choice! ***" + RESET);
            }
        }
    }
}
