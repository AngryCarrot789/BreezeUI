package reghzy.breezeui.utils;

import java.lang.reflect.Array;
import java.util.List;

public class Memory {
    @SuppressWarnings("unchecked")
    public static <E> E[] toArray(List<E> collection, Class<E> clazz) {
        int size = collection.size();
        E[] array = (E[]) Array.newInstance(clazz, size);
        for (int i = 0; i < size; ++i) {
            array[i] = collection.get(i);
        }

        return array;
    }

    public static Byte[] wrap(byte[] array) {
        Byte[] arr = new Byte[array.length];
        for (int i = 0; i < array.length; ++i)
            arr[i] = array[i];
        return arr;
    }

    public static Short[] wrap(short[] array) {
        Short[] arr = new Short[array.length];
        for (int i = 0; i < array.length; ++i)
            arr[i] = array[i];
        return arr;
    }

    public static Integer[] wrap(int[] array) {
        Integer[] arr = new Integer[array.length];
        for (int i = 0; i < array.length; ++i)
            arr[i] = array[i];
        return arr;
    }

    public static Long[] wrap(long[] array) {
        Long[] arr = new Long[array.length];
        for (int i = 0; i < array.length; ++i)
            arr[i] = array[i];
        return arr;
    }

    public static Float[] wrap(float[] array) {
        Float[] arr = new Float[array.length];
        for (int i = 0; i < array.length; ++i)
            arr[i] = array[i];
        return arr;
    }

    public static Double[] wrap(double[] array) {
        Double[] arr = new Double[array.length];
        for (int i = 0; i < array.length; ++i)
            arr[i] = array[i];
        return arr;
    }

    public static Character[] wrap(char[] array) {
        Character[] arr = new Character[array.length];
        for (int i = 0; i < array.length; ++i)
            arr[i] = array[i];
        return arr;
    }

    public static Boolean[] wrap(boolean[] array) {
        Boolean[] arr = new Boolean[array.length];
        for (int i = 0; i < array.length; ++i)
            arr[i] = array[i];
        return arr;
    }

    public static byte[] unwrap(Byte[] array) {
        byte[] arr = new byte[array.length];
        for (int i = 0, len = array.length; i < len; ++i)
            arr[i] = array[i];
        return arr;
    }

    public static short[] unwrap(Short[] array) {
        short[] arr = new short[array.length];
        for (int i = 0, len = array.length; i < len; ++i)
            arr[i] = array[i];
        return arr;
    }

    public static int[] unwrap(Integer[] array) {
        int[] arr = new int[array.length];
        for (int i = 0, len = array.length; i < len; ++i)
            arr[i] = array[i];
        return arr;
    }

    public static long[] unwrap(Long[] array) {
        long[] arr = new long[array.length];
        for (int i = 0, len = array.length; i < len; ++i)
            arr[i] = array[i];
        return arr;
    }

    public static float[] unwrap(Float[] array) {
        float[] arr = new float[array.length];
        for (int i = 0, len = array.length; i < len; ++i)
            arr[i] = array[i];
        return arr;
    }

    public static double[] unwrap(Double[] array) {
        double[] arr = new double[array.length];
        for (int i = 0, len = array.length; i < len; ++i)
            arr[i] = array[i];
        return arr;
    }

    public static char[] unwrap(Character[] array) {
        char[] arr = new char[array.length];
        for (int i = 0, len = array.length; i < len; ++i)
            arr[i] = array[i];
        return arr;
    }

    public static boolean[] unwrap(Boolean[] array) {
        boolean[] arr = new boolean[array.length];
        for (int i = 0, len = array.length; i < len; ++i)
            arr[i] = array[i];
        return arr;
    }
}
