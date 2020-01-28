/*
 * KeyManager.java
 * Author: Seokjin Yoon
 * Created Date: 2020-01-27
 */

package com.thunder_cut.encryption;

import java.nio.channels.AsynchronousSocketChannel;
import java.security.PublicKey;
import java.util.HashMap;

public class KeyManager {
    private static SymmetricKey symmetricKey = null;
    private static HashMap<AsynchronousSocketChannel, PublicKey> publicKey = new HashMap<>();

    private KeyManager() {
        throw new AssertionError("No " + getClass().getName() + " instances for you!");
    }

    public static SymmetricKey getSymmetricKey() {
        return symmetricKey;
    }

    public static void setSymmetricKey(SymmetricKey key) {
        symmetricKey = key;
    }

    public static PublicKey getPublicKey(AsynchronousSocketChannel client) {
        return publicKey.get(client);
    }

    public static void setPublicKey(AsynchronousSocketChannel client, PublicKey key) {
        publicKey.put(client, key);
    }
}
