/*
 * SymmetricKeyEncryption.java
 * Author: Seokjin Yoon
 * Created Date: 2020-01-27
 */

package com.thunder_cut.encryption;

import javax.crypto.Cipher;
import java.security.Key;
import java.util.Base64;

public class SymmetricKeyEncryption {
    public SymmetricKeyEncryption() {
    }

    /**
     * Encrypt data with a symmetric key using AES.
     *
     * @param data plain data
     * @param key  a symmetric key
     * @return encrypted data
     */
    public byte[] encrypt(byte[] data, Key key) {
        try {
            byte[] encrypted = getCipher(Cipher.ENCRYPT_MODE, key).doFinal(data);
            byte[] encoded = Base64.getEncoder().encode(encrypted);
            return encoded;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Decrypt data with a symmetric key using AES.
     *
     * @param data encrypted data
     * @param key  a symmetric key
     * @return decrypted data
     */
    public byte[] decrypt(byte[] data, Key key) {
        try {
            byte[] decoded = Base64.getDecoder().decode(data);
            byte[] decrypted = getCipher(Cipher.DECRYPT_MODE, key).doFinal(decoded);
            return decrypted;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Cipher getCipher(int mode, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(mode, key);
        return cipher;
    }
}
