package rocks.voss.beatthemeat.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.SurfaceView;

import lombok.Getter;
import lombok.Setter;
import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.utils.KeyUtil;
import rocks.voss.beatthemeat.utils.UiUtil;

public abstract class AbstractTemperatureCanvas extends SurfaceView {
    protected static final int TEMPERATURE_THRESHOLD = 20;
    protected static final int DEGREES_YELLOW = 5;

    @Setter
    @Getter
    protected int id;
    @Setter
    protected Paint colorBackground;
    @Setter
    protected Paint colorText;
    @Setter
    protected Paint colorTextAlarm;
    @Setter
    protected Paint colorRed;
    @Setter
    protected Paint colorYellow;
    @Setter
    protected Paint colorGreen;
    @Setter
    protected Paint colorIndicator;
    @Setter
    protected Paint colorSeparator;

    protected boolean isRange;
    protected int temperatureCurrent;
    protected int temperatureMin;
    protected int temperatureMax;

    protected float paddingPixel;
    protected float maxWidth;
    protected float maxHeight;

    protected Paint colorBlack;

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
        isRange = sharedPref.getBoolean(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_IS_RANGE, this.id), true);
        temperatureCurrent = sharedPref.getInt(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_CURRENT, this.id), -9999);
        temperatureMin = sharedPref.getInt(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_MIN, this.id), 50);
        temperatureMax = sharedPref.getInt(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_MAX, this.id), 100);

        paddingPixel = UiUtil.getStandardPaddingPixel(getContext());
        maxWidth = canvas.getWidth() - paddingPixel;
        maxHeight = canvas.getHeight() - paddingPixel;

        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), colorBackground);

        doOnDraw(canvas);
    }

    protected abstract void doOnDraw(Canvas canvas);
}
