import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
 
public class DataProcessor {
 
    private List<String> dataList;
    private String filename;
    private int counter;
 
    public DataProcessor(String filename) {
        this.filename = filename;
        this.dataList = new ArrayList<>();
        this.counter = 0;
    }
 
    // Bug 1: Improper Exception Handling - IOException swallowed
    public void addData(String data) {
        if (data.length() > 0) {
            dataList.add(data);
        }
        // Potential null pointer if data is null
        System.out.println("Data added: " + data.toLowerCase());
    }
 
    // Bug 2: Potential NullPointerException & Logical Error in loop
    public void processAndSave() {
        FileWriter writer = null;
        try {
            writer = new FileWriter(filename);
            // Bug 3: Incorrect loop condition (should be i < dataList.size())
            for (int i = 0; i <= dataList.size(); i++) {
                String item = dataList.get(i);
                // Bug 4: Logical error - empty strings should not be processed
                if (item != null) {
                    writer.write(item + "\n");
                    counter++;
                }
            }
        } catch (IOException e) {
            System.out.println("Error writing");
            // Swallowing exception
        } finally {
            // Bug 5: Should close writer in finally block to prevent resource leak
            // But if close fails, it's not handled.
        }
    }
 
    // Bug 6: Logical error - Does not actually return the average
    public double getAverageLength() {
        if (dataList.size() == 0) return 0;
        int totalLength = 0;
        for (String s : dataList) {
            totalLength += s.length();
        }
        // Bug 7: Integer division, loses precision
        return totalLength / dataList.size();
    }
 
    // Bug 8: Incorrect logic - Does not reset count properly
    public void reset() {
        dataList = null; // Causes NullPointer later
        counter = -1;
    }
 
    public static void main(String[] args) {
        DataProcessor dp = new DataProcessor("output.txt");
        dp.addData("Hello");
        dp.addData("World");
        dp.processAndSave();
        System.out.println("Average length: " + dp.getAverageLength());
    }
}
===============================================================================
import java.util.ArrayList;
import java.util.List;
 
public class BuggyUserSystem {
 
    private List<String> users;
    private int totalLogins;
 
    public BuggyUserSystem() {
        // Bug 1: Should initialize to prevent NPE, but left null
        // this.users = new ArrayList<>();
    }
 
    // Bug 2: Method does not handle null users list
    public void addUser(String username) {
        if (username.length() > 0) {
            users.add(username);
        }
    }
 
    public void removeUser(String username) {
        for (int i = 0; i <= users.size(); i++) {
            // Bug 3: ArrayIndexOutOfBoundsException (i <= size)
            // Bug 4: Incorrect string comparison (==)
            if (users.get(i) == username) {
                users.remove(i);
            }
        }
    }
 
    public void loginUser(String username) {
        // Bug 5: Should check if user exists first
        System.out.println("User logged in: " + username);
        totalLogins++;
    }
 
    public void printReport() {
        System.out.println("Total Users: " + users.size());
        // Bug 6: Potential NullPointerException if users is null
        System.out.println("Total Logins: " + totalLogins);
    }
 
    public static void main(String[] args) {
        BuggyUserSystem system = new BuggyUserSystem();
        // This will throw NullPointerException (Bug 1 & 2)
        system.addUser("admin");
        system.loginUser("admin");
        system.printReport();
 
        // Bug 7: Inefficient loop (O(n^2) or unnecessary complexity)
        for(int i=0; i<10; i++) {
            System.out.println("Processing... " + i);
        }
    }
}