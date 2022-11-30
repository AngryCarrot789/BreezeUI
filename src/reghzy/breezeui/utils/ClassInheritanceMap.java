package reghzy.breezeui.utils;

import org.jetbrains.annotations.Contract;

import java.util.LinkedHashMap;

public class ClassInheritanceMap<T> {
    private final LinkedHashMap<Class<?>, T> map;

    public ClassInheritanceMap() {
        this.map = new LinkedHashMap<Class<?>, T>();
    }

    @Contract("null->null")
    public T get(Object instance) {
        return instance != null ? get(instance.getClass()) : null;
    }

    public T get(Class<?> clazz) {
        while (clazz != null) {
            T t = this.map.get(clazz);
            if (t != null) {
                return t;
            }

            clazz = clazz.getSuperclass();
        }

        return null;
    }

    public void put(Class<?> clazz, T value) {
        this.map.put(clazz, value);
    }

    public void put(Object instance, T value) {
        put(instance.getClass(), value);
    }

    public void clear() {
        this.map.clear();
    }

    public int size() {
        return this.map.size();
    }
}
