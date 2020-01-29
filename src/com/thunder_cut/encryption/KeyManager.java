/*
 * KeyManager.java
 * Author: Seokjin Yoon
 * Created Date: 2020-01-27
 */

package com.thunder_cut.encryption;

import java.nio.channels.AsynchronousSocketChannel;
import java.security.Key;
import java.security.PublicKey;
import java.util.HashMap;

public class KeyManager {
    private static Key symmetricKey = null;
    private static HashMap<AsynchronousSocketChannel, PublicKey> publicKey = new HashMap<>();

    private KeyManager() {
        throw new AssertionError("No " + getClass().getName() + " instances for you!");
    }

    /**
     * Load a symmetric key.
     *
     * @return a symmetric key
     */
    public static Key getSymmetricKey() {
        return symmetricKey;
    }

    /**
     * Save a symmetric key.
     *
     * @param key a symmetric key
     */
    public static void setSymmetricKey(Key key) {
        symmetricKey = key;
    }

    /**
     * Load a public key for each client.
     *
     * @param client a client
     * @return a public key
     */
    public static PublicKey getPublicKey(AsynchronousSocketChannel client) {
        return publicKey.get(client);
    }

    /**
     * Save a public key for each client.
     *
     * @param client a client
     * @param key    a public key
     */
    public static void setPublicKey(AsynchronousSocketChannel client, PublicKey key) {
        publicKey.put(client, key);
    }
}
