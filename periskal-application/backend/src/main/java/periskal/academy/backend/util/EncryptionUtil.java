package periskal.academy.backend.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public final class EncryptionUtil {

    private EncryptionUtil() {} // Private constructor to prevent instantiation
    private static final String SALT = "PeriskalAcademy2024";
    

    public static String encode(String username, String password) throws Exception {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Username and password cannot be empty.");
        }

        String combined = username + ":" + password;
        String encrypted = encryptString(combined);
        return Base64.getEncoder().encodeToString(encrypted.getBytes(StandardCharsets.UTF_8));
    }

    private static String encryptString(String input) throws Exception {

        byte[] keyBytes = deriveKeyFromSalt(SALT);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // Using a zero IV
        byte[] iv = new byte[16];
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedBytes = cipher.doFinal(inputBytes);

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private static byte[] deriveKeyFromSalt(String salt) throws Exception {

        PBEKeySpec spec = new PBEKeySpec(salt.toCharArray(), salt.getBytes(StandardCharsets.UTF_8), 10000, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        return factory.generateSecret(spec).getEncoded();
    }

}
