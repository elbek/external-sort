package elb;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

    /**
     * Rigourous Test :-)
     */
    public void testSort() throws IOException {
        new ExternalSort(1000, new File("/test/input.txt")).sort();
    }

    public void testApp() throws IOException {
        File file = new File("/test/input.txt");
        file.createNewFile();
        BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));

        while (true) {
            bw.write(nextInt(new Random(), Integer.MAX_VALUE) + "\n");
            if (file.length() / (1024 * 1024) > 500) {
                break;
            }
        }
        bw.close();
    }

    int nextInt(Random rng, int n) {
        return rng.nextInt(n);
    }

    long nextLong(Random rng, long n) {
        // error checking and 2^x checking removed for simplicity.
        long bits, val;
        do {
            bits = (rng.nextLong() << 1) >>> 1;
            val = bits % n;
        } while (bits - val + (n - 1) < 0L);
        return val;
    }
}
