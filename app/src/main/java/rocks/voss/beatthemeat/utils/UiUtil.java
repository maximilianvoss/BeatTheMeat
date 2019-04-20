package rocks.voss.beatthemeat.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;

import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.R;
import rocks.voss.beatthemeat.enums.HistoryScaleEnum;
import rocks.voss.beatthemeat.ui.AbstractTemperatureCanvas;
import rocks.voss.beatthemeat.ui.HistoryTemperatureCanvas;

public class UiUtil {

    private static final float STROKE_WIDTH = 5f;

    public static float getStandardPaddingPixel(Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Constants.STANDARD_PADDING, context.getResources().getDisplayMetrics());
    }

    public static float getStandardPaddingPixel(Context context, int dip) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }

    public static void setupTemperatureCanvas(Context context, AbstractTemperatureCanvas canvas) {
        int[] attrs = {
                android.R.attr.colorBackground,
                R.attr.colorThermometerRed,
                R.attr.colorThermometerYellow,
                R.attr.colorThermometerGreen,
                R.attr.colorThermometerLightRed,
                R.attr.colorThermometerLightYellow,
                R.attr.colorThermometerLightGreen,
                R.attr.colorThermometerText,
                R.attr.colorThermometerTextAlarm,
                R.attr.colorThermometerIndicator,
                R.attr.colorThermometerSeparator
        };
        TypedArray ta = context.obtainStyledAttributes(R.style.AppTheme, attrs);
        int index = 0;

        Paint paintBackground = new Paint();
        paintBackground.setColor(ta.getColor(index++, Color.BLACK));
        canvas.setColorBackground(paintBackground);

        Paint paintRed = new Paint();
        paintRed.setColor(ta.getColor(index++, Color.RED));
        paintRed.setStrokeWidth(STROKE_WIDTH);
        canvas.setColorRed(paintRed);

        Paint paintYellow = new Paint();
        paintYellow.setColor(ta.getColor(index++, Color.YELLOW));
        paintYellow.setStrokeWidth(STROKE_WIDTH);
        canvas.setColorYellow(paintYellow);

        Paint paintGreen = new Paint();
        paintGreen.setColor(ta.getColor(index++, Color.GREEN));
        paintGreen.setStrokeWidth(STROKE_WIDTH);
        canvas.setColorGreen(paintGreen);

        Paint paintLightRed = new Paint();
        paintLightRed.setColor(ta.getColor(index++, Color.RED));
        paintLightRed.setStrokeWidth(STROKE_WIDTH);
        canvas.setColorLightRed(paintLightRed);

        Paint paintLightYellow = new Paint();
        paintLightYellow.setColor(ta.getColor(index++, Color.YELLOW));
        paintLightYellow.setStrokeWidth(STROKE_WIDTH);
        canvas.setColorLightYellow(paintLightYellow);

        Paint paintLightGreen = new Paint();
        paintLightGreen.setColor(ta.getColor(index++, Color.GREEN));
        paintLightGreen.setStrokeWidth(STROKE_WIDTH);
        canvas.setColorLightGreen(paintLightGreen);

        Paint paintText = new Paint();
        paintText.setColor(ta.getColor(index++, Color.WHITE));
        canvas.setColorText(paintText);

        Paint paintTextAlarm = new Paint();
        paintTextAlarm.setColor(ta.getColor(index++, Color.RED));
        canvas.setColorTextAlarm(paintTextAlarm);

        Paint paintIndicator = new Paint();
        paintIndicator.setColor(ta.getColor(index++, Color.WHITE));
        canvas.setColorIndicator(paintIndicator);

        Paint paintSeparator = new Paint();
        paintSeparator.setColor(ta.getColor(index++, Color.DKGRAY));
        canvas.setColorSeparator(paintSeparator);

        if (canvas instanceof HistoryTemperatureCanvas) {
            ((HistoryTemperatureCanvas) canvas).setScale(HistoryScaleEnum.min15);
        }

        ta.recycle();
    }
}
