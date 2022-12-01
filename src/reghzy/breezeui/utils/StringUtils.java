package reghzy.breezeui.utils;

public class StringUtils {
    /**
     * Returns the value before the first occurrence of the given character. If the character is never found, the original value is returned
     * <p>
     * <code>splitLeft("hi.there.lol", '.') == "hi"</code>
     * </p>
     */
    public static String splitLeft(String value, char character) {
        return splitLeft(value, character, value);
    }

    /**
     * Returns the value before the first occurrence of the given character. If the character is never found, then def is returned
     * <p>
     * <code>splitLeft("hi.there.lol", '.') == "hi"</code>
     * </p>
     */
    public static String splitLeft(String value, char character, String def) {
        int index = value.indexOf(character);
        return index == -1 ? def : value.substring(0, index);
    }

    /**
     * Returns the value after the first occurrence of the given character. If the character is never found, the original value is returned
     * <p>
     * <code>splitRight("hi.there.lol", '.') == "there.lol"</code>
     * </p>
     */
    public static String splitRight(String value, char character) {
        return splitRight(value, character, value);
    }

    /**
     * Returns the value after the first occurrence of the given character. If the character is never found, then def is returned
     * <p>
     * <code>splitRight("hi.there.lol", '.') == "there.lol"</code>
     * </p>
     */
    public static String splitRight(String value, char character, String def) {
        int index = value.indexOf(character);
        return index == -1 ? def : value.substring(index + 1);
    }

    /**
     * Returns the value before the last occurrence of the given character. If the character is never found, the original value is returned
     * <p>
     * <code>splitLastLeft("hi.there.lol", '.') == "hi.there"</code>
     * </p>
     * <p>
     * <code>splitLastLeft("hello lol", '.') == "hello lol"</code>
     * </p>
     */
    public static String splitLastLeft(String value, char character) {
        return splitLastLeft(value, character, value);
    }

    /**
     * Returns the value before the last occurrence of the given character. If the character is never found, then def is returned
     * <p>
     * <code>splitLastLeft("hi.there.lol", '.') == "hi.there"</code>
     * </p>
     * <p>
     * <code>splitLastLeft("hello lol", '.') == "hello lol"</code>
     * </p>
     */
    public static String splitLastLeft(String value, char character, String def) {
        int index = value.lastIndexOf(character);
        return index == -1 ? def : value.substring(0, index);
    }

    /**
     * Returns the value after the last occurrence of the given character. If the character is never found, the original value is returned
     * <p>
     * <code>splitLastRight("hi.there.lol", '.') == "lol"</code>
     * </p>
     * <p>
     * <code>splitLastRight("hello lol", '.') == "hello lol"</code>
     * </p>
     */
    public static String splitLastRight(String value, char character) {
        return splitLastRight(value, character, value);
    }

    /**
     * Returns the value after the last occurrence of the given character. If the character is never found, then def is returned
     * <p>
     * <code>splitLastRight("hi.there.lol", '.') == "lol"</code>
     * </p>
     * <p>
     * <code>splitLastRight("hello lol", '.') == "hello lol"</code>
     * </p>
     */
    public static String splitLastRight(String value, char character, String def) {
        int index = value.lastIndexOf(character);
        return index == -1 ? def : value.substring(index + 1);
    }
}
