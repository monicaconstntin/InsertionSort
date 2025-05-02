import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class InsertionSort{

    public static void main(String[] args) throws IOException {
        int size = 1_500_000;
        int[] array = generateRandomArray(size);
        System.out.println("Array generated. Starting sort...");

        long startTime = System.currentTimeMillis();
        insertionSort(array);
        long endTime = System.currentTimeMillis();

        System.out.println("Sorting completed in " + (endTime - startTime) / 1000.0 + " seconds.");

        try {
            saveArrayToFile(array, "sorted_output.txt");
            System.out.println("Sorted array saved to sorted_output.txt");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    // Insertion Sort implementation
    public static void insertionSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];
            int j = i - 1;

            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;

            // Optional: log progress every N steps
            if (i % 50000 == 0) {
                System.out.println("Sorted " + i + " elements...");
            }
        }
    }

    // Generate large array with random integers
    public static int[] generateRandomArray(int size) throws IOException {
        Random rand = new Random();
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = rand.nextInt(1_000_000); // values between 0 and 999,999
        }
        saveArrayToFile(arr, "input.txt");
        return arr;
    }

    // Save array to a file
    public static void saveArrayToFile(int[] arr, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (int num : arr) {
                writer.write(num + "\n");
            }
        }
    }
}
