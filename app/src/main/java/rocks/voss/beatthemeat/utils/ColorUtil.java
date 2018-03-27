package rocks.voss.beatthemeat.utils;

import android.graphics.Color;

/**
 * Created by voss on 14.03.18.
 */

public class ColorUtil {
    public static int getRed(int color) {
        int value = color & 0xFF0000;
        value >>= 16;
        return value;
    }

    public static int getGreen(int color) {
        int value = color & 0xFF00;
        value >>= 8;
        return value;
    }

    public static int getBlue(int color) {
        int value = color & 0xFF;
        return value;
    }

    public static int getCloserColor(int colorStart, int colorDest, int steps, int step) {
        int redStart = getRed(colorStart);
        int redDest = getRed(colorDest);
        int greenStart = getGreen(colorStart);
        int greenDest = getGreen(colorDest);
        int blueStart = getBlue(colorStart);
        int blueDest = getBlue(colorDest);

        int redNew = ((redDest - redStart) / steps) * step + redStart;
        int greenNew = ((greenDest - greenStart) / steps) * step + greenStart;
        int blueNew = ((blueDest - blueStart) / steps) * step + blueStart;

        return Color.parseColor(getCombinedColor(redNew, greenNew, blueNew));
    }

    private static String getCombinedColor(int r, int g, int b ) {
        String value = "#";
        value += getHexValue(r);
        value += getHexValue(g);
        value += getHexValue(b);
        return value;
    }

    private static String getHexValue(int value) {
        String hex = Integer.toHexString(value);
        if ( hex.length() == 1 ) {
            hex = "0" + hex;
        }
        return hex;
    }
}
