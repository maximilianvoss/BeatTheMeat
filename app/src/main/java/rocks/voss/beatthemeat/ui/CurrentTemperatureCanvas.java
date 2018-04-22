package rocks.voss.beatthemeat.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.activities.ThermometerDisplayActivity;
import rocks.voss.beatthemeat.activities.ThermometerSettingsActivity;
import rocks.voss.beatthemeat.utils.TemperatureUtil;
import rocks.voss.beatthemeat.utils.UiUtil;


/**
 * Created by voss on 11.03.18.
 */
public class CurrentTemperatureCanvas extends AbstractTemperatureCanvas {

    private float squareSize;
    private final static int indicatorSize = 7;

    public CurrentTemperatureCanvas(Context context, int id) {
        this(context);
        this.id = id;
    }

    public CurrentTemperatureCanvas(Context context) {
        this(context, null);
    }

    public CurrentTemperatureCanvas(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CurrentTemperatureCanvas(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

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
                Intent intent = new Intent(v.getContext(), ThermometerDisplayActivity.class);
                intent.putExtra(Constants.THERMOMETER_CANVAS_ID, id);
                v.getContext().startActivity(intent);
                return true;
            }
        });
    }

    @Override
    protected void doOnDraw(Canvas canvas) {
        colorIndicator.setStrokeWidth(15f);
        colorSeparator.setStrokeWidth(2f);

        if (maxWidth / 2 < maxHeight) {
            squareSize = maxWidth / 2;
        } else {
            squareSize = maxHeight;
        }

        canvas.drawArc(paddingPixel, paddingPixel, 2 * squareSize, 2 * squareSize, 180f, 180f, true, colorBlack);
        if (isRange) {
            drawRange(canvas);
        } else {
            drawTarget(canvas);
        }

        drawMarkers(canvas);
        drawIndicator(canvas);

        if (temperatureCurrent == -9999) {
            drawTemperature(canvas, "N/A");
        } else {
            drawTemperature(canvas, String.valueOf(temperatureCurrent));
        }
    }

    private float getAnglePerTemperature(float temperature) {
        int displayTemperatureMin = calcDisplayTemperatureMin();
        int displayTemperatureMax = calcDisplayTemperatureMax();
        if ( temperature < displayTemperatureMin ) {
            return 0f;
        }
        if ( temperature > displayTemperatureMax ) {
            return 180f;
        }
        float anglePerC = 180f / (displayTemperatureMax - displayTemperatureMin);
        return anglePerC * (temperature - displayTemperatureMin);
    }

    private void drawMarkers(Canvas canvas) {
        if ( isRange ) {
            for ( int i = calcDisplayTemperatureMin() + 5; i < calcDisplayTemperatureMax(); i+=5 ) {
                Paint color;
                if ( i <= temperatureMin - DEGREES_YELLOW ) {
                    color = colorRed;
                } else if ( i <= temperatureMin ) {
                    color = colorYellow;
                } else if ( i <= temperatureMax ) {
                    color = colorGreen;
                } else if ( i <= temperatureMax + DEGREES_YELLOW ) {
                    color = colorYellow;
                } else {
                    color = colorRed;
                }
                float angle = 180f - getAnglePerTemperature(i);
                drawTemperatureIndicator(canvas, color, squareSize - paddingPixel / 2, angle, true);
            }
        } else {
            for (int i = calcDisplayTemperatureMin() + 5; i < calcDisplayTemperatureMax(); i += 5) {
                Paint color;
                if (i <= temperatureMin) {
                    color = colorGreen;
                } else if (i <= temperatureMin + DEGREES_YELLOW) {
                    color = colorYellow;
                } else {
                    color = colorRed;
                }
                float angle = 180f - getAnglePerTemperature(i);
                drawTemperatureIndicator(canvas, color, squareSize - paddingPixel / 2, angle, true);
            }
        }
    }

    private void drawTarget(Canvas canvas) {
        float angleGreen = getAnglePerTemperature(temperatureMin);
        float angleYellow = getAnglePerTemperature(temperatureMin + DEGREES_YELLOW);
        float angleRed = 180f;

        drawTemperatureArc(canvas, 180f, angleGreen, colorLightGreen);
        drawTemperatureArc(canvas, 180f + angleGreen, angleYellow - angleGreen, colorLightYellow);
        drawTemperatureArc(canvas, 180f + angleYellow, angleRed - angleYellow, colorLightRed);

        drawTemperatureLine(canvas, colorSeparator, squareSize - paddingPixel / 2, 180f - angleGreen);
        drawTemperatureLine(canvas, colorSeparator, squareSize - paddingPixel / 2, 180f - angleYellow);
    }

    private void drawRange(Canvas canvas) {
        float angleRed1 = getAnglePerTemperature(temperatureMin - DEGREES_YELLOW);
        float angleYellow1 = getAnglePerTemperature(temperatureMin);
        float angleGreen = getAnglePerTemperature(temperatureMax);
        float angleYellow2 = getAnglePerTemperature(temperatureMax + DEGREES_YELLOW);
        float angleRed2 = 180f;

        drawTemperatureArc(canvas, 180f, angleRed1, colorLightRed);
        drawTemperatureArc(canvas, 180f + angleRed1, angleYellow1 - angleRed1, colorLightYellow);
        drawTemperatureArc(canvas, 180f + angleYellow1, angleGreen - angleYellow1, colorLightGreen);
        drawTemperatureArc(canvas, 180f + angleGreen, angleYellow2 - angleGreen, colorLightYellow);
        drawTemperatureArc(canvas, 180f + angleYellow2, angleRed2 - angleYellow2, colorLightRed);

        drawTemperatureLine(canvas, colorSeparator, squareSize - paddingPixel / 2, 180f - angleRed1);
        drawTemperatureLine(canvas, colorSeparator, squareSize - paddingPixel / 2, 180f - angleYellow1);
        drawTemperatureLine(canvas, colorSeparator, squareSize - paddingPixel / 2, 180f - angleGreen);
        drawTemperatureLine(canvas, colorSeparator, squareSize - paddingPixel / 2, 180f - angleYellow2);
    }

    private void drawIndicator(Canvas canvas) {
        float angleCurrentTemp = 180f - getAnglePerTemperature(temperatureCurrent);
        drawTemperatureLine(canvas, colorIndicator, squareSize, angleCurrentTemp);
    }

    private void drawTemperatureArc(Canvas canvas, float startAngle, float sweepAngle, Paint paint) {
        canvas.drawArc(paddingPixel + 2f, paddingPixel + 2f, 2 * squareSize - 4f, 2 * squareSize - 4f, startAngle, sweepAngle, true, paint);
    }

    private void drawTemperatureLine(Canvas canvas, Paint paint, float length, float angle) {
        drawTemperatureIndicator(canvas, paint, length, angle, false);
    }

    private void drawTemperatureIndicator(Canvas canvas, Paint paint, float length, float angle, boolean isIndicator) {
        double xStart = squareSize + paddingPixel / 2;
        double yStart = squareSize + paddingPixel / 2;
        double yEnd = squareSize + paddingPixel / 2 - Math.sin(Math.toRadians(angle)) * length;
        double xEnd = squareSize + paddingPixel / 2 + Math.cos(Math.toRadians(angle)) * length;

        if (isIndicator) {
            xStart += Math.cos(Math.toRadians(angle)) * (length - UiUtil.getStandardPaddingPixel(getContext(), indicatorSize));
            yStart -= Math.sin(Math.toRadians(angle)) * (length - UiUtil.getStandardPaddingPixel(getContext(), indicatorSize));
        }
        canvas.drawLine((float) xStart, (float) yStart, (float) xEnd, (float) yEnd, paint);
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
}
