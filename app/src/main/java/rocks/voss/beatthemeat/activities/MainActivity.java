package rocks.voss.beatthemeat.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.R;
import rocks.voss.beatthemeat.database.TemperatureDatabase;
import rocks.voss.beatthemeat.services.NotificationSoundService;
import rocks.voss.beatthemeat.services.TemperatureCollectionService;
import rocks.voss.beatthemeat.ui.ThermometerCanvas;
import rocks.voss.beatthemeat.utils.KeyUtil;
import rocks.voss.beatthemeat.utils.TemperatureUtil;

public class MainActivity extends AppCompatActivity {

    private static final String NUMBER_OF_THERMOMETERS = "numberOfThermometers";
    @Getter
    private static final List<ThermometerCanvas> thermometers = new ArrayList<>();

    @Setter
    @Getter
    private static TemperatureDatabase temperatureDatabase;

    @Getter
    private static Switch switchAlarm;

    private Context context;
    private LinearLayout linearLayout;
    private static SharedPreferences sharedPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        TemperatureUtil.setEnabled(true);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.activity_main);
        ScrollView scrollView = findViewById(R.id.scrollview);
        linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(linearLayout);

        thermometers.clear();
        fillLinearLayout();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThermometerCanvas thermometerCanvas = new ThermometerCanvas(context, thermometers.size());
                thermometerCanvas.setLayoutParams(new ViewGroup.LayoutParams(-1, 300));
                setupThermometerCanvas(thermometerCanvas);
                linearLayout.addView(thermometerCanvas);
                thermometers.add(thermometerCanvas);
                linearLayout.postInvalidate();

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(NUMBER_OF_THERMOMETERS, thermometers.size());
                editor.apply();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.settings:
                intent = new Intent(this, AppSettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.remove:
                MainActivity.removeThermometer();
                onResume();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        linearLayout.removeAllViews();

        Intent notificationSoundServiceIntent = new Intent(context, NotificationSoundService.class);
        context.stopService(notificationSoundServiceIntent);

        thermometers.clear();
        fillLinearLayout();
    }

    @Override
    public void onDestroy() {
        TemperatureCollectionService.schedule(this);
        super.onDestroy();
    }

    public static void removeThermometer() {
        if (thermometers.size() > 0) {
            int id = thermometers.size() - 1;
            SharedPreferences.Editor editor = sharedPref.edit();
            thermometers.remove(id);
            editor.putInt(NUMBER_OF_THERMOMETERS, thermometers.size());
            editor.remove(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_IS_RANGE, id));
            editor.remove(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_MIN, id));
            editor.remove(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_MAX, id));
            editor.apply();

        }
    }

    public static void refreshThermometers() {
        for (ThermometerCanvas thermometerCanvas : thermometers) {
            thermometerCanvas.postInvalidate();
        }
    }

    @SuppressLint("ResourceType")
    private void setupThermometerCanvas(ThermometerCanvas thermometerCanvas) {
        int[] attrs = {android.R.attr.colorBackground, R.attr.colorThermometerRed, R.attr.colorThermometerYellow, R.attr.colorThermometerGreen, R.attr.colorThermometerText, R.attr.colorThermometerTextAlarm, R.attr.colorThermometerIndicator, R.attr.colorThermometerSeparator};
        TypedArray ta = obtainStyledAttributes(R.style.AppTheme, attrs);

        Paint paintBackground = new Paint();
        paintBackground.setColor(ta.getColor(0, Color.BLACK));
        thermometerCanvas.setColorBackground(paintBackground);

        Paint paintRed = new Paint();
        paintRed.setColor(ta.getColor(1, Color.RED));
        thermometerCanvas.setColorRed(paintRed);

        Paint paintYellow = new Paint();
        paintYellow.setColor(ta.getColor(2, Color.YELLOW));
        thermometerCanvas.setColorYellow(paintYellow);

        Paint paintGreen = new Paint();
        paintGreen.setColor(ta.getColor(3, Color.GREEN));
        thermometerCanvas.setColorGreen(paintGreen);

        Paint paintText = new Paint();
        paintText.setColor(ta.getColor(4, Color.WHITE));
        thermometerCanvas.setColorText(paintText);

        Paint paintTextAlarm = new Paint();
        paintTextAlarm.setColor(ta.getColor(5, Color.RED));
        thermometerCanvas.setColorTextAlarm(paintTextAlarm);

        Paint paintIndicator = new Paint();
        paintIndicator.setColor(ta.getColor(6, Color.WHITE));
        paintIndicator.setStrokeWidth(15f);
        thermometerCanvas.setColorIndicator(paintIndicator);

        Paint paintSeparator = new Paint();
        paintSeparator.setColor(ta.getColor(7, Color.DKGRAY));
        paintSeparator.setStrokeWidth(2f);
        thermometerCanvas.setColorSeparator(paintSeparator);

        ta.recycle();
    }


    private void fillLinearLayout() {
        switchAlarm = new Switch(context);
        switchAlarm.setText(R.string.setting_general_alarm_enable);
        switchAlarm.setLayoutParams(new ViewGroup.LayoutParams(-1, 150));
        switchAlarm.setChecked(TemperatureUtil.isEnabled());
        switchAlarm.setPadding(25, 0, 25, 0);
        switchAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                TemperatureUtil.setEnabled(b);
                if (!b) {
                    Intent notificationSoundServiceIntent = new Intent(context, NotificationSoundService.class);
                    context.stopService(notificationSoundServiceIntent);
                }
            }
        });
        linearLayout.addView(switchAlarm);

        int numberOfThermometers = sharedPref.getInt(NUMBER_OF_THERMOMETERS, 0);
        for (int i = 0; i < numberOfThermometers; i++) {
            ThermometerCanvas thermometerCanvas = new ThermometerCanvas(context, i);
            thermometerCanvas.setLayoutParams(new ViewGroup.LayoutParams(-1, 300));
            setupThermometerCanvas(thermometerCanvas);
            linearLayout.addView(thermometerCanvas);
            thermometers.add(thermometerCanvas);
            linearLayout.postInvalidate();
        }
    }
}
