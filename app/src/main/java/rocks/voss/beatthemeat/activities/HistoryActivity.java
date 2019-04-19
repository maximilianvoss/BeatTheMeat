package rocks.voss.beatthemeat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import rocks.voss.androidutils.AndroidUtilsConstants;
import rocks.voss.androidutils.activities.ExportGoogleDriveActivity;
import rocks.voss.androidutils.database.ExportData;
import rocks.voss.androidutils.database.ExportDataSet;
import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.R;
import rocks.voss.beatthemeat.database.probe.Thermometer;
import rocks.voss.beatthemeat.database.probe.ThermometerCache;
import rocks.voss.beatthemeat.database.temperatures.TemperatureCache;
import rocks.voss.beatthemeat.services.HistoryTemperatureService;
import rocks.voss.beatthemeat.ui.HistoryTemperatureCanvas;
import rocks.voss.beatthemeat.utils.UiUtil;

public class HistoryActivity extends AppCompatActivity {

    private Thermometer thermometer;

    @Getter
    private static HistoryTemperatureCanvas canvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_history);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("  " + getSupportActionBar().getTitle());

        int thermometerId = getIntent().getIntExtra(Constants.THERMOMETER_CANVAS_ID, -1);
        thermometer = ThermometerCache.getThermometerById(thermometerId);

        ConstraintLayout constraintLayout = findViewById(R.id.constraintlayout);
        canvas = new HistoryTemperatureCanvas(this, thermometer);
        UiUtil.setupTemperatureCanvas(this, canvas);
        constraintLayout.addView(canvas);

        HistoryTemperatureService.schedule(this);
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        HistoryTemperatureService.schedule(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        HistoryTemperatureService.schedule(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        HistoryTemperatureService.cancelJob(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HistoryTemperatureService.cancelJob(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.historymenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.upload:
                intent = new Intent(this, ExportGoogleDriveActivity.class);

                ExportData exportData = new ExportData();

                List<String> headers = new ArrayList<>(2);
                headers.add("Time");
                headers.add("Temperature");
                exportData.setHeader(headers);
                exportData.setDataSets((List<ExportDataSet>) (List<?>) TemperatureCache.getTemperatures(thermometer.id));

                intent.putExtra(AndroidUtilsConstants.EXPORT_GOOGLE_DRIVE_ACTIVITY_EXPORT_DATA, exportData);
                intent.putExtra(AndroidUtilsConstants.EXPORT_GOOGLE_DRIVE_ACTIVITY_EXPORT_FILE_NAME, "temperatures-%1$s.csv");
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
