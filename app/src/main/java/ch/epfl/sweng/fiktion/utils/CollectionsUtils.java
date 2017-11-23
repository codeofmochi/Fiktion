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

}
