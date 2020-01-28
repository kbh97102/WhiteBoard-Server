/*
 * SymmetricKey.java
 * Author: Seokjin Yoon
 * Created Date: 2020-01-28
 */

package com.thunder_cut.encryption;

import java.nio.charset.StandardCharsets;

public class SymmetricKey {
    private byte[] key;
    private int strength;

    public SymmetricKey(String key, int strength) {
        this.key = key.substring(0, strength / 8).getBytes(StandardCharsets.UTF_8);
        this.strength = strength;
    }

    public byte[] getKey() {
        return key;
    }

    public int getStrength() {
        return strength;
    }

    @Override
    public String toString() {
        return new String(key, StandardCharsets.UTF_8);
    }
}
