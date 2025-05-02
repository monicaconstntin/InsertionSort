import java.io.*;
import java.util.*;

public class InsertionSort {

    public static void main(String[] args) throws Exception {
        int[] array = readArrayFromFile("input5.txt");
        System.out.println("Array loaded. Starting parallel sort...");

        int numThreads = 4;
        int length = array.length;
        Thread[] threads = new Thread[numThreads];
        int partSize = length / numThreads;

        long startTime = System.currentTimeMillis();

        // paralel sort on chunks
        for (int i = 0; i < numThreads; i++) {
            int start = i * partSize;
            int end = (i == numThreads - 1) ? length : (i + 1) * partSize;
            threads[i] = new Thread(new SortTask(array, start, end));
            threads[i].start();
        }

        // wait for the treads to finish
        for (Thread thread : threads) {
            thread.join();
        }

        // merge sorted sections
        int[] sorted = mergeSortedSections(array, numThreads, partSize);

        long endTime = System.currentTimeMillis();

        double durationInSeconds = (endTime - startTime) / 1000.0;
        System.out.printf("Sorting completed in %.3f seconds.%n", durationInSeconds);

        saveArrayToFile(sorted, "sorted_output.txt");
        System.out.println("Sorted array saved to sorted_output.txt");
    }
    public static int[] readArrayFromFile(String filename) throws IOException {
        List<Integer> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                list.add(Integer.parseInt(line.trim()));
            }
        }
        return list.stream().mapToInt(i -> i).toArray();
    }

    public static void saveArrayToFile(int[] arr, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (int num : arr) {
                writer.write(num + "\n");
            }
        }
    }

    // sorting task for every Thread
    static class SortTask implements Runnable {
        private int[] arr;
        private int start, end;

        public SortTask(int[] arr, int start, int end) {
            this.arr = arr;
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            insertionSort(arr, start, end);
        }

        private void insertionSort(int[] arr, int start, int end) {
            for (int i = start + 1; i < end; i++) {
                int key = arr[i];
                int j = i - 1;
                while (j >= start && arr[j] > key) {
                    arr[j + 1] = arr[j];
                    j--;
                }
                arr[j + 1] = key;
            }
        }
    }

    // merge sorted sections
    public static int[] mergeSortedSections(int[] arr, int numSections, int sectionSize) {
        List<int[]> sections = new ArrayList<>();
        for (int i = 0; i < numSections; i++) {
            int start = i * sectionSize;
            int end = (i == numSections - 1) ? arr.length : (i + 1) * sectionSize;
            sections.add(Arrays.copyOfRange(arr, start, end));
        }

        // merge all the sections using a heap
        PriorityQueue<Element> heap = new PriorityQueue<>(Comparator.comparingInt(e -> e.value));
        int[] indices = new int[numSections];

        for (int i = 0; i < sections.size(); i++) {
            if (sections.get(i).length > 0)
                heap.offer(new Element(i, sections.get(i)[0]));
        }

        List<Integer> merged = new ArrayList<>();
        while (!heap.isEmpty()) {
            Element e = heap.poll();
            merged.add(e.value);
            indices[e.section]++;
            if (indices[e.section] < sections.get(e.section).length) {
                heap.offer(new Element(e.section, sections.get(e.section)[indices[e.section]]));
            }
        }

        return merged.stream().mapToInt(i -> i).toArray();
    }

    static class Element {
        int section;
        int value;

        Element(int section, int value) {
            this.section = section;
            this.value = value;
        }
    }
}
