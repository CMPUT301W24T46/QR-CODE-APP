package com.example.eventapp.helpers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * ProfileHashGenerator provides a utility method to generate a SHA-256 hash from a given text.
 * This class can be used for generating unique identifiers or for other cryptographic purposes
 * where a secure hash function is needed.
 */

public class ProfileHashGenerator {

    /**
     * Generates a SHA-256 hash of the provided text.
     *
     * @param text The input text to hash.
     * @return A string representation of the SHA-256 hash.
     * @throws RuntimeException If the SHA-256 MessageDigest algorithm is not available.
     */

    public static String generateSHA256(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(text.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
