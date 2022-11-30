package reghzy.breezeui.utils;

public class Validate {
    public static void notNull(Object value, String errorMessage) {
        if (value == null) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
