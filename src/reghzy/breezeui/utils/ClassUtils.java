package reghzy.breezeui.utils;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.jetbrains.annotations.Contract;

import java.util.HashMap;

public class ClassUtils {
    private static final HashMap<Class<?>, Class<?>> PRIMITIVE_TO_BOXED;
    private static final HashMap<Class<?>, Class<?>> BOXED_TO_PRIMITIVE;
    private static final HashMap<Class<?>, Object> BOXED_TO_DEFAULT_VAL;

    static {
        PRIMITIVE_TO_BOXED = new HashMap<Class<?>, Class<?>>();
        PRIMITIVE_TO_BOXED.put(Byte.TYPE, Byte.class);
        PRIMITIVE_TO_BOXED.put(Short.TYPE, Short.class);
        PRIMITIVE_TO_BOXED.put(Integer.TYPE, Integer.class);
        PRIMITIVE_TO_BOXED.put(Long.TYPE, Long.class);
        PRIMITIVE_TO_BOXED.put(Float.TYPE, Float.class);
        PRIMITIVE_TO_BOXED.put(Double.TYPE, Double.class);
        PRIMITIVE_TO_BOXED.put(Boolean.TYPE, Boolean.class);
        PRIMITIVE_TO_BOXED.put(Character.TYPE, Character.class);
        PRIMITIVE_TO_BOXED.put(Void.TYPE, Void.class);

        BOXED_TO_PRIMITIVE = new HashMap<Class<?>, Class<?>>();
        BOXED_TO_PRIMITIVE.put(Byte.class, Byte.TYPE);
        BOXED_TO_PRIMITIVE.put(Short.class, Short.TYPE);
        BOXED_TO_PRIMITIVE.put(Integer.class, Integer.TYPE);
        BOXED_TO_PRIMITIVE.put(Long.class, Long.TYPE);
        BOXED_TO_PRIMITIVE.put(Float.class, Float.TYPE);
        BOXED_TO_PRIMITIVE.put(Double.class, Double.TYPE);
        BOXED_TO_PRIMITIVE.put(Boolean.class, Boolean.TYPE);
        BOXED_TO_PRIMITIVE.put(Character.class, Character.TYPE);
        BOXED_TO_PRIMITIVE.put(Void.class, Void.TYPE);

        BOXED_TO_DEFAULT_VAL = new HashMap<Class<?>, Object>();
        BOXED_TO_DEFAULT_VAL.put(Byte.TYPE, (byte) 0);
        BOXED_TO_DEFAULT_VAL.put(Short.TYPE, (short) 0);
        BOXED_TO_DEFAULT_VAL.put(Integer.TYPE, 0);
        BOXED_TO_DEFAULT_VAL.put(Long.TYPE, 0L);
        BOXED_TO_DEFAULT_VAL.put(Float.TYPE, 0F);
        BOXED_TO_DEFAULT_VAL.put(Double.TYPE, 0D);
        BOXED_TO_DEFAULT_VAL.put(Boolean.TYPE, false);
        BOXED_TO_DEFAULT_VAL.put(Character.TYPE, '\0');
        BOXED_TO_DEFAULT_VAL.put(Void.TYPE, null);
    }

    /**
     * Converts primitive types into their boxed types (for example, {@link Integer#TYPE} into {@link Integer}))
     * @param clazz The target class
     * @return The boxed type, or clazz if it isn't a primitive type
     */
    @Contract("null->null")
    public static Class<?> box(Class<?> clazz) {
        Class<?> boxed = PRIMITIVE_TO_BOXED.get(clazz);
        return boxed == null ? clazz : boxed;
    }

    public static Class<?> unbox(Class<?> clazz) {
        Class<?> boxed = BOXED_TO_PRIMITIVE.get(clazz);
        return boxed == null ? clazz : boxed;
    }

    public static Object getDefaultValue(Class<?> clazz) {
        return BOXED_TO_DEFAULT_VAL.get(box(clazz));
    }

    public static Object convertPrimitiveTo(Object value, Class<?> target) {
        if (value == null) {
            return null;
        }

        Class<?> clazz = unbox(target);
        if (clazz == null || !clazz.isPrimitive()) {
            return value;
        }

        if (value instanceof Number) {
            Number number = (Number) value;
            if (target == Byte.TYPE) return number.byteValue();
            if (target == Short.TYPE) return number.shortValue();
            if (target == Integer.TYPE) return number.intValue();
            if (target == Long.TYPE) return number.longValue();
            if (target == Float.TYPE) return number.floatValue();
            if (target == Double.TYPE) return number.doubleValue();
            if (target == Boolean.TYPE) return number.intValue() != 0;
            if (target == Character.TYPE) return (char) number.intValue();
            return value;
        }
        else if (value instanceof Boolean) {
            int val = (Boolean) value ? 1 : 0;
            if (target == Byte.TYPE) return (byte) val;
            if (target == Short.TYPE) return (short) val;
            if (target == Integer.TYPE) return val;
            if (target == Long.TYPE) return (long)val;
            if (target == Float.TYPE) return (float)val;
            if (target == Double.TYPE) return (double)val;
            if (target == Boolean.TYPE) return value;
            if (target == Character.TYPE) return (char) val;
        }

        return clazz.cast(value);
    }
}
