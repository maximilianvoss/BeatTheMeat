package rocks.voss.beatthemeat.activities;

import android.app.Activity;
import android.os.Bundle;

import rocks.voss.beatthemeat.fragments.ThermometerSettingsFragment;

public class ThermometerSettingsActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int id = getIntent().getIntExtra("rocks.voss.beatthemeat.widgets.ThermometerCanvas.id", 0);
        ThermometerSettingsFragment thermometerSettingsFragment = new ThermometerSettingsFragment();
        thermometerSettingsFragment.setId(id);
        getFragmentManager().beginTransaction().replace(android.R.id.content, thermometerSettingsFragment).commit();
    }
}
