package org.exqudens.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;

public interface EntryProcessor {

    default <K, V> Entry<K, V> entry(K key, V value) {
        return new SimpleEntry<>(key, value);
    }

    default <K, V> Entry<K, V> immutableEntry(K key, V value) {
        return new SimpleImmutableEntry<>(key, value);
    }

}
