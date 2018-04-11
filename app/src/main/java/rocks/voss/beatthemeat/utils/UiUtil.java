package rocks.voss.beatthemeat.utils;

import android.content.Context;
import android.util.TypedValue;

import rocks.voss.beatthemeat.Constants;

public class UiUtil {
    public static float getStandardPaddingPixel(Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Constants.STANDARD_PADDING, context.getResources().getDisplayMetrics());
    }

    public static float getStandardPaddingPixel(Context context, int dip) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }
}
