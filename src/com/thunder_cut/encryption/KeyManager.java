/*
 * KeyManager.java
 * Author: Seokjin Yoon
 * Created Date: 2020-01-27
 */

package com.thunder_cut.encryption;

import com.thunder_cut.socket.Attachment;

import java.security.PublicKey;
import java.util.HashMap;

public class KeyManager {
    private static SymmetricKey symmetricKey = null;
    private static HashMap<Attachment, PublicKey> publicKey = new HashMap<>();

    private KeyManager() {
    }

    public static SymmetricKey getSymmetricKey() {
        return symmetricKey;
    }

    public static void setSymmetricKey(SymmetricKey key) {
        symmetricKey = key;
    }

    public static PublicKey getPublicKey(Attachment client) {
        return publicKey.get(client);
    }

    public static void setPublicKey(Attachment client, PublicKey key) {
        publicKey.put(client, key);
    }
}
