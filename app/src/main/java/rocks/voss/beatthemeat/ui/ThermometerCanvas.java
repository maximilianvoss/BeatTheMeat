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

    private boolean isRange;
    private int temperatureCurrent;
    private int temperatureMin;
    private int temperatureMax;

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
    }


    protected void reDraw() {
        this.invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        isRange = sharedPref.getBoolean(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_IS_RANGE, this.id), true);
        temperatureCurrent = sharedPref.getInt(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_CURRENT, this.id), -9999);
        temperatureMin = sharedPref.getInt(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_MIN, this.id), 50);
        temperatureMax = sharedPref.getInt(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_MAX, this.id), 100);

        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), colorBackground);
        canvas.drawArc(20f, 20f, 510f, 510f, 180f, 180f, true, colorBlack);
        if (isRange) {
            drawRange(canvas);
        } else {
            drawTarget(canvas);
        }
        if (temperatureCurrent == -9999) {
            drawTemperature(canvas, " N/A");
        } else {
            drawTemperature(canvas, String.valueOf(temperatureCurrent));
        }
    }

    private void drawTarget(Canvas canvas) {
        int displayTemperatureMin;
        int displayTemperatureMax;
        if (temperatureCurrent < temperatureMin) {
            displayTemperatureMin = temperatureCurrent - TEMPERATURE_THRESHOLD;
            displayTemperatureMax = temperatureMin + TEMPERATURE_THRESHOLD;
        } else {
            displayTemperatureMin = temperatureMin - TEMPERATURE_THRESHOLD;
            displayTemperatureMax = temperatureCurrent + TEMPERATURE_THRESHOLD;
        }
        float anglePerC = 180f / ((float) displayTemperatureMax - (float) displayTemperatureMin);

        float angleGreen = -anglePerC * (displayTemperatureMax - temperatureMin);
        float angleYellow = -anglePerC * DEGREES_YELLOW;
        float angleRed = -180f - angleGreen - angleYellow;

        drawTemperatureArc(canvas, 0f, angleGreen, colorGreen);
        drawTemperatureArc(canvas, angleGreen, angleYellow, colorYellow);
        drawTemperatureArc(canvas, angleGreen + angleYellow, angleRed, colorRed);

        drawTemperatureLine(canvas, colorSeparator, 240f, -angleGreen);
        drawTemperatureLine(canvas, colorSeparator, 240f, -angleGreen - angleYellow);

        float currentTemp = anglePerC * (displayTemperatureMax - temperatureCurrent);
        drawTemperatureLine(canvas, colorIndicator, 250f, currentTemp);
    }

    private void drawRange(Canvas canvas) {
        int displayTemperatureMin;
        int displayTemperatureMax;

        if (temperatureCurrent < temperatureMin) {
            displayTemperatureMin = temperatureCurrent - TEMPERATURE_THRESHOLD;
        } else {
            displayTemperatureMin = temperatureMin - TEMPERATURE_THRESHOLD;
        }
        if (temperatureCurrent > temperatureMax) {
            displayTemperatureMax = temperatureCurrent + TEMPERATURE_THRESHOLD;
        } else {
            displayTemperatureMax = temperatureMax + TEMPERATURE_THRESHOLD;
        }

        float anglePerC = 180f / ((float) displayTemperatureMax - (float) displayTemperatureMin);

        float angleRed1 = -anglePerC * (temperatureMin - displayTemperatureMin - DEGREES_YELLOW);
        float angleYellow = -anglePerC * DEGREES_YELLOW;
        float angleGreen = -anglePerC * (temperatureMax - temperatureMin);
        float angleRed2 = -180f - angleRed1 - 2 * angleYellow - angleGreen;

        drawTemperatureArc(canvas, 0f, angleRed2, colorRed);
        drawTemperatureArc(canvas, angleRed2, angleYellow, colorYellow);
        drawTemperatureArc(canvas, angleRed2 + angleYellow, angleGreen, colorGreen);
        drawTemperatureArc(canvas, angleRed2 + angleYellow + angleGreen, angleYellow, colorYellow);
        drawTemperatureArc(canvas, angleRed2 + angleYellow + angleGreen + angleYellow, angleRed1, colorRed);

        drawTemperatureLine(canvas, colorSeparator, 240f, -angleRed2);
        drawTemperatureLine(canvas, colorSeparator, 240f, -angleRed2 - angleYellow);
        drawTemperatureLine(canvas, colorSeparator, 240f, -angleRed2 - angleYellow - angleGreen);
        drawTemperatureLine(canvas, colorSeparator, 240f, -angleRed2 - angleYellow - angleGreen - angleYellow);

        float currentTemp = anglePerC * (displayTemperatureMax - temperatureCurrent);
        drawTemperatureLine(canvas, colorIndicator, 250f, currentTemp);
    }

    private void drawTemperatureArc(Canvas canvas, float startAngle, float sweepAngle, Paint paint) {
        canvas.drawArc(25f, 25f, 500f, 500f, startAngle, sweepAngle, true, paint);
    }

    private void drawTemperatureLine(Canvas canvas, Paint paint, float length, float angle) {
        double x = Math.cos(Math.toRadians(angle)) * length;
        double y = Math.sin(Math.toRadians(angle)) * length;

        canvas.drawLine(263f, 263f, 263f + (float) x, 263f - (float) y, paint);
    }

    private void drawTemperature(Canvas canvas, String temperature) {
        while (temperature.length() < 3) {
            temperature = " " + temperature;
        }
        Paint color;
        if (TemperatureUtil.isAlarm(getContext(), id)) {
            color = colorTextAlarm;
        } else {
            color = colorText;
        }

        color.setTextSize(200);
        color.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(temperature, canvas.getWidth() - 25, 225, color);
    }

}
