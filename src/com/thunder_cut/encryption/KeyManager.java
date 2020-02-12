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
    private Key symmetricKey;
    private HashMap<AsynchronousSocketChannel, PublicKey> publicKey;

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
     * Load a public key for each client.
     *
     * @param client a client
     * @return a public key
     */
    public PublicKey getPublicKey(AsynchronousSocketChannel client) {
        return publicKey.get(client);
    }

    /**
     * Save a public key for each client.
     *
     * @param client a client
     * @param key    a public key
     */
    public void setPublicKey(AsynchronousSocketChannel client, PublicKey key) {
        publicKey.put(client, key);
    }
}
