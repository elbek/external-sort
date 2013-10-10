package elb;

import java.io.*;
import java.util.*;

/**
 * @author ekamoliddinov
 */
public class ExternalSort {
    long maxKBAvailable;
    File inputFile;

    public ExternalSort(long maxKBAvailable, File inputFile) {
        this.maxKBAvailable = maxKBAvailable;
        this.inputFile = inputFile;
    }

    public File sort() throws IOException {

        List<ExternalSortItem> items = prorate();

        int numbersCountPerItem = (int) (maxKBAvailable * 1024 / (8 * (items.size() + 1)));
        for (ExternalSortItem item : items) {
            item.setNumbersCountCanBeLoad(numbersCountPerItem);
        }

        long numbersInMemory[] = new long[numbersCountPerItem];
        int pointer = 0;
        File file = new File(inputFile.getParentFile().getAbsolutePath() + "/result.txt");
        while (true) {
            long min = Long.MAX_VALUE;
            Iterator<ExternalSortItem> itemIterator = items.iterator();
            ExternalSortItem minimalFound = null;
            while (itemIterator.hasNext()) {
                ExternalSortItem item = itemIterator.next();
                if (item.get() == null) {
                    item.removeFile();
                    itemIterator.remove();
                    continue;
                }
                if (item.get() < min) {
                    min = item.get();
                    minimalFound = item;
                }
            }

            if (items.size() != 0 && pointer != numbersCountPerItem) {
                numbersInMemory[pointer++] = min;
                minimalFound.remove();
            } else if (pointer == numbersCountPerItem) {
                writeToFile(file, numbersInMemory, false);
                pointer = 0;
            }
            if (items.size() == 0) {
                if (pointer > 0) {
                    writeToFileLastChunk(file, numbersInMemory, false, pointer);
                }
                break;
            }
        }

        return file;
    }

    private List<ExternalSortItem> prorate() throws IOException {
        List<ExternalSortItem> items = new LinkedList<ExternalSortItem>();
        BufferedReader br = new BufferedReader(new FileReader(inputFile));

        int filePointer = 0;
        File file = new File(inputFile.getParentFile().getAbsolutePath() + "/" + new Integer(filePointer++));
        int numbersCount = (int) (maxKBAvailable * 1024 / 8);
        long numbersInMemory[] = new long[numbersCount];
        int pointer = 0;
        String line;
        while ((line = br.readLine()) != null) {
            if (pointer == numbersCount) {
                writeToFile(file, numbersInMemory, true);
                pointer = 0;
                items.add(new ExternalSortItem(file));
                file = new File(inputFile.getParentFile().getAbsolutePath() + "/" + new Integer(filePointer++));
            }
            numbersInMemory[pointer++] = new Long(line);
        }
        if (pointer > 0) {
            writeToFileLastChunk(file, numbersInMemory, true, pointer);
            items.add(new ExternalSortItem(file));
        }

        br.close();
        return items;
    }


    private void writeToFileLastChunk(File file, long[] numbersInMemory, boolean sort, int length) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        if (sort)
            Arrays.sort(numbersInMemory, 0, length);

        FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
        BufferedWriter bw = new BufferedWriter(fw);
        for (int i = 0; i < length; i++) {
            long l = numbersInMemory[i];
            bw.write(Long.toString(l) + "\n");
        }
        bw.close();
    }

    private void writeToFile(File file, long[] numbersInMemory, boolean sort) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        if (sort)
            Arrays.sort(numbersInMemory);

        FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
        BufferedWriter bw = new BufferedWriter(fw);
        for (long aNumbersInMemory : numbersInMemory) {
            bw.write(Long.toString(aNumbersInMemory) + "\n");
        }
        bw.close();
    }

    class ExternalSortItem {
        private File file;
        private long numbersCountCanBeLoad;
        Queue<Long> numbers = new LinkedList<Long>();
        BufferedReader br;


        ExternalSortItem(File file) throws FileNotFoundException {
            this.file = file;
            br = new BufferedReader(new FileReader(file));
        }

        public void remove() {
            numbers.poll();
        }

        public Long get() throws IOException {
            if (numbers.size() == 0) {
                String line;
                long read = 0;
                while ((line = br.readLine()) != null) {
                    read++;
                    numbers.add(Long.parseLong(line));
                    if (read == numbersCountCanBeLoad) {
                        break;
                    }
                }
            }
            if (numbers.size() == 0) {
                br.close();
                return null;
            }
            return numbers.peek();

        }

        public void setNumbersCountCanBeLoad(long numbersCountCanBeLoad) {
            this.numbersCountCanBeLoad = numbersCountCanBeLoad;
        }

        public void removeFile() {
            file.deleteOnExit();
        }
    }
}
