import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.sql.*;
import java.util.*;

@RestController
public class BuggyController {

    // CRITICAL BUG: SQL Injection - direct string concatenation
    @GetMapping("/user")
    public String getUser(@RequestParam String id) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/db", "root", "password123");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE id = " + id);
        return rs.getString(1);
    }

    // CRITICAL BUG: Path Traversal vulnerability
    @GetMapping("/file")
    public String readFile(@RequestParam String name) throws IOException {
        FileReader fr = new FileReader("/data/" + name);
        BufferedReader br = new BufferedReader(fr);
        return br.readLine();
    }

    // BUG: Null dereference - calling method on potentially null
    @PostMapping("/process")
    public int processData(@RequestBody Map<String, String> data) {
        String value = data.get("key");
        return value.length() + value.hashCode();
    }

    // BUG: Resource leak - connection never closed
    @GetMapping("/count")
    public int getCount() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/db", "root", "pass");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
        rs.next();
        return rs.getInt(1);
    }

    // BUG: Infinite loop
    @GetMapping("/loop")
    public int infiniteLoop(int[] arr, int target) {
        int i = 0;
        while (true) {
            if (arr[i] == target)
                return i;
        }
    }

    // BUG: Array index out of bounds
    @GetMapping("/element")
    public String getElement(@RequestParam int idx) {
        String[] items = { "a", "b", "c" };
        return items[idx];
    }

    // BUG: Division by zero
    @GetMapping("/divide")
    public int divide(@RequestParam int a, @RequestParam int b) {
        return a / b;
    }

    // BUG: Hardcoded secrets
    private static final String API_KEY = "sk-1234567890abcdef";
    private static final String DB_PASSWORD = "SuperSecret123!";

    // BUG: Unchecked cast
    @SuppressWarnings("unchecked")
    public List<String> unsafeCast(Object obj) {
        return (List<String>) obj;
    }

    // BUG: Empty catch block hiding errors
    @GetMapping("/silent")
    public String silentFail() {
        try {
            throw new RuntimeException("Error");
        } catch (Exception e) {
            // swallowed
        }
        return "ok";
    }

    // BUG: Concurrent modification
    @GetMapping("/concurrent")
    public void modifyWhileIterating() {
        List<String> list = new ArrayList<>(Arrays.asList("a", "b", "c"));
        for (String s : list) {
            if (s.equals("b")) {
                list.remove(s);
            }
        }
    }

    // BUG: String comparison with ==
    @GetMapping("/compare")
    public boolean compareStrings(@RequestParam String a, @RequestParam String b) {
        return a == b;
    }
}
