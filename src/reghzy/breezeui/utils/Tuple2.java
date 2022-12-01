package reghzy.breezeui.utils;

import java.util.Map;

public class Tuple2<K, V> implements Map.Entry<K, V> {
    public final K a;
    public final V b;

    public Tuple2(K a, V b) {

        this.a = a;
        this.b = b;
    }

    @Override
    public K getKey() {
        return this.a;
    }

    @Override
    public V getValue() {
        return this.b;
    }

    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException();
    }
}
