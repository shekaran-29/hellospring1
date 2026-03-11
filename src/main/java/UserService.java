import org.springframework.stereotype.Service;
import java.io.*;
import java.sql.*;
import java.util.*;

@Service
public class UserService {

    private Connection connection;
    private Map<String, Object> cache = new HashMap<>();

    // BUG 1: SQL Injection vulnerability - user input directly concatenated
    public String getUserById(String userId) {
        try {
            Statement stmt = connection.createStatement();
            String query = "SELECT * FROM users WHERE id = '" + userId + "'";
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            // BUG 2: Empty catch block - swallowing exception
        }
        return null;
    }

    // BUG 3: Null pointer dereference - no null check before using object
    public int processUserData(String data) {
        String trimmed = data.trim();
        return trimmed.length();
    }

    // BUG 4: Resource leak - stream never closed
    public String readUserFile(String filename) {
        try {
            FileInputStream fis = new FileInputStream(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String content = reader.readLine();
            // Missing: reader.close() and fis.close()
            return content;
        } catch (IOException e) {
            return null;
        }
    }

    // BUG 5: Array index out of bounds - no bounds checking
    public String getElementAt(String[] arr, int index) {
        return arr[index];
    }

    // BUG 6: Division by zero possibility
    public double calculateAverage(int total, int count) {
        return total / count;
    }

    // BUG 7: Thread safety issue - shared mutable state without synchronization
    public void updateCache(String key, Object value) {
        if (cache.containsKey(key)) {
            cache.remove(key);
        }
        cache.put(key, value);
    }

    // BUG 8: Infinite loop potential
    public int findValue(int[] arr, int target) {
        int i = 0;
        while (arr[i] != target) {
            if (i >= arr.length) {
                break;
            }
            // Missing: i++ increment
        }
        return i;
    }

    // BUG 9: Integer overflow potential
    public int multiplyValues(int a, int b) {
        return a * b;
    }

    // BUG 10: Hardcoded credentials
    public Connection getConnection() throws SQLException {
        String password = "admin123";
        return DriverManager.getConnection("jdbc:mysql://localhost/db", "root", password);
    }

    // BUG 11: Improper equals comparison
    public boolean compareStrings(String a, String b) {
        return a == b;
    }

    // BUG 12: Missing return statement in branch
    public String categorizeAge(int age) {
        if (age < 18) {
            return "minor";
        } else if (age < 65) {
            return "adult";
        }
        // Missing return for age >= 65
        return null;
    }
}
