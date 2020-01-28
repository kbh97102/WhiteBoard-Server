/*
 * PublicKeyEncryption.java
 * Author: Seokjin Yoon
 * Created Date: 2020-01-28
 */

package com.thunder_cut.encryption;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.PublicKey;
import java.util.Base64;

public class PublicKeyEncryption {
    private PublicKeyEncryption() {
        throw new AssertionError("No " + getClass().getName() + " instances for you!");
    }

    /**
     * Encrypt data with a public key using RSA.
     *
     * @param data plain data
     * @param key  a public key
     * @return encrypted data
     */
    public static byte[] encrypt(byte[] data, PublicKey key) {
        try {
            byte[] encrypted = getCipher(Cipher.ENCRYPT_MODE, key).doFinal(data);
            byte[] encoded = Base64.getEncoder().encode(encrypted);
            return encoded;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Cipher getCipher(int mode, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(mode, key);
        return cipher;
    }
}
