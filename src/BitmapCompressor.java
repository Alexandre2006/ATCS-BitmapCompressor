/******************************************************************************
 *  Compilation:  javac BitmapCompressor.java
 *  Execution:    java BitmapCompressor - < input.bin   (compress)
 *  Execution:    java BitmapCompressor + < input.bin   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *  Data files:   q32x48.bin
 *                q64x96.bin
 *                mystery.bin
 *
 *  Compress or expand binary input from standard input.
 *
 *  % java DumpBinary 0 < mystery.bin
 *  8000 bits
 *
 *  % java BitmapCompressor - < mystery.bin | java DumpBinary 0
 *  1240 bits
 ******************************************************************************/

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 *  The {@code BitmapCompressor} class provides static methods for compressing
 *  and expanding a binary bitmap input.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *  @author Zach Blick
 *  @author YOUR NAME HERE
 */
public class BitmapCompressor {

    /**
     * Reads a sequence of bits from standard input, compresses them,
     * and writes the results to standard output.
     */
    public static void compress() {

        LinkedHashSet<Byte> uniqueBytes = new LinkedHashSet<>();
        ArrayList<Byte> bytes = new ArrayList<>();

        while (!BinaryStdIn.isEmpty()) {
            byte byteRead = BinaryStdIn.readByte();
            uniqueBytes.add(byteRead);
            bytes.add(byteRead);
        }

        // Count # of unique chars and length
        int unique = uniqueBytes.size();
        int length = bytes.size();

        // Find size of each byte
        int remainderSize = 3;
        int lengthOfByte = (int) Math.ceil(Math.log(unique) / Math.log(2));
        lengthOfByte = lengthOfByte > 0 ? lengthOfByte : 1; // Minimum 0 bytes

        // Find remainder
        int remainder = (remainderSize + (lengthOfByte * length)) % 8;

        // Write remainder & # of unique elements
        BinaryStdOut.write(remainder, remainderSize);
        BinaryStdOut.write(unique, 8);

        // Write each unique element
        for (Byte uniqueElement : uniqueBytes) {
            BinaryStdOut.write(uniqueElement);
        }

        // Convert hashset to list
        ArrayList<Byte> uniqueBytesList = new ArrayList<>(uniqueBytes);

        // Write each element
        for (Byte messageByte : bytes) {
            BinaryStdOut.write(uniqueBytesList.indexOf(messageByte), lengthOfByte);
        }

        BinaryStdOut.close();
    }

    /**
     * Reads a sequence of bits from standard input, decodes it,
     * and writes the results to standard output.
     */
    public static void expand() {

        // Read remainder and unique bytes
        int remainder = BinaryStdIn.readInt(3);
        int unique = BinaryStdIn.readInt(8);

        // Read unique bytes
        ArrayList<Byte> uniqueBytes = new ArrayList<>();
        for (int i = 0; i < unique; i++) {
            uniqueBytes.add(BinaryStdIn.readByte());
        }

        // Calculate size of each byte
        int lengthOfByte = (int) Math.ceil(Math.log(unique) / Math.log(2));
        lengthOfByte = lengthOfByte > 0 ? lengthOfByte : 1; // Minimum 0 bytes

        // Read fu
        ArrayList<Integer> buffer = new ArrayList<>();
        while (!BinaryStdIn.isEmpty()) {
            try {
                buffer.add(BinaryStdIn.readInt(lengthOfByte));
            } catch (Exception e) {
                break;
            }
        }

        // Remove remainder
        int byteRemainder = remainder / lengthOfByte;

        // Write each element
        for (Integer index : buffer.subList(0, buffer.size() - byteRemainder)) {
            BinaryStdOut.write(uniqueBytes.get(index));
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