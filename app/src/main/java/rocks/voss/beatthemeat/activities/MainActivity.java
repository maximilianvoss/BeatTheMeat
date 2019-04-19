package rocks.voss.beatthemeat.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

import rocks.voss.beatthemeat.R;
import rocks.voss.beatthemeat.database.probe.Thermometer;
import rocks.voss.beatthemeat.database.probe.ThermometerCache;
import rocks.voss.beatthemeat.services.HistoryTemperatureService;
import rocks.voss.beatthemeat.services.NotificationSoundService;
import rocks.voss.beatthemeat.services.TemperatureCollectionService;
import rocks.voss.beatthemeat.services.ThermometerSettingsCollectionService;
import rocks.voss.beatthemeat.ui.CurrentTemperatureCanvas;
import rocks.voss.beatthemeat.utils.AlarmUtil;
import rocks.voss.beatthemeat.utils.NotificationUtil;
import rocks.voss.beatthemeat.utils.UiUtil;

public class MainActivity extends AppCompatActivity {
    private static final List<CurrentTemperatureCanvas> thermometerCanvas = new ArrayList<>();

    private Context context;
    private static SharedPreferences sharedPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        AlarmUtil.setEnabled(true);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("  " + getSupportActionBar().getTitle());

        createUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
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
            case R.id.about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.add:
                addThermometer();
                return true;
            case R.id.remove:
                removeThermometer();
                return true;
            case R.id.quit:
                HistoryTemperatureService.cancelJob(this);
                TemperatureCollectionService.cancelJob(this);
                ThermometerSettingsCollectionService.cancelJob(this);
                this.finishAndRemoveTask();
                this.finishAffinity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NotificationUtil.isNotificationActive()) {
            AlarmUtil.setEnabled(false);
            NotificationUtil.stopNotification(context);
        }
        createUI();
    }

    public static void refreshThermometers() {
        for (CurrentTemperatureCanvas currentTemperatureCanvas : thermometerCanvas) {
            currentTemperatureCanvas.postInvalidate();
        }
    }

    private void addThermometer() {
        Thermometer thermometer = new Thermometer();
        thermometer.id = ThermometerCache.getThermometers().size();
        ThermometerCache.insertThermometer(thermometer);
        createUI();
    }

    private void removeThermometer() {
        List<Thermometer> thermometers = ThermometerCache.getThermometers();
        if (thermometers.size() > 0) {
            ThermometerCache.delete(thermometers.get(thermometers.size() - 1));
            createUI();
        }
    }

    private void createUI() {
        float paddingPixel = UiUtil.getStandardPaddingPixel(this);

        ScrollView scrollView = findViewById(R.id.scrollview);
        scrollView.removeAllViews();

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(linearLayout);

        Switch switchAlarm = new Switch(context);
        switchAlarm.setText(R.string.setting_general_alarm_enable);
        switchAlarm.setLayoutParams(new ViewGroup.LayoutParams(-1, 150));
        switchAlarm.setChecked(AlarmUtil.isEnabled());
        switchAlarm.setPadding((int) paddingPixel, (int) paddingPixel, (int) paddingPixel, 0);
        switchAlarm.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                    AlarmUtil.setEnabled(isChecked);
                    if (!isChecked) {
                        Intent notificationSoundServiceIntent = new Intent(context, NotificationSoundService.class);
                        context.stopService(notificationSoundServiceIntent);
                    }
                }
        );
        linearLayout.addView(switchAlarm);

        for (Thermometer thermometer : ThermometerCache.getThermometers()) {
            CurrentTemperatureCanvas currentTemperatureCanvas = new CurrentTemperatureCanvas(context, thermometer);
            currentTemperatureCanvas.setLayoutParams(new ViewGroup.LayoutParams(-1, 300));
            UiUtil.setupTemperatureCanvas(this, currentTemperatureCanvas);
            linearLayout.addView(currentTemperatureCanvas);
            thermometerCanvas.add(currentTemperatureCanvas);
            linearLayout.postInvalidate();
        }
    }
}
