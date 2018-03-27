package rocks.voss.beatthemeat.utils;

/**
 * Created by voss on 26.03.18.
 */

public class KeyUtil {
    public static String createKey(String key, int id) {
        return key + "_" + String.valueOf(id);
    }
}
