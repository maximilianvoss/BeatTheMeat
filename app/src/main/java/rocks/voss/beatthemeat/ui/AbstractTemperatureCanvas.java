package rocks.voss.beatthemeat.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.SurfaceView;

import lombok.Setter;
import rocks.voss.beatthemeat.database.probe.Thermometer;
import rocks.voss.beatthemeat.database.temperatures.Temperature;
import rocks.voss.beatthemeat.database.temperatures.TemperatureCache;
import rocks.voss.beatthemeat.utils.UiUtil;

public abstract class AbstractTemperatureCanvas extends SurfaceView {
    protected static final int TEMPERATURE_THRESHOLD = 20;
    protected static final int DEGREES_YELLOW = 5;

    //    @Setter
//    @Getter
//    protected int id;
    @Setter
    protected Paint colorBackground;
    @Setter
    protected Paint colorText;
    @Setter
    protected Paint colorTextAlarm;
    @Setter
    protected Paint colorRed;
    @Setter
    protected Paint colorLightRed;
    @Setter
    protected Paint colorYellow;
    @Setter
    protected Paint colorLightYellow;
    @Setter
    protected Paint colorGreen;
    @Setter
    protected Paint colorLightGreen;
    @Setter
    protected Paint colorIndicator;
    @Setter
    protected Paint colorSeparator;

    protected Thermometer thermometer;
    protected Temperature temperature;

    protected float paddingPixel;
    protected float maxWidth;
    protected float maxHeight;

    protected final Paint colorBlack;

    public AbstractTemperatureCanvas(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setWillNotDraw(false);
        colorBlack = new Paint();
        colorBlack.setColor(Color.BLACK);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getContext());

        temperature = TemperatureCache.getLatestTemperature(thermometer.id);
        if (temperature == null) {
            temperature = new Temperature();
            temperature.isActive = false;
            temperature.temperature = 0;
            temperature.thermometerId = thermometer.id;
        }

        paddingPixel = UiUtil.getStandardPaddingPixel(getContext());
        maxWidth = canvas.getWidth() - paddingPixel;
        maxHeight = canvas.getHeight() - paddingPixel;

        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), colorBackground);

        doOnDraw(canvas);
    }


    protected int calcDisplayTemperatureMin() {
        int displayTemperatureMin;
        if (temperature.temperature > thermometer.temperatureMin) {
            displayTemperatureMin = thermometer.temperatureMin - TEMPERATURE_THRESHOLD;
        } else {
            displayTemperatureMin = temperature.temperature - TEMPERATURE_THRESHOLD;
        }
        if (displayTemperatureMin < 0) {
            return 0;
        }

        if (displayTemperatureMin % 10 > 5) {
            displayTemperatureMin = displayTemperatureMin + 10 - displayTemperatureMin % 10;
        } else {
            displayTemperatureMin = displayTemperatureMin - displayTemperatureMin % 10;
        }

        return displayTemperatureMin;
    }

    protected int calcDisplayTemperatureMax() {
        int displayTemperatureMax;
        if (thermometer.isRange) {
            if (temperature.temperature > thermometer.temperatureMax) {
                displayTemperatureMax = temperature.temperature + TEMPERATURE_THRESHOLD;
            } else {
                displayTemperatureMax = thermometer.temperatureMax + TEMPERATURE_THRESHOLD;
            }
        } else {
            if (temperature.temperature < thermometer.temperatureMin) {
                displayTemperatureMax = thermometer.temperatureMin + TEMPERATURE_THRESHOLD;
            } else {
                displayTemperatureMax = temperature.temperature + TEMPERATURE_THRESHOLD;
            }
        }
        if (displayTemperatureMax % 10 > 5) {
            displayTemperatureMax = displayTemperatureMax + 10 - displayTemperatureMax % 10;
        } else {
            displayTemperatureMax = displayTemperatureMax - displayTemperatureMax % 10;
        }

        return displayTemperatureMax;
    }

    protected abstract void doOnDraw(Canvas canvas);
}
