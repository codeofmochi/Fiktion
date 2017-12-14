package ch.epfl.sweng.fiktion.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by painguin on 14.12.17.
 */

public class HashUtils {
    public static String sha256(String s) throws NoSuchAlgorithmException {
        return sha256(s.getBytes());
    }

    public static String sha256(byte[] bytes) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(bytes);
        return CollectionsUtils.bytesToHexString(hash);
    }
}
