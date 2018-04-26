package rocks.voss.beatthemeat.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import rocks.voss.beatthemeat.database.Temperature;
import rocks.voss.beatthemeat.enums.HistoryScaleEnum;
import rocks.voss.beatthemeat.services.HistoryTemperatureService;

public class HistoryTemperatureCanvas extends AbstractTemperatureCanvas {

    private GestureDetector gestureDetector;

    @Setter
    private List<Temperature> temperatures;

    @Setter
    @Getter
    private HistoryScaleEnum scale;

    private float pixelPerC = 0f;
    private float pixelPerTemp = 0f;
    private int displayTemperatureMin = 0;
    private int displayTemperatureMax = 0;

    private final static float radius = 15f;
    private final static int cellPerCs = 5;

    public HistoryTemperatureCanvas(Context context, int id) {
        this(context);
        this.id = id;
    }

    public HistoryTemperatureCanvas(Context context) {
        this(context, null);
    }

    public HistoryTemperatureCanvas(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HistoryTemperatureCanvas(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float direction = e1.getX() - e2.getX();
                if (direction > 0) {
                    scale = scale.next();
                } else {
                    scale = scale.prev();
                }
                HistoryTemperatureService.execute();
                return true;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    protected void doOnDraw(Canvas canvas) {
        if (temperatures == null || temperatures.size() == 0) {
            return;
        }
        maxHeight -= 50;

        calcDisplayMinMax();

        OffsetDateTime currentTime = OffsetDateTime.now();
        OffsetDateTime historyTime = HistoryScaleEnum.getTime(scale);
        long timeFrame;
        if (HistoryScaleEnum.isCalcInSeconds(scale)) {
            timeFrame = ChronoUnit.SECONDS.between(historyTime, currentTime);
        } else {
            timeFrame = ChronoUnit.MINUTES.between(historyTime, currentTime);
        }

        pixelPerC = (maxHeight - paddingPixel) / (displayTemperatureMax - displayTemperatureMin);
        pixelPerTemp = (maxWidth - paddingPixel) / timeFrame;

        drawIndicator(canvas);
        drawRange(canvas);
        drawRaster(canvas);
        drawPath(canvas);
    }

    private void calcDisplayMinMax() {
        displayTemperatureMin = temperatures.get(0).temperature;
        displayTemperatureMax = temperatures.get(0).temperature;
        for (Temperature temperature : temperatures) {
            if (temperature.temperature > displayTemperatureMax) {
                displayTemperatureMax = temperature.temperature;
            }
            if (temperature.temperature < displayTemperatureMin) {
                displayTemperatureMin = temperature.temperature;
            }
        }
        displayTemperatureMax = displayTemperatureMax + TEMPERATURE_THRESHOLD + (10 - displayTemperatureMax % 10);
        displayTemperatureMin = displayTemperatureMin - TEMPERATURE_THRESHOLD + (10 - displayTemperatureMin % 10);
        if (displayTemperatureMin < 0) {
            displayTemperatureMin = 0;
        }
    }

    private void drawPath(Canvas canvas) {
        OffsetDateTime historyTime = HistoryScaleEnum.getTime(scale);

        Path path = new Path();
        float x = paddingPixel;
        float y = maxHeight - pixelPerC * (temperatures.get(0).temperature - displayTemperatureMin);
        path.moveTo(x, y);
        for (int i = 0; i < temperatures.size(); i++) {
            Temperature temperature = temperatures.get(i);

            long timeFrameHistory;
            if (HistoryScaleEnum.isCalcInSeconds(scale)) {
                timeFrameHistory = ChronoUnit.SECONDS.between(historyTime, temperature.time);
            } else {
                timeFrameHistory = ChronoUnit.MINUTES.between(historyTime, temperature.time);
            }
            if (timeFrameHistory >= 0) {
                x = paddingPixel + pixelPerTemp * timeFrameHistory;
                y = maxHeight - pixelPerC * (temperature.temperature - displayTemperatureMin);
                path.lineTo(x, y);
            }
        }

        colorIndicator.setStrokeWidth(10f);
        colorIndicator.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, colorIndicator);
    }

    private void drawScaleName(Canvas canvas) {
        colorText.setTextAlign(Paint.Align.RIGHT);
        colorText.setTextSize(3 * radius);
        canvas.drawText(HistoryScaleEnum.getScaleName(scale), maxWidth, maxHeight + paddingPixel + radius, colorText);
    }

    private void drawRangeBar(Canvas canvas, float start, float end, Paint color) {
        if (start < paddingPixel || start > maxHeight && end > maxHeight) {
            return;
        }
        if (start > maxHeight) {
            start = maxHeight;
        }
        if (end < paddingPixel) {
            end = paddingPixel;
        }
        if (end > maxHeight) {
            end = maxHeight;
        }
        canvas.drawRect(paddingPixel, start, maxWidth, end, color);
    }

    private void drawRange(Canvas canvas) {
        float start = maxHeight;
        float end = maxHeight - (temperatureMin - displayTemperatureMin - DEGREES_YELLOW) * pixelPerC;
        drawRangeBar(canvas, start, end, colorLightRed);

        start = end;
        end = maxHeight - (temperatureMin - displayTemperatureMin) * pixelPerC;
        drawRangeBar(canvas, start, end, colorLightYellow);

        if (isRange) {
            start = end;
            end = maxHeight - (temperatureMax - displayTemperatureMin) * pixelPerC;
            drawRangeBar(canvas, start, end, colorLightGreen);

            start = end;
            end = maxHeight - (temperatureMax - displayTemperatureMin + DEGREES_YELLOW) * pixelPerC;
            drawRangeBar(canvas, start, end, colorLightYellow);

            start = end;
            end = paddingPixel;
            drawRangeBar(canvas, start, end, colorLightRed);
        } else {
            start = end;
            end = paddingPixel;
            drawRangeBar(canvas, start, end, colorLightGreen);
        }
    }

    private void drawRaster(Canvas canvas) {
        colorSeparator.setStyle(Paint.Style.STROKE);

        canvas.drawRect(paddingPixel, paddingPixel, maxWidth, maxHeight, colorSeparator);
        int count;
        switch (scale) {
            case min15:
            case hrs5:
            case hrs10:
                count = 5;
                break;
            case min30:
            case hrs1:
            case hrs3:
                count = 6;
                break;
            default:
                count = 0;
        }

        float cellSizeVertical = (maxWidth - paddingPixel) / count;
        for (int i = 0; i < count; i++) {
            canvas.drawLine(paddingPixel + cellSizeVertical * i, paddingPixel, paddingPixel + cellSizeVertical * i, maxHeight, colorSeparator);
        }

        int temperatureDiff = displayTemperatureMax - displayTemperatureMin;
        for (int i = 0; i < temperatureDiff / cellPerCs; i++) {
            canvas.drawLine(paddingPixel, paddingPixel + i * pixelPerC * cellPerCs, maxWidth, paddingPixel + i * pixelPerC * cellPerCs, colorSeparator);
        }
    }

    private void drawIndicator(Canvas canvas) {
        int count = HistoryScaleEnum.values().length;

        float size = radius * 4;
        float start = (getWidth() - size * (count - 1)) / 2;
        for (int i = 0; i < count; i++) {
            if (scale.ordinal() == i) {
                colorText.setStyle(Paint.Style.FILL_AND_STROKE);
            } else {
                colorText.setStyle(Paint.Style.STROKE);
            }
            canvas.drawCircle(start + size * i, maxHeight + paddingPixel, radius, colorText);
        }
        drawScaleName(canvas);
    }
}
