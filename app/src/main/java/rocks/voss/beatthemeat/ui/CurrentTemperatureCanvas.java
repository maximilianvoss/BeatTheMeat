package rocks.voss.beatthemeat.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.activities.HistoryActivity;
import rocks.voss.beatthemeat.activities.ThermometerSettingsActivity;
import rocks.voss.beatthemeat.utils.TemperatureUtil;
import rocks.voss.beatthemeat.utils.UiUtil;


/**
 * Created by voss on 11.03.18.
 */
public class CurrentTemperatureCanvas extends AbstractTemperatureCanvas {

    private float squareSize;
    private final static int temperatureIndicatorSize = 7;
    private final static int indicatorWidth = 7;

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
                Intent intent = new Intent(v.getContext(), HistoryActivity.class);
                intent.putExtra(Constants.THERMOMETER_CANVAS_ID, id);
                v.getContext().startActivity(intent);
                return true;
            }
        });
    }

    @Override
    protected void doOnDraw(Canvas canvas) {
        colorSeparator.setStrokeWidth(2f);

        if (maxWidth / 2 < maxHeight) {
            squareSize = maxWidth / 2;
        } else {
            squareSize = maxHeight;
        }

        if (isRange) {
            drawRange(canvas);
        } else {
            drawTarget(canvas);
        }
        drawMarkers(canvas);

        if (temperatureCurrent == Constants.FALLBACK_VALUE_TEMPERATURE_NOT_SET) {
            drawTemperature(canvas, "N/A");
        } else {
            drawTemperature(canvas, String.valueOf(temperatureCurrent));
        }

        colorBlack.setStyle(Paint.Style.STROKE);
        canvas.drawArc(paddingPixel, paddingPixel, 2 * squareSize, 2 * squareSize, 180f, 180f, false, colorBlack);

        drawIndicator(canvas);
    }

    private float getAnglePerTemperature(float temperature) {
        int displayTemperatureMin = calcDisplayTemperatureMin();
        int displayTemperatureMax = calcDisplayTemperatureMax();
        if (temperature < displayTemperatureMin) {
            return 0f;
        }
        if (temperature > displayTemperatureMax) {
            return 180f;
        }
        float anglePerC = 180f / (displayTemperatureMax - displayTemperatureMin);
        return anglePerC * (temperature - displayTemperatureMin);
    }

    private void drawMarkers(Canvas canvas) {
        if (isRange) {
            for (int i = calcDisplayTemperatureMin() + 5; i < calcDisplayTemperatureMax(); i += 5) {
                Paint color;
                if (i <= temperatureMin - DEGREES_YELLOW) {
                    color = colorRed;
                } else if (i <= temperatureMin) {
                    color = colorYellow;
                } else if (i <= temperatureMax) {
                    color = colorGreen;
                } else if (i <= temperatureMax + DEGREES_YELLOW) {
                    color = colorYellow;
                } else {
                    color = colorRed;
                }
                float angle = 180f - getAnglePerTemperature(i);
                drawTemperatureLine(canvas, color, squareSize - paddingPixel / 2, angle, true);
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
                drawTemperatureLine(canvas, color, squareSize - paddingPixel / 2, angle, true);
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

    private void drawTemperatureArc(Canvas canvas, float startAngle, float sweepAngle, Paint paint) {
        canvas.drawArc(paddingPixel + 2f, paddingPixel + 2f, 2 * squareSize - 4f, 2 * squareSize - 4f, startAngle, sweepAngle, true, paint);
    }

    private void drawTemperatureLine(Canvas canvas, Paint paint, float length, float angle) {
        drawTemperatureLine(canvas, paint, length, angle, false);
    }

    private void drawTemperatureLine(Canvas canvas, Paint paint, float length, float angle, boolean isIndicator) {
        Point start = new Point();
        start.x = (int) (squareSize + paddingPixel / 2);
        start.y = (int) (squareSize + paddingPixel / 2);

        Point end = new Point();
        end.x = (int) (squareSize + paddingPixel / 2 + Math.cos(Math.toRadians(angle)) * length);
        end.y = (int) (squareSize + paddingPixel / 2 - Math.sin(Math.toRadians(angle)) * length);

        if (isIndicator) {
            start.x += Math.cos(Math.toRadians(angle)) * (length - UiUtil.getStandardPaddingPixel(getContext(), temperatureIndicatorSize));
            start.y -= Math.sin(Math.toRadians(angle)) * (length - UiUtil.getStandardPaddingPixel(getContext(), temperatureIndicatorSize));
        }
        canvas.drawLine(start.x, start.y, end.x, end.y, paint);
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

    private void drawIndicator(Canvas canvas) {
        float angle = 180f - getAnglePerTemperature(temperatureCurrent);
        float length = UiUtil.getStandardPaddingPixel(getContext(), indicatorWidth);

        Point end = new Point();
        end.x = (int) (squareSize + paddingPixel / 2 + Math.cos(Math.toRadians(angle)) * (squareSize - paddingPixel / 2));
        end.y = (int) (squareSize + paddingPixel / 2 - Math.sin(Math.toRadians(angle)) * (squareSize - paddingPixel / 2));

        Point center = new Point();
        center.x = (int) (squareSize + paddingPixel / 2);
        center.y = (int) (squareSize + paddingPixel / 2 - length);

        Point rear = new Point();
        rear.x = (int) (center.x - Math.cos(Math.toRadians(angle)) * length);
        rear.y = (int) (center.y + Math.sin(Math.toRadians(angle)) * length);

        Point mid1 = new Point();
        mid1.x = (int) (center.x + Math.cos(Math.toRadians(angle + 90)) * length);
        mid1.y = (int) (center.y - Math.sin(Math.toRadians(angle + 90)) * length);

        Point mid2 = new Point();
        mid2.x = (int) (center.x + Math.cos(Math.toRadians(angle - 90)) * length);
        mid2.y = (int) (center.y - Math.sin(Math.toRadians(angle - 90)) * length);

        Path path = new Path();
        path.moveTo(rear.x, rear.y);
        path.lineTo(mid1.x, mid1.y);
        path.lineTo(end.x, end.y);
        path.lineTo(mid2.x, mid2.y);
        path.lineTo(rear.x, rear.y);

        canvas.drawPath(path, colorIndicator);
        colorBlack.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawCircle(center.x, center.y, length / 3, colorBlack);
    }
}
