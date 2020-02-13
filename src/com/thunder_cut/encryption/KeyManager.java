/*
 * KeyManager.java
 * Author: Seokjin Yoon
 * Created Date: 2020-01-27
 */

package com.thunder_cut.encryption;

import javax.crypto.KeyGenerator;
import java.nio.channels.SocketChannel;
import java.security.Key;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.HashMap;

public class KeyManager {
    private Key symmetricKey;
    private HashMap<SocketChannel, PublicKey> publicKey;

    public KeyManager() {
        symmetricKey = null;
        publicKey = new HashMap<>();
    }

    /**
     * Load a symmetric key.
     *
     * @return a symmetric key
     */
    public Key getSymmetricKey() {
        return symmetricKey;
    }

    /**
     * Save a symmetric key.
     *
     * @param key a symmetric key
     */
    public void setSymmetricKey(Key key) {
        symmetricKey = key;
    }

    /**
     * Generate a symmetric key with secure random.
     *
     * @param strength
     * @return a symmetric key
     */
    public static Key generateSymmetricKey(int strength) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            keyGenerator.init(strength, secureRandom);
            Key key = keyGenerator.generateKey();
            return key;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Load a public key for each client.
     *
     * @param client a client
     * @return a public key
     */
    public PublicKey getPublicKey(SocketChannel client) {
        return publicKey.get(client);
    }

    /**
     * Save a public key for each client.
     *
     * @param client a client
     * @param key    a public key
     */
    public void setPublicKey(SocketChannel client, PublicKey key) {
        publicKey.put(client, key);
    }

    /**
     * Remove a public key.
     *
     * @param client a client
     * @return a public key
     */
    public PublicKey removePublicKey(SocketChannel client) {
        return publicKey.remove(client);
    }
}
