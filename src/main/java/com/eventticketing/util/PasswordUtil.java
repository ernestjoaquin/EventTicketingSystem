package com.eventticketing.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/** Simple SHA-256 password hashing helper. */
public class PasswordUtil {

    public static String hash(String plainText) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(plainText.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean verify(String plainText, String hashed) {
        return hash(plainText).equals(hashed);
    }
}
