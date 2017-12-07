package ch.epfl.sweng.fiktion.utils;

/**
 * Util function on collections
 * Created by dialexo on 24.11.17.
 */

public class CollectionsUtils {

    public static String mkString(Iterable<String> collection, String separator) {
        if (!collection.iterator().hasNext()) return "";
        StringBuilder sb = new StringBuilder();
        for (String e : collection) {
            sb.append(e);
            sb.append(separator);
        }
        int sbl = sb.length();
        sb.delete(sbl - separator.length(), sbl);
        return sb.toString();
    }

    /**
     * converts an array of bytes into a string, each byte is converted with its hexadecimal
     * representation
     *
     * @param bytes the bytes to convert
     * @return the string of the resulting conversion
     */
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


}
