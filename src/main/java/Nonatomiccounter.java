public class NonAtomicCounter {
    private int count = 0; // The shared non-atomic state

    // BUG: Not synchronized; multiple threads will overwrite each other
    public void increment() {
        count++; 
    }

    public int getCount() {
        return count;
    }

    public static void main(String[] args) throws InterruptedException {
        NonAtomicCounter counter = new NonAtomicCounter();
        
        // Create 100 threads that each increment 1000 times
        Thread[] threads = new Thread[100];
        for (int i = 0; i < 100; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) counter.increment();
            });
            threads[i].start();
        }

        for (Thread t : threads) t.join();
        
        // EXPECTED: 100,000 | ACTUAL: Likely a random lower number
        System.out.println("Final Count: " + counter.getCount());
    }
}
