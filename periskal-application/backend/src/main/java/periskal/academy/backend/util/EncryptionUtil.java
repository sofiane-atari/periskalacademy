package periskal.academy.backend.util;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtil {

    private static final String SALT = "PeriskalAcademy2024";

    public static String encode(String username, String password) throws Exception {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Username and password cannot be empty.");
        }

        String combined = username + ":" + password;
        String encrypted = encryptString(combined);
        return Base64.getEncoder().encodeToString(encrypted.getBytes("UTF-8"));
    }

    private static String encryptString(String input) throws Exception {

        byte[] keyBytes = deriveKeyFromSalt(SALT);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // Using a zero IV
        byte[] iv = new byte[16];
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] inputBytes = input.getBytes("UTF-8");
        byte[] encryptedBytes = cipher.doFinal(inputBytes);

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private static byte[] deriveKeyFromSalt(String salt) throws Exception {

        PBEKeySpec spec = new PBEKeySpec(salt.toCharArray(), salt.getBytes("UTF-8"), 10000, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        return factory.generateSecret(spec).getEncoded();
    }

}
