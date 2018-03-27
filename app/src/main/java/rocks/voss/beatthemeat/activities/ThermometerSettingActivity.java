package rocks.voss.beatthemeat.activities;

import android.app.Activity;
import android.os.Bundle;

import rocks.voss.beatthemeat.fragments.TemperatureRangeFragment;

public class ThermometerSettingActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int id = getIntent().getIntExtra("rocks.voss.beatthemeat.widgets.ThermometerCanvas.id", 0);
        TemperatureRangeFragment temperatureRangeFragment = new TemperatureRangeFragment();
        temperatureRangeFragment.setId(id);
        getFragmentManager().beginTransaction().replace(android.R.id.content, temperatureRangeFragment).commit();
    }
}
