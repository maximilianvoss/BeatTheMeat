package rocks.voss.beatthemeat.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import rocks.voss.beatthemeat.R;
import rocks.voss.beatthemeat.services.DataCollectionService;
import rocks.voss.beatthemeat.ui.ThermometerCanvas;

public class MainActivity extends AppCompatActivity {

    public static final String NUMBER_OF_THERMOMETERS = "numberOfThermometers";
    @Getter
    private static List<ThermometerCanvas> thermometers = new ArrayList<>();

    private Context context;
    private LinearLayout linearLayout;
    private static SharedPreferences sharedPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        DataCollectionService.schedule(this);

        setContentView(R.layout.activity_main);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollview);

        linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
        linearLayout.setBackgroundColor(Color.GREEN);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(linearLayout);

        int numberOfThermometers = sharedPref.getInt(NUMBER_OF_THERMOMETERS, 0);
        thermometers.clear();
        for (int i = 0; i < numberOfThermometers; i++) {
            ThermometerCanvas thermometerCanvas = new ThermometerCanvas(context, i);
            thermometerCanvas.setLayoutParams(new ViewGroup.LayoutParams(-1, 300));
            setupThermometerCanvas(thermometerCanvas);
            linearLayout.addView(thermometerCanvas);
            thermometers.add(thermometerCanvas);
            linearLayout.postInvalidate();
        }

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

        int numberOfThermometers = sharedPref.getInt(NUMBER_OF_THERMOMETERS, 0);

        for (int i = 0; i < numberOfThermometers; i++) {
            ThermometerCanvas thermometerCanvas = new ThermometerCanvas(context, i);
            thermometerCanvas.setLayoutParams(new ViewGroup.LayoutParams(-1, 300));
            setupThermometerCanvas(thermometerCanvas);
            linearLayout.addView(thermometerCanvas);
            linearLayout.postInvalidate();
        }
        linearLayout.postInvalidate();
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
}
