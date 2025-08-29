import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Main {

    static Map<String, User> users = new ConcurrentHashMap<>();
    static Map<String, Account> accounts = new ConcurrentHashMap<>();

    static {
        users.put("admin", new User("admin", PasswordUtil.hash("admin123"), true));
        accounts.put("admin", new Account("admin"));
    }

    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";


    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println(BLUE+"================ MENU ================"+RESET);
            System.out.println(CYAN+"\n1. REGISTER"+RESET);
            System.out.println(CYAN+"\n2. LOGIN"+RESET);
            System.out.println(CYAN+"\n3. ADMIN LOGIN"+RESET);
            System.out.println(RED+"\n0. EXIT"+RESET);
            System.out.print( "\n"+BLUE+"Choose: "+RESET);
            String ch = sc.nextLine().trim();
            if (ch.equals("1")) register(sc);
            else if (ch.equals("2")) login(sc);
            else if (ch.equals("3")) adminLogin(sc);
            else if (ch.equals("0")) { System.out.println(BLUE + "============= Thank you ============="+RESET); break; }
            else System.out.println(RED+"Invalid choice"+RESET);
        }
    }

    static void register(Scanner sc) {
        System.out.print(BLUE+"Create username: ");
        String u = sc.nextLine().trim();
        if (u.isEmpty() || users.containsKey(u)) {
            System.out.println(RED+"******** Username invalid or taken ********"+RESET);
            return;
        }
        System.out.print(BLUE+"Set 4-8 digit PIN: ");
        String pin = sc.nextLine().trim();
        if (pin.length() < 4 || pin.length() > 8) {
            System.out.println(RED+"******* PIN must be between 4 and 8 digits *******"+RESET);
            return;
        }
        boolean allDigits = true;
        for (char c : pin.toCharArray()) {
            if (!Character.isDigit(c)) { allDigits = false; break; }
        }
        if (!allDigits) {
            System.out.println(RED+"PIN must contain only digits"+RESET);
            return;
        }
        accounts.putIfAbsent(u, new Account(u));
        users.put(u, new User(u, PasswordUtil.hash(pin), false));
        System.out.println(GREEN+"================ Registerd Successfully ================"+RESET);
    }

    static void login(Scanner sc) {
        System.out.print(BLUE+"Username: ");
        String u = sc.nextLine().trim();
        System.out.print(BLUE+"PIN: ");
        String p = sc.nextLine().trim();
        User user = users.get(u);
        if (user == null || !user.verify(p)) {
            System.out.println(RED+"*************** Login failed ***************"+RESET);
            return;
        }
        Account acc = accounts.get(u);
        System.out.println(GREEN+"================ Welcome " + u +" ================"+RESET );
        userMenu(sc, acc);
    }

    static void adminLogin(Scanner sc) {
        System.out.print(BLUE+"Admin username: ");
        String u = sc.nextLine().trim();
        System.out.print(BLUE+"Admin password: ");
        String p = sc.nextLine().trim();
        User admin = users.get(u);
        if (admin == null || !admin.verify(p) || !admin.isAdmin) {
            System.out.println(RED+"************ Admin login failed ************"+RESET);
            return;
        }
        System.out.println(GREEN+"=========== Welcome Admin " + u + " ===========" + RESET);
        adminMenu(sc);
    }

    static void userMenu(Scanner sc, Account acc) {
        while (true) {
            System.out.println(BLUE + "\n=========== USER MENU ===========" + RESET);
            System.out.println(CYAN + "1. BALANCE" + RESET);
            System.out.println(CYAN + "2. DEPOSIT" + RESET);
            System.out.println(CYAN + "3. WITHDRAW" + RESET);
            System.out.println(CYAN + "4. HISTORY" + RESET);
            System.out.println(CYAN + "5. TRANSFER" + RESET);
            System.out.println(RED  + "0. LOGOUT" + RESET);
            System.out.print("\n" + BLUE + "Choose: " + RESET);
            String ch = sc.nextLine().trim();
            if (ch.equals("1")) {
                System.out.println(BLUE + "Your Balance:" + GREEN +"₹" + acc.getBalance() + RESET);
            } else if (ch.equals("2")) {
                System.out.print(BLUE + "Enter deposit amount: " + RESET);
                try {
                    double amt = Double.parseDouble(sc.nextLine().trim());
                    if (amt <= 0) {
                        System.out.println(RED + "******* Amount must be positive ******* "+ RESET);
                    } else if (acc.isFrozen()) {
                        System.out.println(RED + "******** Account is frozen ********" + RESET);
                    } else {
                        acc.deposit(amt);
                        System.out.println(GREEN + "Deposited ₹" + amt + RESET);
                        System.out.println(BLUE + "New Balance:" + GREEN + "₹"  + acc.getBalance() + RESET);
                    }
                } catch (NumberFormatException e) {
                    System.out.println(RED + "*********** Invalid amount ***********" + RESET);
                }
            } else if (ch.equals("3")) {
                System.out.print(BLUE + "Enter withdraw amount: " + RESET);
                try {
                    double amt = Double.parseDouble(sc.nextLine().trim());
                    if (amt <= 0) {
                        System.out.println(RED + "********** Amount must be positive **********" + RESET);
                    } else if (amt > acc.getBalance()) {
                        System.out.println(RED + "*********** Insufficient Balance ***********" + RESET);
                    } else if (acc.isFrozen()) {
                        System.out.println(RED + "******** Account is frozen ********" + RESET);
                    } else {
                        acc.withdraw(amt);
                        System.out.println(GREEN + " Withdrawn ₹" + amt + RESET);
                        System.out.println(BLUE + "New Balance: " + GREEN + "₹" + acc.getBalance() + RESET);
                    }
                } catch (NumberFormatException e) {
                    System.out.println(RED + "*********** Invalid amount ***********" + RESET);
                }
            } else if (ch.equals("4")) {
                acc.printHistory();
            } else if (ch.equals("5")) {
                System.out.print(BLUE + "Enter target username: " + RESET);
                String target = sc.nextLine().trim();
                Account receiver = accounts.get(target);
                if (receiver == null) {
                    System.out.println(RED + "******** User not found ********" + RESET);
                } else {
                    System.out.print(BLUE + "Enter transfer amount: " + RESET);
                    try {
                        double amt = Double.parseDouble(sc.nextLine().trim());
                        if (amt <= 0) {
                            System.out.println(RED + "********** Amount must be positive **********" + RESET);
                        } else if (amt > acc.getBalance()) {
                            System.out.println(RED + "*********** Insufficient Balance ***********" + RESET);
                        } else if (acc.isFrozen()) {
                            System.out.println(RED + "******** Your account is frozen ********" + RESET);
                        } else if (receiver.isFrozen()) {
                            System.out.println(RED + "******** Receiver account is frozen ********" + RESET);
                        } else {
                            acc.transferTo(receiver, amt);
                            System.out.println(GREEN + "Transferred ₹" + amt + " to " + target + RESET);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println(RED + "*********** Invalid amount ***********" + RESET);
                    }
                }
            } else if (ch.equals("0")) {
                System.out.println(RED + "**************** Logged out ****************" + RESET);
                return;
            } else {
                System.out.println(RED + "Invalid choice" + RESET);
            }
        }
    }

    static void adminMenu(Scanner sc) {
        while (true) {
            System.out.println(PURPLE + "\n========= ADMIN MENU =========" + RESET);
            System.out.println(CYAN + "1. View All Users" + RESET);
            System.out.println(CYAN + "2. Freeze Account" + RESET);
            System.out.println(CYAN + "3. Unfreeze Account" + RESET);
            System.out.println(CYAN + "4. Audit User History" + RESET);
            System.out.println(RED + "0. Logout" + RESET);
            System.out.print("\nChoose: ");
            String ch = sc.nextLine().trim();
            if (ch.equals("1")) {
                for (Account acc : accounts.values()) {
                    System.out.println(YELLOW + "User: " + acc.owner +
                            " | Balance: ₹" + acc.getBalance() +
                            " | Status: " + acc.status + RESET);
                }
            } else if (ch.equals("2")) {
                System.out.print("Enter username to freeze: ");
                String u = sc.nextLine().trim();
                Account acc = accounts.get(u);
                if (acc != null) {
                    acc.status = "FROZEN";
                    System.out.println(RED + "Account frozen" + RESET);
                } else {
                    System.out.println(RED + "User not found" + RESET);
                }
            } else if (ch.equals("3")) {
                System.out.print("Enter username to unfreeze: ");
                String u = sc.nextLine().trim();
                Account acc = accounts.get(u);
                if (acc != null) {
                    acc.status = "ACTIVE";
                    System.out.println(GREEN + "Account unfrozen" + RESET);
                } else {
                    System.out.println(RED + "User not found" + RESET);
                }
            } else if (ch.equals("4")) {
                System.out.print("Enter username to audit: ");
                String u = sc.nextLine().trim();
                Account acc = accounts.get(u);
                if (acc != null) {
                    acc.printHistory();
                } else {
                    System.out.println(RED + "User not found" + RESET);
                }
            } else if (ch.equals("0")) {
                System.out.println(RED + "Admin logged out" + RESET);
                return;
            } else {
                System.out.println(RED + "Invalid choice" + RESET);
            }
        }
    }

    static class Account {
        String owner;
        double balance;
        String status = "ACTIVE";
        List<Transaction> history = new ArrayList<>();
        Account(String owner) {
            this.owner = owner;
            this.balance = 0.0;
        }
        boolean isFrozen() {
            return status.equals("FROZEN");
        }
        void deposit(double amt){
            balance += amt;
            history.add(new Transaction("Deposited", amt, balance));
        }
        void withdraw(double amt){
            balance -= amt;
            history.add(new Transaction("Withdrawn", amt, balance));
        }
        double getBalance() {
            return balance;
        }
        void printHistory() {
            if (history.isEmpty()) {
                System.out.println(RED + "********** No transactions yet **********" + RESET);
                return;
            }
            System.out.println(BLUE + "\n----- Transaction History -----" + RESET);
            for (Transaction t : history) {
                System.out.println(t);
            }
        }
        void transferTo(Account receiver, double amt) {
            this.balance -= amt;
            receiver.balance += amt;
            this.history.add(new Transaction("Transfer To: " + receiver.owner, amt, this.balance));
            receiver.history.add(new Transaction("Transfer From: " + this.owner, amt, receiver.balance));
        }
    }

    static class Transaction {
        String type;
        double amount;
        double balanceAfter;
        String time;
        Transaction(String type, double amount, double balanceAfter) {
            this.type = type;
            this.amount = amount;
            this.balanceAfter = balanceAfter;
            this.time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
        public String toString() {
            return CYAN + "[" + time + "] " + type + ": ₹" + amount + " | Balance After: ₹" + balanceAfter + RESET;
        }
    }

    static class User {
        String username;
        String pinHash;
        boolean isAdmin;
        Long accountId;
        User(String u, String h, boolean a) {
            this.username = u;
            this.pinHash = h;
            this.isAdmin = a;
        }
        boolean verify(String p) {
            return PasswordUtil.hash(p).equals(pinHash);
        }
    }

    static class PasswordUtil {
        static String hash(String s) {
            int h = 7;
            for (char c : s.toCharArray()) {
                h = h * 31 + c;
            }
            return Integer.toHexString(h);
        }
    }
}
