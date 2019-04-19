package rocks.voss.beatthemeat.activities;

import android.app.Activity;
import android.os.Bundle;

import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.database.probe.ThermometerCache;
import rocks.voss.beatthemeat.fragments.ThermometerSettingsFragment;

public class ThermometerSettingsActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int thermometerId = getIntent().getIntExtra(Constants.THERMOMETER_CANVAS_ID, -1);
        ThermometerSettingsFragment thermometerSettingsFragment = new ThermometerSettingsFragment();
        thermometerSettingsFragment.setThermometer(ThermometerCache.getThermometerById(thermometerId));
        getFragmentManager().beginTransaction().replace(android.R.id.content, thermometerSettingsFragment).commit();
    }
}
