package elb;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.*;
import java.util.Random;

/**
 * Unit test for simple App.
 */
public class AppTest
        extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    public void testSort() throws IOException {
        File file = new File("/test/input.txt");
        file.createNewFile();
        BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));

        while (true) {
            bw.write(nextInt(new Random(), Integer.MAX_VALUE) + "\n");
            if (file.length() / (1024 * 1024) > 50) {
                break;
            }
        }
        bw.close();

        new ExternalSort(1000, new File("/test/input.txt")).sort();
        BufferedReader reader = new BufferedReader(new FileReader(new File("/test/result.txt")));
        String line;
        int lastOne = Integer.MIN_VALUE;
        while ((line = reader.readLine()) != null) {
            Assert.assertTrue(Integer.parseInt(line) >= lastOne);
            lastOne = Integer.parseInt(line);
        }

    }

    int nextInt(Random rng, int n) {
        return rng.nextInt(n);
    }

    long nextLong(Random rng, long n) {
        long bits, val;
        do {
            bits = (rng.nextLong() << 1) >>> 1;
            val = bits % n;
        } while (bits - val + (n - 1) < 0L);
        return val;
    }
}
