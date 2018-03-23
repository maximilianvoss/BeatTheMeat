package rocks.voss.beatthemeat.widgets;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;

import rocks.voss.beatthemeat.R;
import rocks.voss.beatthemeat.data.ThermometerSettings;
import rocks.voss.beatthemeat.utils.ColorUtil;


/**
 * Created by voss on 11.03.18.
 */
public class ThermometerCanvas extends SurfaceView {
    private Paint colorBackground = new Paint();
    private Paint colorRed = new Paint();
    private Paint colorYellow = new Paint();
    private Paint colorGreen = new Paint();
    private ThermometerSettings thermometerSettings = new ThermometerSettings();
    private Intent intent;


    public ThermometerCanvas(Context context) {
        this(context, null);
    }

    public ThermometerCanvas(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThermometerCanvas(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(v.getContext(), ThermometerSettingActivity.class);
                intent.putExtra("rocks.voss.beatthemeat.thermometerSettings", thermometerSettings);
                v.getContext().startActivity(intent);
            }
        });

        this.setWillNotDraw(false);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ThermometerCanvas, 0, 0);
            colorBackground.setColor(a.getInt(R.styleable.ThermometerCanvas_colorBackground, 0xFFFFFF00));
            colorRed.setColor(a.getInt(R.styleable.ThermometerCanvas_colorThermometerRed, 0xFF000000));
            colorYellow.setColor(a.getInt(R.styleable.ThermometerCanvas_colorThermometerYellow, 0xFFFF0000));
            colorGreen.setColor(a.getInt(R.styleable.ThermometerCanvas_colorThermometerGreen, 0x00FF0000));
            a.recycle();
        }
    }

    @Override
    public void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if ( intent != null ) {
            thermometerSettings = (ThermometerSettings) intent.getParcelableExtra("rocks.voss.beatthemeat.thermometerSettings");
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), colorBackground);

        Paint colorBlack = new Paint();
        colorBlack.setColor(Color.BLACK);

        canvas.drawArc(20f, 20f, 505f, 510f, 180f, 180f, true, colorBlack);
        drawRange(canvas);

        drawTemperature(canvas, String.valueOf(thermometerSettings.getTemp1()));
        drawIndicator(canvas);
    }


    private void drawRange(Canvas canvas) {
        Paint colorBlack = new Paint();
        colorBlack.setColor(Color.BLACK);

        colorBlack.setStrokeWidth(10f);
        canvas.drawLine(262.5f, 262.5f, 262.5f, 10, colorBlack);

        canvas.drawArc(25f, 25f, 500f, 500f, 180f, 180f, true, colorRed);

        int steps = 36;
        int degrees = 60;

        drawTransition(canvas, steps, degrees, colorYellow, colorRed, 180f, 180f);
        drawTransition(canvas, steps, degrees, colorGreen, colorYellow, 210f, 120f);
    }

    private void drawTransition(Canvas canvas, int steps, int degrees, Paint colorDest, Paint colorSrc, float startAngle, float sweepAngle) {
        for (int i = 0; i < steps; i++) {
            Paint colorTmp = new Paint();
            colorTmp.setColor(ColorUtil.getCloserColor(colorDest.getColor(), colorSrc.getColor(), steps, i));
            canvas.drawArc(25f, 25f, 500f, 500f, startAngle + (degrees * i) / (steps * 2), sweepAngle - (degrees * i) / steps, true, colorTmp);
        }
    }

    private void drawIndicator(Canvas canvas) {
        Paint colorBlack = new Paint();
        colorBlack.setColor(Color.BLACK);
        colorBlack.setStrokeWidth(10f);
        canvas.drawLine(262.5f, 262.5f, 250, 25, colorBlack);
    }

    private void drawTemperature(Canvas canvas, String temp) {
        while (temp.length() < 3) {
            temp = " " + temp;
        }
        Paint colorBlack = new Paint();
        colorBlack.setColor(Color.BLACK);
        colorBlack.setTextSize(200);
        canvas.drawText(temp, 530, 250, colorBlack);
    }

}
