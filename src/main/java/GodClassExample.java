import org.springframework.stereotype.Service;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.net.*;
import java.text.*;
import java.security.*;

/**
 * GOD CLASS ANTI-PATTERN EXAMPLE
 * This class violates Single Responsibility Principle by doing everything:
 * - User management
 * - Database operations
 * - File handling
 * - Email sending
 * - Logging
 * - Caching
 * - Authentication
 * - Reporting
 * - Configuration
 * - Validation
 */
@Service
public class GodClassExample {

    // Too many instance variables - code smell
    private Connection dbConnection;
    private Statement statement;
    private ResultSet resultSet;
    private Map<String, Object> cache = new HashMap<>();
    private Map<String, String> config = new HashMap<>();
    private List<String> logs = new ArrayList<>();
    private List<String> errors = new ArrayList<>();
    private Map<Integer, String> users = new HashMap<>();
    private Map<String, String> sessions = new HashMap<>();
    private Map<String, Integer> loginAttempts = new HashMap<>();
    private Properties properties = new Properties();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Random random = new Random();
    private MessageDigest md5;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private FileOutputStream fileOut;
    private FileInputStream fileIn;
    private int userCount = 0;
    private int errorCount = 0;
    private int requestCount = 0;
    private boolean isInitialized = false;
    private boolean isConnected = false;
    private boolean cacheEnabled = true;
    private String dbUrl;
    private String dbUser;
    private String dbPassword;
    private String emailHost;
    private String logFile;

    // ==================== DATABASE OPERATIONS ====================
    
    public void initDatabase(String url, String user, String pass) {
        this.dbUrl = url;
        this.dbUser = user;
        this.dbPassword = pass;
        try {
            dbConnection = DriverManager.getConnection(url, user, pass);
            statement = dbConnection.createStatement();
            isConnected = true;
            log("Database connected");
        } catch (SQLException e) {
            errors.add(e.getMessage());
        }
    }

    public ResultSet executeQuery(String sql) {
        try {
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            return null;
        }
    }

    public int executeUpdate(String sql) {
        try {
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            return -1;
        }
    }

    // BUG: SQL Injection
    public String getUserName(String id) {
        try {
            ResultSet rs = statement.executeQuery("SELECT name FROM users WHERE id = '" + id + "'");
            if (rs.next()) return rs.getString(1);
        } catch (SQLException e) {}
        return null;
    }

    // BUG: SQL Injection
    public boolean deleteUser(String id) {
        try {
            statement.executeUpdate("DELETE FROM users WHERE id = " + id);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    // ==================== USER MANAGEMENT ====================
    
    public int createUser(String name, String email, String password) {
        int id = ++userCount;
        users.put(id, name);
        try {
            String hashedPass = hashPassword(password);
            String sql = "INSERT INTO users VALUES (" + id + ", '" + name + "', '" + email + "', '" + hashedPass + "')";
            statement.executeUpdate(sql);
            log("User created: " + name);
            sendEmail(email, "Welcome", "Your account has been created");
        } catch (Exception e) {
            errors.add("User creation failed: " + e.getMessage());
            return -1;
        }
        return id;
    }

    public boolean updateUser(int id, String name, String email) {
        if (!users.containsKey(id)) return false;
        users.put(id, name);
        try {
            statement.executeUpdate("UPDATE users SET name='" + name + "', email='" + email + "' WHERE id=" + id);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public Map<Integer, String> getAllUsers() {
        return users;
    }

    public String getUser(int id) {
        return users.get(id);
    }

    public int getUserCount() {
        return userCount;
    }

    // ==================== AUTHENTICATION ====================
    
    public String login(String username, String password) {
        int attempts = loginAttempts.getOrDefault(username, 0);
        if (attempts >= 5) {
            log("Account locked: " + username);
            return null;
        }
        
        try {
            ResultSet rs = statement.executeQuery(
                "SELECT id, password FROM users WHERE name = '" + username + "'");
            if (rs.next()) {
                String storedPass = rs.getString("password");
                if (storedPass.equals(hashPassword(password))) {
                    String session = generateSessionId();
                    sessions.put(session, username);
                    loginAttempts.put(username, 0);
                    log("Login successful: " + username);
                    return session;
                }
            }
        } catch (Exception e) {}
        
        loginAttempts.put(username, attempts + 1);
        log("Login failed: " + username);
        return null;
    }

    public boolean logout(String sessionId) {
        if (sessions.containsKey(sessionId)) {
            String user = sessions.remove(sessionId);
            log("Logout: " + user);
            return true;
        }
        return false;
    }

    public boolean isValidSession(String sessionId) {
        return sessions.containsKey(sessionId);
    }

    // BUG: Weak password hashing
    private String hashPassword(String password) {
        try {
            if (md5 == null) md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return password; // BUG: Returns plain password on failure
        }
    }

    private String generateSessionId() {
        return Long.toHexString(random.nextLong()) + Long.toHexString(System.currentTimeMillis());
    }

    // ==================== FILE OPERATIONS ====================
    
    // BUG: Path traversal vulnerability
    public String readFile(String filename) {
        try {
            fileIn = new FileInputStream("/data/" + filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(fileIn));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
            // BUG: Resource leak - stream not closed
            return content.toString();
        } catch (IOException e) {
            return null;
        }
    }

    // BUG: Path traversal vulnerability
    public boolean writeFile(String filename, String content) {
        try {
            fileOut = new FileOutputStream("/data/" + filename);
            fileOut.write(content.getBytes());
            // BUG: Resource leak - stream not closed
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean deleteFile(String filename) {
        return new File("/data/" + filename).delete();
    }

    public List<String> listFiles(String directory) {
        File dir = new File(directory);
        return Arrays.asList(dir.list());
    }

    // ==================== EMAIL OPERATIONS ====================
    
    public boolean sendEmail(String to, String subject, String body) {
        try {
            socket = new Socket(emailHost, 25);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            writer.println("HELO localhost");
            reader.readLine();
            writer.println("MAIL FROM:<noreply@app.com>");
            reader.readLine();
            writer.println("RCPT TO:<" + to + ">");
            reader.readLine();
            writer.println("DATA");
            reader.readLine();
            writer.println("Subject: " + subject);
            writer.println();
            writer.println(body);
            writer.println(".");
            reader.readLine();
            writer.println("QUIT");
            
            log("Email sent to: " + to);
            // BUG: Socket not closed
            return true;
        } catch (IOException e) {
            errors.add("Email failed: " + e.getMessage());
            return false;
        }
    }

    public boolean sendBulkEmail(List<String> recipients, String subject, String body) {
        boolean allSuccess = true;
        for (String recipient : recipients) {
            if (!sendEmail(recipient, subject, body)) {
                allSuccess = false;
            }
        }
        return allSuccess;
    }

    // ==================== CACHING ====================
    
    public void putCache(String key, Object value) {
        if (cacheEnabled) {
            cache.put(key, value);
        }
    }

    public Object getCache(String key) {
        return cache.get(key);
    }

    public void clearCache() {
        cache.clear();
    }

    public void enableCache(boolean enabled) {
        this.cacheEnabled = enabled;
    }

    public int getCacheSize() {
        return cache.size();
    }

    // ==================== CONFIGURATION ====================
    
    public void loadConfig(String filename) {
        try {
            properties.load(new FileInputStream(filename));
            for (String key : properties.stringPropertyNames()) {
                config.put(key, properties.getProperty(key));
            }
            // BUG: Hardcoded credentials loaded from config
            dbUrl = config.get("db.url");
            dbUser = config.get("db.user");
            dbPassword = config.get("db.password");
            emailHost = config.get("email.host");
            logFile = config.get("log.file");
            isInitialized = true;
        } catch (IOException e) {
            errors.add("Config load failed");
        }
    }

    public String getConfig(String key) {
        return config.get(key);
    }

    public void setConfig(String key, String value) {
        config.put(key, value);
    }

    // ==================== LOGGING ====================
    
    public void log(String message) {
        String entry = dateFormat.format(new Date()) + " - " + message;
        logs.add(entry);
        System.out.println(entry);
        try {
            if (logFile != null) {
                FileWriter fw = new FileWriter(logFile, true);
                fw.write(entry + "\n");
                // BUG: FileWriter not closed
            }
        } catch (IOException e) {}
    }

    public void logError(String message) {
        errorCount++;
        log("ERROR: " + message);
        errors.add(message);
    }

    public List<String> getLogs() {
        return logs;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void clearLogs() {
        logs.clear();
    }

    // ==================== REPORTING ====================
    
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== SYSTEM REPORT ===\n");
        report.append("Date: ").append(dateFormat.format(new Date())).append("\n");
        report.append("Users: ").append(userCount).append("\n");
        report.append("Sessions: ").append(sessions.size()).append("\n");
        report.append("Cache size: ").append(cache.size()).append("\n");
        report.append("Errors: ").append(errorCount).append("\n");
        report.append("Requests: ").append(requestCount).append("\n");
        report.append("DB Connected: ").append(isConnected).append("\n");
        return report.toString();
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("users", userCount);
        stats.put("sessions", sessions.size());
        stats.put("cache", cache.size());
        stats.put("errors", errorCount);
        stats.put("requests", requestCount);
        return stats;
    }

    // ==================== VALIDATION ====================
    
    // BUG: Weak email validation
    public boolean validateEmail(String email) {
        return email != null && email.contains("@");
    }

    // BUG: Weak password validation
    public boolean validatePassword(String password) {
        return password != null && password.length() >= 4;
    }

    public boolean validateUsername(String username) {
        return username != null && username.length() >= 3 && username.length() <= 20;
    }

    // BUG: No actual validation
    public boolean validateInput(String input) {
        return input != null;
    }

    // ==================== NETWORK OPERATIONS ====================
    
    public String httpGet(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            // BUG: Connection not closed
            return response.toString();
        } catch (Exception e) {
            return null;
        }
    }

    // ==================== UTILITY METHODS ====================
    
    // BUG: Division by zero possible
    public double calculateAverage(int[] numbers) {
        int sum = 0;
        for (int n : numbers) sum += n;
        return sum / numbers.length;
    }

    // BUG: Null pointer dereference
    public int getStringLength(String str) {
        return str.trim().length();
    }

    // BUG: Array index out of bounds
    public Object getElement(Object[] arr, int index) {
        return arr[index];
    }

    // BUG: Integer overflow
    public long multiply(int a, int b) {
        return a * b;
    }

    // BUG: String comparison with ==
    public boolean compareStrings(String a, String b) {
        return a == b;
    }

    public void incrementRequestCount() {
        requestCount++;
    }

    public void shutdown() {
        try {
            if (statement != null) statement.close();
            if (dbConnection != null) dbConnection.close();
            if (socket != null) socket.close();
            if (fileIn != null) fileIn.close();
            if (fileOut != null) fileOut.close();
        } catch (Exception e) {
            logError("Shutdown error: " + e.getMessage());
        }
        log("System shutdown");
    }
}
