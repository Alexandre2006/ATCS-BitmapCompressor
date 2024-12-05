/**
 *  Compilation:  javac BitmapCompressor.java
 *  Execution:    java BitmapCompressor - < input.bin   (compress)
 *  Execution:    java BitmapCompressor + < input.bin   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *  Data files:   q32x48.bin
 *                q64x96.bin
 *                mystery.bin
 * <p>
 *  Compress or expand binary input from standard input.
 * <p>
 *  % java DumpBinary 0 < mystery.bin
 *  8000 bits
 * <p>
 *  % java BitmapCompressor - < mystery.bin | java DumpBinary 0
 *  1240 bits
 */

import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 *  The {@code BitmapCompressor} class provides static methods for compressing
 *  and expanding a binary bitmap input.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *  @author Zach Blick
 *  @author Alexandre Haddad-Delaveau
 */
public class BitmapCompressor {
    /**
     * Reads a sequence of bits from standard input, compresses them,
     * and writes the results to standard output.
     */
    static int LENGTH = 8;
    static int MAX = (int) (Math.pow(2, LENGTH - 1) - 1);

    public static void compress() {
        boolean lastValue = false;
        int valueCount = 0;

        while (!BinaryStdIn.isEmpty()) {
            boolean currentValue = BinaryStdIn.readBoolean();

            if (currentValue != lastValue) {
                // Write count
                BinaryStdOut.write(valueCount, LENGTH);

                // Switch value (if necessary) and reset counter
                lastValue = currentValue;
                valueCount = 1;
            } else {
                // Increment count
                valueCount ++;

                // Check if max has been reached
                if (valueCount == MAX) {
                    BinaryStdOut.write(0, LENGTH);
                    valueCount = 0;
                }
            }
        }

        // If end, write remaining count
        if (valueCount != 0) {
            BinaryStdOut.write(valueCount);
        }

        BinaryStdOut.close();
    }

    /**
     * Reads a sequence of bits from standard input, decodes it,
     * and writes the results to standard output.
     */
    public static void expand() {
        boolean lastValue = false;

        while(!BinaryStdIn.isEmpty()) {
            int count = BinaryStdIn.readInt(LENGTH);

            if (count != 0) {
                // Write number of values
                for (int i = 0; i < count; i++) {
                    BinaryStdOut.write(lastValue);
                }

                lastValue = !lastValue;
            } else {
                for (int i = 0; i < MAX; i++) {
                    BinaryStdOut.write(lastValue);
                }
            }
        }

        BinaryStdOut.close();
    }

    /**
     * When executed at the command-line, run {@code compress()} if the command-line
     * argument is "-" and {@code expand()} if it is "+".
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}