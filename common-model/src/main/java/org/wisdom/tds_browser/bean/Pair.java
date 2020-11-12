package org.wisdom.tds_browser.bean;

import java.io.Serializable;

public class Pair<k, v> implements Serializable {

    public k key;

    public v value;

    public Pair(k key, v value) {
        this.key = key;
        this.value = value;
    }

    public static <k, v> Pair with(k key, v value) {
        return new Pair(key, value);
    }

    public k getKey() {
        return key;
    }

    public v getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }
}
