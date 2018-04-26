package rocks.voss.beatthemeat.activities;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;

import lombok.Getter;
import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.R;
import rocks.voss.beatthemeat.services.HistoryTemperatureService;
import rocks.voss.beatthemeat.ui.HistoryTemperatureCanvas;
import rocks.voss.beatthemeat.utils.UiUtil;

public class HistoryActivity extends AppCompatActivity {

    @Getter
    private static HistoryTemperatureCanvas canvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        int id = getIntent().getIntExtra(Constants.THERMOMETER_CANVAS_ID, 0);

        ConstraintLayout constraintLayout = findViewById(R.id.constraintlayout);
        canvas = new HistoryTemperatureCanvas(this, id);
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
}
