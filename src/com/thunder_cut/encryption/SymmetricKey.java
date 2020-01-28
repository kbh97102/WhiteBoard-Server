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

    /**
     * Create a symmetric key.
     *
     * @param key      String to use as a symmetric key.
     * @param strength the strength of the key
     */
    public SymmetricKey(String key, int strength) {
        this.key = key.substring(0, strength / 8).getBytes(StandardCharsets.UTF_8);
        this.strength = strength;
    }

    /**
     * Get a symmetric key of byte[] type.
     *
     * @return a symmetric key of byte[] type
     */
    public byte[] getKey() {
        return key;
    }

    /**
     * Get the strength of the key
     *
     * @return the strength of the key
     */
    public int getStrength() {
        return strength;
    }

    /**
     * Returns a symmetric key of String type.
     *
     * @return a symmetric key of String type.
     */
    @Override
    public String toString() {
        return new String(key, StandardCharsets.UTF_8);
    }
}
