package main.application;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 *
 * Class used for generating hashed passwords.
 * Author: Oskar Mampe: U1564420
 * Date: 02/12/2018
 *
 */
public class HashPassword {

    /**
     *
     * Converts a string to a hashed string with salt.
     *
     * @param value string value to pass to encoder to be hashed
     * @param salt an additional value to strengthen hashes against dictionary attacks.
     * @return hashed {@link String}
     */
    public static String convertToHash(String value, byte[] salt) {
        try {
            //Create the encoder
            KeySpec spec = new PBEKeySpec(value.toCharArray(), salt, 65536, 128);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = secretKeyFactory.generateSecret(spec).getEncoded();
            Base64.Encoder encoder = Base64.getEncoder();
            return encoder.encodeToString(hash);//return the encoded string + salt
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * Generates a random salt
     *
     * @return byte[16] random salt
     */
    public static byte[] generateRandomSalt() {
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return salt;
    }
}
