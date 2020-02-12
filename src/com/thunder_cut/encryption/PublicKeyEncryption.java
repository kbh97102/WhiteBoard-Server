/*
 * PublicKeyEncryption.java
 * Author: Seokjin Yoon
 * Created Date: 2020-01-28
 */

package com.thunder_cut.encryption;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class PublicKeyEncryption {
    public PublicKeyEncryption() {
    }

    /**
     * Encrypt data with a public key using RSA.
     *
     * @param data plain data
     * @param key  a public key
     * @return encrypted data
     */
    public byte[] encrypt(byte[] data, PublicKey key) {
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
     * Decrypt data with a private key using RSA.
     *
     * @param data encrypted data
     * @param key  a private key
     * @return decrypted data
     */
    public byte[] decrypt(byte[] data, PrivateKey key) {
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
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(mode, key);
        return cipher;
    }
}
