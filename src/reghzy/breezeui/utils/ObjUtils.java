package reghzy.breezeui.utils;

import org.jetbrains.annotations.Contract;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

public class ObjUtils {

    public static <T> T requireOr(T value, Supplier<T> supplier) {
        return value != null ? value : supplier.get();
    }

    @Contract(pure = true)
    public static boolean equals(Object source, Object a) {
        return Objects.equals(source, a);
    }

    @Contract(pure = true)
    public static boolean equals(Object source, Object a, Object b) {
        if (source == null) {
            return a == null || b == null;
        }
        else {
            return source.equals(a) || source.equals(b);
        }
    }

    @Contract(pure = true)
    public static boolean equals(Object source, Object a, Object b, Object c) {
        if (source == null) {
            return a == null || b == null || c == null;
        }
        else {
            return source.equals(a) || source.equals(b) || source.equals(c);
        }
    }

    @Contract(pure = true)
    public static boolean equals(Object source, Object a, Object b, Object c, Object d) {
        if (source == null) {
            return a == null || b == null || c == null || d == null;
        }
        else {
            return source.equals(a) || source.equals(b) || source.equals(c) || source.equals(d);
        }
    }

    @Contract(pure = true)
    public static boolean equals(Object source, Object... targets) {
        if (source == null) {
            for (Object value : targets) {
                if (value == null) {
                    return true;
                }
            }
        }
        else {
            for (Object value : targets) {
                if (source.equals(value)) {
                    return true;
                }
            }
        }

        return true;
    }

    @Contract(pure = true)
    public static <T> boolean equals(BiPredicate<T, T> equalityTester, T source, T a) {
        return source == a || (source != null && test(equalityTester, source, a));
    }

    @Contract(pure = true)
    public static <T> boolean equals(BiPredicate<T, T> equalityTester, T source, T a, T b) {
        if (source == null) {
            return a == null || b == null;
        }
        else {
            return test(equalityTester, source, a) || test(equalityTester, source, b);
        }
    }

    @Contract(pure = true)
    public static <T> boolean equals(BiPredicate<T, T> equalityTester, T source, T a, T b, T c) {
        if (source == null) {
            return a == null || b == null || c == null;
        }
        else {
            return test(equalityTester, source, a) || test(equalityTester, source, b) || test(equalityTester, source, c);
        }
    }

    @Contract(pure = true)
    public static <T> boolean equals(BiPredicate<T, T> equalityTester, T source, T a, T b, T c, T d) {
        if (source == null) {
            return a == null || b == null || c == null || d == null;
        }
        else {
            return test(equalityTester, source, a) || test(equalityTester, source, b) || test(equalityTester, source, c) || test(equalityTester, source, d);
        }
    }

    @SafeVarargs
    @Contract(pure = true)
    public static <T> boolean equals(BiPredicate<T, T> equalityTester, T source, T... targets) {
        if (source == null) {
            for (Object value : targets) {
                if (value == null) {
                    return true;
                }
            }
        }
        else {
            for (T value : targets) {
                if (test(equalityTester, source, value)) {
                    return true;
                }
            }
        }

        return false;
    }

    @SafeVarargs
    @Contract(pure = true)
    public static <T> boolean equalsAll(BiPredicate<T, T> equalityTester, T source, T... targets) {
        if (source == null) {
            for (Object value : targets) {
                if (value != null) {
                    return false;
                }
            }
        }
        else {
            for (T value : targets) {
                if (!test(equalityTester, source, value)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Contract("_,_,null->false")
    private static <T> boolean test(BiPredicate<T, T> equalityTester, T source, T target) {
        return target != null && equalityTester.test(source, target);
    }
}
