/*
 * SymmetricKeyEncryption.java
 * Author: Seokjin Yoon
 * Created Date: 2020-01-27
 */

package com.thunder_cut.encryption;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class SymmetricKeyEncryption {
    private SymmetricKeyEncryption() {
    }

    public static byte[] encrypt(byte[] data, SymmetricKey key) {
        try {
            byte[] encrypted = getCipher(Cipher.ENCRYPT_MODE, key).doFinal(data);
            byte[] encoded = Base64.getEncoder().encode(encrypted);
            return encoded;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] decrypt(byte[] data, SymmetricKey key) {
        try {
            byte[] decoded = Base64.getDecoder().decode(data);
            byte[] decrypted = getCipher(Cipher.DECRYPT_MODE, key).doFinal(decoded);
            return decrypted;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Cipher getCipher(int mode, SymmetricKey symmetricKey) throws Exception {
        byte[] key = symmetricKey.getKey();
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(mode, new SecretKeySpec(key, "AES"), new IvParameterSpec(key));
        return cipher;
    }
}
