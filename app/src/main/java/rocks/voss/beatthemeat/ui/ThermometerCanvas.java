package rocks.voss.beatthemeat.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;

import lombok.Setter;
import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.activities.ThermometerSettingsActivity;
import rocks.voss.beatthemeat.utils.KeyUtil;
import rocks.voss.beatthemeat.utils.TemperatureUtil;
import rocks.voss.beatthemeat.utils.UiUtil;


/**
 * Created by voss on 11.03.18.
 */
public class ThermometerCanvas extends SurfaceView {
    private static final int TEMPERATURE_THRESHOLD = 20;
    private static final int DEGREES_YELLOW = 5;

    @Setter
    private int id;
    @Setter
    private Paint colorBackground;
    @Setter
    private Paint colorText;
    @Setter
    private Paint colorTextAlarm;
    @Setter
    private Paint colorRed;
    @Setter
    private Paint colorYellow;
    @Setter
    private Paint colorGreen;
    @Setter
    private Paint colorIndicator;
    @Setter
    private Paint colorSeparator;
    @Setter
    private boolean displayTemperature = true;

    private int temperatureCurrent;
    private int temperatureMin;
    private int temperatureMax;

    private float paddingPixel;
    private float maxWidth;
    private float maxHeight;
    private float squareSize;

    private Paint colorBlack;


    public ThermometerCanvas(Context context, int id) {
        this(context);
        this.id = id;
    }

    public ThermometerCanvas(Context context) {
        this(context, null);
    }

    public ThermometerCanvas(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThermometerCanvas(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setWillNotDraw(false);

        colorBlack = new Paint();
        colorBlack.setColor(Color.BLACK);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ThermometerSettingsActivity.class);
                intent.putExtra(Constants.THERMOMETER_CANVAS_ID, id);
                v.getContext().startActivity(intent);
            }
        });

        this.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        boolean isRange = sharedPref.getBoolean(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_IS_RANGE, this.id), true);
        temperatureCurrent = sharedPref.getInt(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_CURRENT, this.id), -9999);
        temperatureMin = sharedPref.getInt(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_MIN, this.id), 50);
        temperatureMax = sharedPref.getInt(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_MAX, this.id), 100);

        paddingPixel = UiUtil.getStandardPaddingPixel(getContext());
        maxWidth = canvas.getWidth() - paddingPixel;
        maxHeight = canvas.getHeight() - paddingPixel;
        if (maxWidth / 2 < maxHeight) {
            squareSize = maxWidth / 2;
        } else {
            squareSize = maxHeight;
        }

        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), colorBackground);
        canvas.drawArc(paddingPixel, paddingPixel, 2 * squareSize, 2 * squareSize, 180f, 180f, true, colorBlack);

        int displayTemperatureMin = calcDisplayTemperatureMin();
        int displayTemperatureMax = calcDisplayTemperatureMax(isRange);
        float anglePerC = 180f / ((float) displayTemperatureMax - (float) displayTemperatureMin);

        if (isRange) {
            drawRange(canvas, displayTemperatureMin, anglePerC);
        } else {
            drawTarget(canvas, displayTemperatureMax, anglePerC);
        }

        drawIndicator(canvas, displayTemperatureMax, anglePerC);

        if ( displayTemperature) {
            if (temperatureCurrent == -9999) {
                drawTemperature(canvas, "N/A");
            } else {
                drawTemperature(canvas, String.valueOf(temperatureCurrent));
            }
        }
    }

    private void drawTarget(Canvas canvas, int displayTemperatureMax, float anglePerC) {
        float angleGreen = -anglePerC * (displayTemperatureMax - temperatureMin);
        float angleYellow = -anglePerC * DEGREES_YELLOW;
        float angleRed = -180f - angleGreen - angleYellow;

        drawTemperatureArc(canvas, 0f, angleGreen, colorGreen);
        drawTemperatureArc(canvas, angleGreen, angleYellow, colorYellow);
        drawTemperatureArc(canvas, angleGreen + angleYellow, angleRed, colorRed);

        drawTemperatureLine(canvas, colorSeparator, squareSize, -angleGreen);
        drawTemperatureLine(canvas, colorSeparator, squareSize, -angleGreen - angleYellow);
    }

    private void drawRange(Canvas canvas, int displayTemperatureMin, float anglePerC) {
        float angleRed1 = -anglePerC * (temperatureMin - displayTemperatureMin - DEGREES_YELLOW);
        float angleYellow = -anglePerC * DEGREES_YELLOW;
        float angleGreen = -anglePerC * (temperatureMax - temperatureMin);
        float angleRed2 = -180f - angleRed1 - 2 * angleYellow - angleGreen;

        drawTemperatureArc(canvas, 0f, angleRed2, colorRed);
        drawTemperatureArc(canvas, angleRed2, angleYellow, colorYellow);
        drawTemperatureArc(canvas, angleRed2 + angleYellow, angleGreen, colorGreen);
        drawTemperatureArc(canvas, angleRed2 + angleYellow + angleGreen, angleYellow, colorYellow);
        drawTemperatureArc(canvas, angleRed2 + angleYellow + angleGreen + angleYellow, angleRed1, colorRed);

        drawTemperatureLine(canvas, colorSeparator, squareSize, -angleRed2);
        drawTemperatureLine(canvas, colorSeparator, squareSize, -angleRed2 - angleYellow);
        drawTemperatureLine(canvas, colorSeparator, squareSize, -angleRed2 - angleYellow - angleGreen);
        drawTemperatureLine(canvas, colorSeparator, squareSize, -angleRed2 - angleYellow - angleGreen - angleYellow);
    }

    private void drawIndicator(Canvas canvas, int displayTemperatureMax, float anglePerC) {
        float currentTemp = anglePerC * (displayTemperatureMax - temperatureCurrent);
        drawTemperatureLine(canvas, colorIndicator, squareSize, currentTemp);
    }

    private void drawTemperatureArc(Canvas canvas, float startAngle, float sweepAngle, Paint paint) {
        canvas.drawArc(paddingPixel + 2f, paddingPixel + 2f, 2 * squareSize - 4f, 2 * squareSize - 4f, startAngle, sweepAngle, true, paint);
    }

    private void drawTemperatureLine(Canvas canvas, Paint paint, float length, float angle) {
        double x = Math.cos(Math.toRadians(angle)) * length;
        double y = Math.sin(Math.toRadians(angle)) * length;

        canvas.drawLine(squareSize + paddingPixel / 2, squareSize + paddingPixel / 2, squareSize + paddingPixel / 2 + (float) x, squareSize + paddingPixel / 2 - (float) y, paint);
    }

    private void drawTemperature(Canvas canvas, String temperature) {
        Paint color;
        if (TemperatureUtil.isAlarm(getContext(), id)) {
            color = colorTextAlarm;
        } else {
            color = colorText;
        }

        color.setTextSize(canvas.getHeight() - 2 * paddingPixel);
        color.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(temperature, maxWidth, maxHeight, color);
    }

    private int calcDisplayTemperatureMin() {
        int displayTemperatureMin;
        if (temperatureCurrent > temperatureMin) {
            displayTemperatureMin = temperatureMin - TEMPERATURE_THRESHOLD;
        } else {
            displayTemperatureMin = temperatureCurrent - TEMPERATURE_THRESHOLD;
        }
        if (displayTemperatureMin < 0) {
            return 0;
        }
        return displayTemperatureMin;
    }

    private int calcDisplayTemperatureMax(boolean isRange) {
        if (isRange) {
            if (temperatureCurrent > temperatureMax) {
                return temperatureCurrent + TEMPERATURE_THRESHOLD;
            }
            return temperatureMax + TEMPERATURE_THRESHOLD;
        } else {
            if (temperatureCurrent < temperatureMin) {
                return temperatureMin + TEMPERATURE_THRESHOLD;
            }
            return temperatureCurrent + TEMPERATURE_THRESHOLD;
        }
    }
}
