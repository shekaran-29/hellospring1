import java.util.*;

public class BuggyCacheManager {
    private static BuggyCacheManager instance; // BUG 1: Not volatile
    private Map<String, Object> cache = new HashMap<>(); // BUG 2: Not thread-safe

    private BuggyCacheManager() {}

    public static BuggyCacheManager getInstance() {
        if (instance == null) { // BUG 3: Race condition (Double-checked locking failure)
            instance = new BuggyCacheManager();
        }
        return instance;
    }

    public void addToCache(String key, Object value) {
        // BUG 4: Memory Leak - entries are never removed, growing indefinitely
        cache.put(key, value); 
    }

    public void processCache() {
        // BUG 5: Throws ConcurrentModificationException if another thread calls addToCache
        for (String key : cache.keySet()) {
            System.out.println("Processing: " + key);
            if (key.contains("old")) cache.remove(key); 
        }
    }

    public static void main(String[] args) {
        // Simulate multi-threaded access to trigger the Race Condition
        Runnable task = () -> BuggyCacheManager.getInstance().addToCache("data", new byte[1024*1024]);
        for(int i=0; i<100; i++) new Thread(task).start();
    }
}
