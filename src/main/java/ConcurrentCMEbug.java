import java.util.ArrayList;
import java.util.List;

public class BuggyCMEExample {
    public static void main(String[] args) {
        List<String> fruits = new ArrayList<>();
        fruits.add("Apple");
        fruits.add("Banana");
        fruits.add("Cherry");

        // This loop will throw ConcurrentModificationException
        for (String fruit : fruits) {
            System.out.println("Checking: " + fruit);
            if (fruit.equals("Banana")) {
                // BUG: Modifying the list directly while iterating
                fruits.remove(fruit); 
            }
        }
    }
}
