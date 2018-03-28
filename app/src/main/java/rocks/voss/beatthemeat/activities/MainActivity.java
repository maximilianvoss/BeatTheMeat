package rocks.voss.beatthemeat.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import rocks.voss.beatthemeat.R;
import rocks.voss.beatthemeat.services.DataCollectionService;
import rocks.voss.beatthemeat.services.NotficationSoundService;
import rocks.voss.beatthemeat.ui.ThermometerCanvas;
import rocks.voss.beatthemeat.utils.TemperatureUtil;

public class MainActivity extends AppCompatActivity {

    public static final String NUMBER_OF_THERMOMETERS = "numberOfThermometers";
    @Getter
    private static List<ThermometerCanvas> thermometers = new ArrayList<>();

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

        DataCollectionService.schedule(this);

        setContentView(R.layout.activity_main);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollview);

        linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(linearLayout);

        thermometers.clear();
        fillLinearLayout(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
                editor.commit();
            }
        });
    }

    public void onResume() {
        super.onResume();
        linearLayout.removeAllViews();

        Intent notificationSoundServiceIntent = new Intent(context, NotficationSoundService.class);
        context.stopService(notificationSoundServiceIntent);

        fillLinearLayout(false);
    }

    public static void removeThermometer(int id) {
        thermometers.remove(id);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(NUMBER_OF_THERMOMETERS, thermometers.size());
        editor.commit();
    }

    @Override
    public void onDestroy() {
        DataCollectionService.schedule(this);
        super.onDestroy();
    }

    public static void refreshThermometers() {
        for (ThermometerCanvas thermometerCanvas : thermometers) {
            thermometerCanvas.postInvalidate();
        }
    }

    private void setupThermometerCanvas(ThermometerCanvas thermometerCanvas) {
        Paint paintBackground = new Paint();
        paintBackground.setColor(Color.parseColor("#ffffff"));
        thermometerCanvas.setColorBackground(paintBackground);

        Paint paintRed = new Paint();
        paintRed.setColor(Color.parseColor("#ef0000"));
        thermometerCanvas.setColorRed(paintRed);

        Paint paintYellow = new Paint();
        paintYellow.setColor(Color.parseColor("#e8a812"));
        thermometerCanvas.setColorYellow(paintYellow);

        Paint paintGreen = new Paint();
        paintGreen.setColor(Color.parseColor("#0cce23"));
        thermometerCanvas.setColorGreen(paintGreen);
    }


    private void fillLinearLayout(boolean addThermometerCanvas) {
        switchAlarm = new Switch(context);
        switchAlarm.setText("Enable Alarm");
        switchAlarm.setLayoutParams(new ViewGroup.LayoutParams(-1, 150));
        switchAlarm.setChecked(TemperatureUtil.isEnabled());
        switchAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                TemperatureUtil.setEnabled(b);
                if ( !b ) {
                    Intent notificationSoundServiceIntent = new Intent(context, NotficationSoundService.class);
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
            if ( addThermometerCanvas) {
                thermometers.add(thermometerCanvas);
            }
            linearLayout.postInvalidate();
        }
    }
}
