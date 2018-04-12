package rocks.voss.beatthemeat.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;

import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.R;
import rocks.voss.beatthemeat.enums.HistoryScaleEnum;
import rocks.voss.beatthemeat.ui.AbstractTemperatureCanvas;
import rocks.voss.beatthemeat.ui.CurrentTemperatureCanvas;
import rocks.voss.beatthemeat.ui.HistoryTemperatureCanvas;

public class UiUtil {
    public static float getStandardPaddingPixel(Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Constants.STANDARD_PADDING, context.getResources().getDisplayMetrics());
    }

    public static float getStandardPaddingPixel(Context context, int dip) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }

    public static void setupTemperatureCanvas(Context context, AbstractTemperatureCanvas canvas) {
        int[] attrs = {android.R.attr.colorBackground, R.attr.colorThermometerRed, R.attr.colorThermometerYellow, R.attr.colorThermometerGreen, R.attr.colorThermometerText, R.attr.colorThermometerTextAlarm, R.attr.colorThermometerIndicator, R.attr.colorThermometerSeparator};
        TypedArray ta = context.obtainStyledAttributes(R.style.AppTheme, attrs);

        Paint paintBackground = new Paint();
        paintBackground.setColor(ta.getColor(0, Color.BLACK));
        canvas.setColorBackground(paintBackground);

        Paint paintRed = new Paint();
        paintRed.setColor(ta.getColor(1, Color.RED));
        paintRed.setStrokeWidth(5f);
        canvas.setColorRed(paintRed);

        Paint paintYellow = new Paint();
        paintYellow.setColor(ta.getColor(2, Color.YELLOW));
        paintYellow.setStrokeWidth(5f);
        canvas.setColorYellow(paintYellow);

        Paint paintGreen = new Paint();
        paintGreen.setColor(ta.getColor(3, Color.GREEN));
        paintGreen.setStrokeWidth(5f);
        canvas.setColorGreen(paintGreen);

        Paint paintText = new Paint();
        paintText.setColor(ta.getColor(4, Color.WHITE));
        canvas.setColorText(paintText);

        Paint paintTextAlarm = new Paint();
        paintTextAlarm.setColor(ta.getColor(5, Color.RED));
        canvas.setColorTextAlarm(paintTextAlarm);

        Paint paintIndicator = new Paint();
        paintIndicator.setColor(ta.getColor(6, Color.WHITE));
        canvas.setColorIndicator(paintIndicator);

        Paint paintSeparator = new Paint();
        paintSeparator.setColor(ta.getColor(7, Color.DKGRAY));
        canvas.setColorSeparator(paintSeparator);

        if ( canvas instanceof HistoryTemperatureCanvas ) {
            ((HistoryTemperatureCanvas) canvas).setScale(HistoryScaleEnum.hrs3);
        }

        ta.recycle();
    }
}
