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
import rocks.voss.beatthemeat.activities.MainActivity;
import rocks.voss.beatthemeat.activities.ThermometerSettingActivity;
import rocks.voss.beatthemeat.utils.ColorUtil;
import rocks.voss.beatthemeat.utils.KeyUtil;


/**
 * Created by voss on 11.03.18.
 */
public class ThermometerCanvas extends SurfaceView {
    @Setter
    private Paint colorBackground;

    @Setter
    private Paint colorRed;

    @Setter
    private Paint colorYellow;

    @Setter
    private Paint colorGreen;

    @Setter
    private int id = 0;

    public ThermometerCanvas(Context context) {
        this(context, null);
    }

    public ThermometerCanvas(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThermometerCanvas(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setWillNotDraw(false);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ThermometerSettingActivity.class);
                intent.putExtra("rocks.voss.beatthemeat.widgets.ThermometerCanvas.id", id);
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

        this.id = MainActivity.getThermometers().indexOf(this);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        boolean isRange = sharedPref.getBoolean(KeyUtil.createKey("isRange", id), true);
        int temperatureCurrent = sharedPref.getInt(KeyUtil.createKey("temperatureCurrent", id), -9999);
        int temperatureMin = sharedPref.getInt(KeyUtil.createKey("temperatureMin", id), 50);
        int temperatureMax = sharedPref.getInt(KeyUtil.createKey("temperatureMax", id), 100);

        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), colorBackground);

        Paint colorBlack = new Paint();
        colorBlack.setColor(Color.BLACK);

        canvas.drawArc(20f, 20f, 505f, 510f, 180f, 180f, true, colorBlack);
        if (isRange) {
            drawRange(canvas);
        } else {
            drawTarget(canvas);
        }
        if ( temperatureCurrent != -9999 ) {
            drawTemperature(canvas, String.valueOf(temperatureCurrent));
        } else {
            drawTemperature(canvas, " N/A");
        }

//        drawIndicator(canvas);
    }

    private void drawTarget(Canvas canvas) {
        Paint colorBlack = new Paint();
        colorBlack.setColor(Color.BLACK);

        double x = 275 + 260 * Math.asin(300f);
        double y = 275 + 260 * Math.acos(300f);

        canvas.drawLine(275f, 275f, (float) x, (float) y, colorBlack);

        drawTriangle(canvas, 180f, 60f, colorRed, colorRed, colorYellow);
        drawTriangle(canvas, 240f, 60f, colorYellow, colorYellow, colorGreen);
        drawTriangle(canvas, 300f, 60f, colorGreen, colorGreen, colorGreen);
    }

    private void drawRange(Canvas canvas) {
        drawTriangle(canvas, 180f, 36f, colorRed, colorRed, colorYellow);
        drawTriangle(canvas, 216f, 36f, colorYellow, colorYellow, colorGreen);
        drawTriangle(canvas, 252f, 36f, colorGreen, colorGreen, colorGreen);
        drawTriangle(canvas, 288f, 36f, colorGreen, colorYellow, colorYellow);
        drawTriangle(canvas, 324f, 36f, colorYellow, colorRed, colorRed);
    }

    private void drawIndicator(Canvas canvas) {
        Paint colorBlack = new Paint();
        colorBlack.setColor(Color.BLACK);
        colorBlack.setStrokeWidth(10f);
        canvas.drawLine(262.5f, 262.5f, 250, 25, colorBlack);
    }

    private void drawTriangle(Canvas canvas, float startAngle, float sweepAngle, Paint fadeColorLeft, Paint color, Paint fadeColorRight) {
        canvas.drawArc(25f, 25f, 500f, 500f, startAngle, sweepAngle, true, color);
        float sweepAnglePartial = sweepAngle / 3;

        for (int i = 0; i < (int) sweepAnglePartial; i++) {
            Paint colorTmp = new Paint();
            colorTmp.setColor(ColorUtil.getCloserColor(fadeColorLeft.getColor(), color.getColor(), (int) sweepAnglePartial, i));
            canvas.drawArc(25f, 25f, 500f, 500f, startAngle + i, 2f, true, colorTmp);
        }

        for (int i = 0; i < (int) sweepAnglePartial; i++) {
            Paint colorTmp = new Paint();
            colorTmp.setColor(ColorUtil.getCloserColor(color.getColor(), fadeColorRight.getColor(), (int) sweepAnglePartial, i));
            canvas.drawArc(25f, 25f, 500f, 500f, startAngle + 2 * sweepAnglePartial + i - 1, 2f, true, colorTmp);
        }
    }

    private void drawTemperature(Canvas canvas, String temperature) {
        while (temperature.length() < 3) {
            temperature = " " + temperature;
        }
        Paint colorBlack = new Paint();
        colorBlack.setColor(Color.BLACK);
        colorBlack.setTextSize(200);
        canvas.drawText(temperature, 530, 250, colorBlack);
    }

}