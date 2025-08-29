import java.sql.*;
import java.util.*;

public class UserDAO {
    private Connection conn;

    private static final String RESET = "\u001B[0m";
    private static final String BLUE = "\u001B[34m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";

    public UserDAO() {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/login_schema",
                    "root",
                    "$iddhesh_001"
            );
          //  System.out.println(GREEN + " Database connected successfully." + RESET);
        } catch (Exception e) {
            System.out.println(RED + "*** Database connection failed! ***" + RESET);
            e.printStackTrace();
        }
    }

    public boolean registerUser(String username, String password) {
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO users(username, password, balance) VALUES(?, ?, ?)");
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setDouble(3, 0.0);
            ps.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println(RED + "*** Username already exists. ***" + RESET);
            return false;
        } catch (Exception e) {
            System.out.println(RED + "*** Registration error. ***" + RESET);
            e.printStackTrace();
            return false;
        }
    }

    public boolean loginUser(String username, String password) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.out.println(RED + "*** Login error. ***" + RESET);
            e.printStackTrace();
            return false;
        }
    }

    public double getBalance(String username) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT balance FROM users WHERE username=?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) {
            System.out.println(RED + "*** Error fetching balance. ***" + RESET);
            e.printStackTrace();
        }
        return 0.0;
    }

    public boolean deposit(String username, double amt) {
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE users SET balance=balance+? WHERE username=?");
            ps.setDouble(1, amt);
            ps.setString(2, username);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                addTransaction(username, "Deposit", amt);
                return true;
            }
        } catch (Exception e) {
            System.out.println(RED + "*** Deposit error. ***" + RESET);
            e.printStackTrace();
        }
        return false;
    }

    public boolean withdraw(String username, double amt) {
        try {
            double bal = getBalance(username);
            if (bal >= amt) {
                PreparedStatement ps = conn.prepareStatement("UPDATE users SET balance=balance-? WHERE username=?");
                ps.setDouble(1, amt);
                ps.setString(2, username);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    addTransaction(username, "Withdraw", amt);
                    return true;
                }
            } else {
                System.out.println(RED + "*** Insufficient balance! ***" + RESET);
            }
        } catch (Exception e) {
            System.out.println(RED + "*** Withdraw error. ***" + RESET);
            e.printStackTrace();
        }
        return false;
    }

    public boolean transfer(String fromUser, String toUser, double amt) {
        try {
            double bal = getBalance(fromUser);
            if (bal >= amt) {
                conn.setAutoCommit(false);

                PreparedStatement ps1 = conn.prepareStatement("UPDATE users SET balance=balance-? WHERE username=?");
                ps1.setDouble(1, amt);
                ps1.setString(2, fromUser);
                ps1.executeUpdate();

                PreparedStatement ps2 = conn.prepareStatement("UPDATE users SET balance=balance+? WHERE username=?");
                ps2.setDouble(1, amt);
                ps2.setString(2, toUser);
                ps2.executeUpdate();

                addTransaction(fromUser, "Transfer to " + toUser, amt);
                addTransaction(toUser, "Transfer from " + fromUser, amt);

                conn.commit();
                conn.setAutoCommit(true);
                return true;
            } else {
                System.out.println(RED + "*** Insufficient balance for transfer! ***" + RESET);
            }
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (Exception ex) {
                System.out.println(RED + "*** Rollback failed! ***" + RESET);
            }
            System.out.println(RED + "*** Transfer error. ***" + RESET);
            e.printStackTrace();
        }
        return false;
    }

    private void addTransaction(String username, String type, double amt) {
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO transactions(username, type, amount) VALUES(?, ?, ?)");
            ps.setString(1, username);
            ps.setString(2, type);
            ps.setDouble(3, amt);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println(RED + "*** Error recording transaction. ***" + RESET);
            e.printStackTrace();
        }
    }

    public List<String> getTransactions(String username) {
        List<String> list = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM transactions WHERE username=? ORDER BY id DESC LIMIT 5");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("type") + " : " + rs.getDouble("amount"));
            }
        } catch (Exception e) {
            System.out.println(RED + "*** Error fetching transactions. ***" + RESET);
            e.printStackTrace();
        }
        return list;
    }

    public List<String> getAllUsers() {
        List<String> list = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT username, balance FROM users");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add("👤 " + rs.getString("username") + " | Balance: " + rs.getDouble("balance"));
            }
        } catch (Exception e) {
            System.out.println(RED + "*** Error fetching users. ***" + RESET);
            e.printStackTrace();
        }
        return list;
    }

    public List<String> getAllTransactions() {
        List<String> list = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM transactions ORDER BY id DESC LIMIT 20");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add("[" + rs.getInt("id") + "] " + rs.getString("username") + " | " +
                        rs.getString("type") + " | " + rs.getDouble("amount"));
            }
        } catch (Exception e) {
            System.out.println(RED + "*** Error fetching all transactions. ***" + RESET);
            e.printStackTrace();
        }
        return list;
    }

    public boolean deleteUser(String username) {
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE username=?");
            ps.setString(1, username);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            System.out.println(RED + "*** Error deleting user. ***" + RESET);
            e.printStackTrace();
            return false;
        }
    }
}
