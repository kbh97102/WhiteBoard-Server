/*
 * KeyManager.java
 * Author: Seokjin Yoon
 * Created Date: 2020-01-27
 */

package com.thunder_cut.encryption;

import com.thunder_cut.socket.Attachment;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.HashMap;

public class KeyManager {
    private static byte[] symmetricKey = null;
    private static HashMap<Attachment, PublicKey> publicKey = new HashMap<>();

    private KeyManager() {
    }

    public static byte[] getSymmetricKey() {
        return symmetricKey;
    }

    public static void setSymmetricKey(String key) {
        symmetricKey = key.substring(0, 128 / 8).getBytes(StandardCharsets.UTF_8);
    }

    public static PublicKey getPublicKey(Attachment client) {
        return publicKey.get(client);
    }

    public static void setPublicKey(Attachment client, PublicKey key) {
        publicKey.put(client, key);
    }
}
